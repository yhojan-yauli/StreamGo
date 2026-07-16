import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { VideoPlayer } from '../../componentes/video-player/video-player';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { SuscripcionStatusService } from '../../services/suscripcion-status';
import { CalificacionesService } from '../../services/calificaciones';
import { urlCompleta } from '../../services/api';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reproducir-client',
  imports: [CommonModule, RouterLink, NavbarClient, VideoPlayer, FormsModule],
  templateUrl: './reproducir-client.html',
  styleUrl: './reproducir-client.scss'
})
export class ReproducirClient implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private contenidoService = inject(ContenidoClienteService);
  private suscripcionService = inject(SuscripcionStatusService);
  private calificacionesService = inject(CalificacionesService);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild('videoPlayer') videoPlayer!: any;

  contenidoId = 0;
  contenido: any = null;
  reproduccion: any = null;
  relacionados: any[] = [];
  cargando = false;
  cargandoRelacionados = false;
  error = '';
  tieneSuscripcion = false;
  horasRestantes = 0;
  bloqueadoPorSuscripcion = false;
  contenidoEsActivo = false;

  calificacionUsuario: number = 0;
  calificacionPromedio: number = 0;
  totalCalificaciones: number = 0;
  comentarioUsuario: string = '';
  mostrarModalCalificacion = false;
  calificando = false;

  private autoSaveInterval: any = null;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const nuevoId = Number(params.get('id'));
      
      if (this.contenidoId !== nuevoId) {
        this.limpiarEstado();
        this.contenidoId = nuevoId;
      }
      
      this.cargarContenidoDesdeStorage();
      this.verificarSuscripcionYReproducir();
      this.cargarRelacionados();
      this.cargarCalificacion();
    });
  }

  ngOnDestroy(): void {
    if (this.autoSaveInterval) {
      clearInterval(this.autoSaveInterval);
    }
  }

  limpiarEstado(): void {
    this.contenido = null;
    this.reproduccion = null;
    this.error = '';
    this.bloqueadoPorSuscripcion = false;
    this.cargando = false;
    if (this.autoSaveInterval) {
      clearInterval(this.autoSaveInterval);
      this.autoSaveInterval = null;
    }
  }

  cargarContenidoDesdeStorage(): void {
    const guardado = localStorage.getItem('clientContenido');
    if (guardado) {
      try {
        const parsed = JSON.parse(guardado);
        if (parsed?.id === this.contenidoId) {
          this.contenido = parsed;
        } else {
          const encontrado = this.relacionados.find(r => r.id === this.contenidoId);
          if (encontrado) {
            this.contenido = encontrado;
            localStorage.setItem('clientContenido', JSON.stringify(encontrado));
          } else {
            this.contenido = null;
          }
        }
        this.contenidoEsActivo = this.contenido?.estado === 'ACTIVO';
      } catch (e) {
        this.contenido = null;
        this.contenidoEsActivo = false;
      }
    } else {
      this.contenido = null;
      this.contenidoEsActivo = false;
    }
  }

  verificarSuscripcionYReproducir(): void {
    if (!this.contenidoId) {
      this.error = 'Contenido no válido.';
      return;
    }

    this.cargando = true;
    this.error = '';
    this.bloqueadoPorSuscripcion = false;

    if (!this.contenido) {
      this.contenidoService.listar().subscribe({
        next: (data) => {
          const encontrado = Array.isArray(data) ? data.find(c => c.id === this.contenidoId) : null;
          if (encontrado) {
            this.contenido = encontrado;
            this.contenidoEsActivo = this.contenido?.estado === 'ACTIVO';
            localStorage.setItem('clientContenido', JSON.stringify(encontrado));
            this.continuarVerificacion();
          } else {
            this.error = 'Contenido no encontrado.';
            this.cargando = false;
            this.cdr.detectChanges();
          }
        },
        error: () => {
          this.continuarVerificacion();
        }
      });
    } else {
      this.continuarVerificacion();
    }
  }

  continuarVerificacion(): void {
    this.suscripcionService.verificarSuscripcion().subscribe({
      next: (data) => {
        this.tieneSuscripcion = data?.horasRestantes > 0;
        this.horasRestantes = data?.horasRestantes || 0;

        if (this.contenidoEsActivo && !this.tieneSuscripcion) {
          this.bloqueadoPorSuscripcion = true;
          this.error = 'Este contenido requiere una suscripción activa. Adquiere un plan para verlo.';
          this.cargando = false;
          this.cdr.detectChanges();
          return;
        }

        this.reproducir();
      },
      error: () => {
        this.tieneSuscripcion = false;
        if (this.contenidoEsActivo) {
          this.bloqueadoPorSuscripcion = true;
          this.error = 'Este contenido requiere una suscripción activa. Adquiere un plan para verlo.';
          this.cargando = false;
          this.cdr.detectChanges();
        } else {
          this.reproducir();
        }
      }
    });
  }

  get videoUrl(): string {
    if (this.bloqueadoPorSuscripcion) return '';
    return this.reproduccion?.videoUrl || this.contenido?.videoUrl || '';
  }

  get mostrarVideo(): boolean {
    return !this.bloqueadoPorSuscripcion && !!this.videoUrl;
  }

  get mostrarMensajeSuscripcion(): boolean {
    return this.bloqueadoPorSuscripcion;
  }

  reproducir(): void {
    if (!this.contenidoId) {
      this.error = 'Contenido no válido.';
      this.cargando = false;
      return;
    }
    
    this.contenidoService.reproducirCliente(this.contenidoId).subscribe({
      next: data => {
        this.reproduccion = data;
        if (data && !this.contenido) {
          this.contenido = data;
          this.contenidoEsActivo = this.contenido?.estado === 'ACTIVO';
          localStorage.setItem('clientContenido', JSON.stringify(data));
        }
        this.cargando = false;
        this.cdr.detectChanges();

        if (this.autoSaveInterval) {
          clearInterval(this.autoSaveInterval);
        }
        this.autoSaveInterval = setInterval(() => {
          this.guardarProgreso();
        }, 5000);
      },
      error: err => {
        console.error('Error al reproducir:', err);
        const mensaje = err.error?.mensaje || err.error?.message || '';
        if (mensaje.includes('suscripción') || mensaje.includes('suscripcion')) {
          this.bloqueadoPorSuscripcion = true;
          this.error = 'Este contenido requiere una suscripción activa. Adquiere un plan para verlo.';
        } else {
          this.error = err.error?.mensaje || 'No se pudo reproducir este contenido.';
        }
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardarProgreso(): void {
    if (!this.videoPlayer?.videoEl) return;
    const video = this.videoPlayer.videoEl;
    const progreso = Math.floor(video.currentTime);
    const completado = video.currentTime >= video.duration - 1;

    this.contenidoService.actualizarProgreso(this.contenidoId, progreso, completado).subscribe({
      next: () => {},
      error: (err) => console.error('Error guardando progreso:', err)
    });
  }

  // ===== CALIFICACIONES =====
  cargarCalificacion(): void {
    this.calificacionesService.obtenerCalificacion(this.contenidoId).subscribe({
      next: (data) => {
        if (data?.puntaje) {
          this.calificacionUsuario = data.puntaje;
          this.comentarioUsuario = data.comentario || '';
        }
        this.calificacionPromedio = data?.promedioCalificacion || 0;
        this.totalCalificaciones = data?.totalCalificaciones || 0;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  abrirModalCalificacion(): void {
    this.mostrarModalCalificacion = true;
  }

  cerrarModalCalificacion(): void {
    this.mostrarModalCalificacion = false;
  }

  enviarCalificacion(): void {
    if (!this.calificacionUsuario || this.calificacionUsuario < 1 || this.calificacionUsuario > 5) {
      this.error = 'Selecciona una calificación de 1 a 5 estrellas.';
      return;
    }

    this.calificando = true;
    this.calificacionesService.calificar(this.contenidoId, this.calificacionUsuario, this.comentarioUsuario).subscribe({
      next: (data) => {
        this.calificacionPromedio = data.promedioCalificacion;
        this.totalCalificaciones = data.totalCalificaciones;
        this.mostrarModalCalificacion = false;
        this.calificando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error calificando:', err);
        this.error = 'No se pudo guardar la calificación.';
        this.calificando = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ===== RELACIONADOS =====
  cargarRelacionados(): void {
    this.cargandoRelacionados = true;
    
    const categoria = this.contenido?.categoria;
    
    if (categoria) {
      this.contenidoService.porCategoria(categoria).subscribe({
        next: (data) => {
          const filtrados = Array.isArray(data) 
            ? data.filter(item => item.id !== this.contenidoId).slice(0, 6) 
            : [];
          
          if (filtrados.length < 3) {
            this.completarConRecomendados(filtrados);
          } else {
            this.relacionados = filtrados;
            this.cargandoRelacionados = false;
            this.cdr.detectChanges();
          }
        },
        error: () => {
          this.completarConRecomendados([]);
        }
      });
    } else {
      this.completarConRecomendados([]);
    }
  }

  completarConRecomendados(actuales: any[]): void {
    this.contenidoService.recomendados().subscribe({
      next: (data) => {
        const recomendados = Array.isArray(data) 
          ? data.filter(item => item.id !== this.contenidoId) 
          : [];
        
        const combinados = [...actuales];
        for (const item of recomendados) {
          if (!combinados.some(c => c.id === item.id)) {
            combinados.push(item);
          }
          if (combinados.length >= 6) break;
        }
        
        this.relacionados = combinados.slice(0, 6);
        this.cargandoRelacionados = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.relacionados = [];
        this.cargandoRelacionados = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ===== UTILIDADES =====
  titulo(): string {
    return this.reproduccion?.titulo || this.contenido?.titulo || 'Reproduciendo contenido';
  }

  descripcion(): string {
    return this.contenido?.descripcion || this.reproduccion?.mensaje || 'Contenido disponible en StreamGO.';
  }

  categoria(): string {
    return this.contenido?.categoria || this.reproduccion?.categoria || 'StreamGO';
  }

  tipoContenido(): string {
    return this.contenido?.tipoContenido || this.reproduccion?.tipoContenido || 'Contenido';
  }

  imagen(item: any): string {
    return urlCompleta(item?.bannerUrl || item?.imagenUrl || '/background.png');
  }

  verRelacionado(item: any): void {
    if (!item?.id) return;
    localStorage.setItem('clientContenido', JSON.stringify(item));
    this.router.navigate(['/client/reproducir', item.id]);
  }

  irAHome(): void {
    this.router.navigate(['/client/home']);
  }

  get estrellas(): number[] {
    return [1, 2, 3, 4, 5];
  }
}