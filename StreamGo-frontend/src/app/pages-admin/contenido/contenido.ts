import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpEventType } from '@angular/common/http';
import { Subscription, timeout } from 'rxjs';
import { ContenidoService } from '../../services/contenido';
import { AlertService } from '../../componentes/ui/alert/alert.service';
import { FileUploadComponent } from '../../componentes/ui/file-upload/file-upload';
import { urlCompleta } from '../../services/api';

@Component({
  selector: 'app-contenido',
  imports: [CommonModule, FormsModule, FileUploadComponent],
  templateUrl: './contenido.html',
  styleUrl: './contenido.scss',
})
export class Contenido implements OnInit {
  urlCompleta = urlCompleta;
  private contenidoService = inject(ContenidoService);
  private alertService = inject(AlertService);
  private cdr = inject(ChangeDetectorRef);

  contenidos: any[] = [];
  cargando = false;
  mostrarFormulario = false;
  modoEdicion = false;
  contenidoId: number | null = null;
  guardando = false;
  uploadProgress: number | null = null;
  private uploadSub: Subscription | null = null;
  pasoActual = 1;
  pasosCompletados = { 1: false, 2: false, 3: false };

  contenidoForm: any = {
    titulo: '', descripcion: '', categoria: '', tipoContenido: 'PELICULA',
    imagenUrl: '', bannerUrl: '', videoUrl: '',
    fechaEstreno: '', duracionMinutos: 0,
    recomendado: false, tendencia: false, estado: 'ACTIVO',
  };

  imagenFile: File | null = null;
  bannerFile: File | null = null;
  videoFile: File | null = null;
  imagenPreviewUrl: string | null = null;
  bannerPreviewUrl: string | null = null;

  ngOnInit(): void { this.cargarContenidos(); }

  cargarContenidos(): void {
    this.cargando = true;
    this.contenidoService.listar().subscribe({
      next: data => {
        this.contenidos = Array.isArray(data) ? [...data] : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.contenidos = [];
        this.cargando = false;
        this.alertService.error('Error al cargar contenidos');
        this.cdr.detectChanges();
      },
    });
  }

  nuevoContenido(): void {
    this.modoEdicion = false;
    this.mostrarFormulario = true;
    this.contenidoId = null;
    this.pasoActual = 1;
    this.pasosCompletados = { 1: false, 2: false, 3: false };
    this.resetForm();
    this.limpiarFiles();
    this.cdr.detectChanges();
  }

  cerrarModal(): void {
    this.cancelarSubida();
    this.mostrarFormulario = false;
    this.guardando = false;
    this.uploadProgress = null;
    this.limpiarFiles();
    this.cdr.detectChanges();
  }

  cancelarSubida(): void {
    if (this.uploadSub) {
      this.uploadSub.unsubscribe();
      this.uploadSub = null;
    }
    if (this.guardando) {
      this.alertService.warning('Subida cancelada');
      this.guardando = false;
      this.uploadProgress = null;
      this.cdr.detectChanges();
    }
  }

  siguientePaso(): void {
    this.pasosCompletados[this.pasoActual as keyof typeof this.pasosCompletados] = true;
    if (this.pasoActual < 3) this.pasoActual++;
    this.cdr.detectChanges();
  }

  anteriorPaso(): void {
    if (this.pasoActual > 1) this.pasoActual--;
    this.cdr.detectChanges();
  }

  esUltimoPaso(): boolean { return this.pasoActual === 3; }

  pasoCompletado(n: number): boolean {
    return this.pasosCompletados[n as keyof typeof this.pasosCompletados];
  }

  pasoActivo(n: number): boolean { return this.pasoActual === n; }

  irAPaso(n: number): void {
    if (this.guardando) return;
    if (this.pasoCompletado(n) || n === this.pasoActual || n === this.pasoActual + 1) {
      this.pasoActual = n;
      this.cdr.detectChanges();
    }
  }

  editar(contenido: any): void {
    this.modoEdicion = true;
    this.mostrarFormulario = true;
    this.contenidoId = contenido.id;
    this.pasoActual = 1;
    this.pasosCompletados = { 1: false, 2: false, 3: false };
    this.limpiarFiles();
    this.contenidoForm = {
      titulo: contenido.titulo, descripcion: contenido.descripcion,
      categoria: contenido.categoria, tipoContenido: contenido.tipoContenido,
      imagenUrl: contenido.imagenUrl, bannerUrl: contenido.bannerUrl,
      videoUrl: contenido.videoUrl, fechaEstreno: contenido.fechaEstreno,
      duracionMinutos: contenido.duracionMinutos,
      recomendado: contenido.recomendado, tendencia: contenido.tendencia,
      estado: contenido.estado,
    };
    this.cdr.detectChanges();
  }

  onImagenSelect(files: FileList): void {
    const file = files[0];
    if (file) {
      this.imagenFile = file;
      this.limpiarUrl('imagen');
      this.imagenPreviewUrl = URL.createObjectURL(file);
    }
  }

  onBannerSelect(files: FileList): void {
    const file = files[0];
    if (file) {
      this.bannerFile = file;
      this.limpiarUrl('banner');
      this.bannerPreviewUrl = URL.createObjectURL(file);
    }
  }

  onVideoSelect(files: FileList): void {
    const file = files[0];
    if (file) this.videoFile = file;
  }

  private sanitizarDatos(): any {
    return {
      ...this.contenidoForm,
      fechaEstreno: this.contenidoForm.fechaEstreno || null,
      duracionMinutos: this.contenidoForm.duracionMinutos || null,
      imagenUrl: this.contenidoForm.imagenUrl || null,
      bannerUrl: this.contenidoForm.bannerUrl || null,
      videoUrl: this.contenidoForm.videoUrl || null,
    };
  }

  guardarContenido(): void {
    if (this.guardando) return;
    this.guardando = true;
    this.uploadProgress = 0;

    const data = this.sanitizarDatos();
    const tieneFiles = this.imagenFile || this.bannerFile || this.videoFile;

    if (tieneFiles) {
      const formData = new FormData();
      const dataBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
      formData.append('data', dataBlob);
      if (this.imagenFile) formData.append('imagen', this.imagenFile);
      if (this.bannerFile) formData.append('banner', this.bannerFile);
      if (this.videoFile) formData.append('video', this.videoFile);

      const obs = this.modoEdicion && this.contenidoId
        ? this.contenidoService.editarConArchivos(this.contenidoId, formData)
        : this.contenidoService.crearConArchivos(formData);

      this.uploadSub = obs.pipe(
        timeout(120000)
      ).subscribe({
        next: event => {
          if (event.type === HttpEventType.UploadProgress && event.total) {
            this.uploadProgress = Math.round((event.loaded / event.total) * 100);
            this.cdr.detectChanges();
          } else if (event.type === HttpEventType.Response) {
            this.onGuardado(this.modoEdicion ? 'Contenido actualizado' : 'Contenido creado');
          }
        },
        error: err => {
          console.error('Error en subida:', err);
          this.alertService.error(err.name === 'TimeoutError' ? 'La subida tardó demasiado' : 'Error al guardar contenido');
          this.guardando = false;
          this.uploadProgress = null;
          this.uploadSub = null;
          this.cdr.detectChanges();
        },
      });
    } else {
      if (this.modoEdicion && this.contenidoId) {
        this.uploadSub = this.contenidoService.editar(this.contenidoId, data).subscribe({
          next: () => this.onGuardado('Contenido actualizado'),
          error: () => {
            this.alertService.error('Error al actualizar contenido');
            this.guardando = false;
            this.uploadProgress = null;
            this.cdr.detectChanges();
          },
        });
      } else {
        this.uploadSub = this.contenidoService.crear(data).subscribe({
          next: () => this.onGuardado('Contenido creado'),
          error: () => {
            this.alertService.error('Error al crear contenido');
            this.guardando = false;
            this.uploadProgress = null;
            this.cdr.detectChanges();
          },
        });
      }
    }
  }

  private onGuardado(mensaje: string): void {
    this.alertService.success(mensaje);
    this.guardando = false;
    this.uploadProgress = null;
    this.uploadSub = null;
    this.cerrarModal();
    this.cargarContenidos();
  }

  async eliminar(id: number): Promise<void> {
    const ok = await this.alertService.confirm('Eliminar contenido?');
    if (!ok) return;
    this.contenidoService.eliminar(id).subscribe({
      next: () => { this.alertService.success('Contenido eliminado'); this.cargarContenidos(); },
      error: () => this.alertService.error('Error al eliminar'),
    });
  }

  async desactivar(id: number): Promise<void> {
    const ok = await this.alertService.confirm('Desactivar contenido?');
    if (!ok) return;
    this.contenidoService.desactivar(id).subscribe({
      next: () => { this.alertService.success('Contenido desactivado'); this.cargarContenidos(); },
      error: () => this.alertService.error('Error al desactivar'),
    });
  }

  private resetForm(): void {
    this.contenidoForm = {
      titulo: '', descripcion: '', categoria: '', tipoContenido: 'PELICULA',
      imagenUrl: '', bannerUrl: '', videoUrl: '',
      fechaEstreno: '', duracionMinutos: 0,
      recomendado: false, tendencia: false, estado: 'ACTIVO',
    };
  }

  private limpiarFiles(): void {
    this.limpiarUrl('imagen');
    this.limpiarUrl('banner');
    this.imagenFile = null;
    this.bannerFile = null;
    this.videoFile = null;
  }

  private limpiarUrl(tipo: 'imagen' | 'banner'): void {
    const key = tipo === 'imagen' ? 'imagenPreviewUrl' : 'bannerPreviewUrl';
    const old = this[key];
    if (old) URL.revokeObjectURL(old);
    this[key] = null;
  }
}
