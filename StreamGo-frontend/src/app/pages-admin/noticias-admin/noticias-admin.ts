import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NoticiasService } from '../../services/noticias';
import { Noticia, NoticiaAdminFiltros, NoticiaRequest } from '../../models/noticia.model';

interface NoticiaAdminForm {
  titulo: string;
  contenido: string;
  trailer: string;
  portadaUrl: string;
  reacciones: number;
  idUsuario?: number | null;
  idAutor?: number | null;
}

@Component({
  selector: 'app-noticias-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './noticias-admin.html',
  styleUrl: './noticias-admin.scss',
})
export class NoticiasAdmin implements OnInit {
  private noticiasService = inject(NoticiasService);
  private cdr = inject(ChangeDetectorRef);

  noticias: Noticia[] = [];
  cargando = false;
  guardando = false;
  modalVisible = false;
  modoEdicion = false;
  noticiaId: number | null = null;
  paginaActual = 1;
  elementosPorPagina = 6;
  totalElementos = 0;

  mensaje = '';
  error = '';

  filtros: NoticiaAdminFiltros = {
    busqueda: '',
    estado: 'todos',
    orden: 'recientes',
  };

  noticiaForm: NoticiaAdminForm = {
    titulo: '',
    contenido: '',
    trailer: '',
    portadaUrl: '',
    reacciones: 0,
  };

  ngOnInit(): void {
    this.cargarNoticias();
  }

  get noticiasPaginadas(): Noticia[] {
    return this.noticias;
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.totalElementos / this.elementosPorPagina));
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, index) => index + 1);
  }

  get inicioResultado(): number {
    if (this.totalElementos === 0) return 0;
    return (this.paginaActual - 1) * this.elementosPorPagina + 1;
  }

  get finResultado(): number {
    return Math.min(this.paginaActual * this.elementosPorPagina, this.totalElementos);
  }

  cargarNoticias(): void {
    this.cargando = true;
    this.error = '';

    this.noticiasService.buscarAdmin({
      search: this.filtros.busqueda.trim(),
      estado: this.filtros.estado,
      sort: this.filtros.orden,
      page: this.paginaActual - 1,
      size: this.elementosPorPagina,
    }).subscribe({
      next: (response) => {
        this.noticias = response.content ?? [];
        this.totalElementos = response.totalElements ?? 0;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.noticias = [];
        this.totalElementos = 0;
        this.cargando = false;
        this.error = 'No se pudieron cargar las noticias.';
        this.cdr.detectChanges();
      },
    });
  }

  aplicarFiltros(): void {
    this.paginaActual = 1;
    this.cargarNoticias();
  }

  cambiarPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
    this.cargarNoticias();
  }

  nuevaNoticia(): void {
    this.modoEdicion = false;
    this.noticiaId = null;
    this.modalVisible = true;
    this.noticiaForm = { titulo: '', contenido: '', trailer: '', portadaUrl: '', reacciones: 0 };
    this.cdr.detectChanges();
  }

  editarNoticia(noticia: Noticia): void {
    this.modoEdicion = true;
    this.noticiaId = noticia.idPost;
    this.modalVisible = true;
    this.noticiaForm = {
      titulo: noticia.titulo || '',
      contenido: noticia.contenido || '',
      trailer: noticia.trailer || '',
      portadaUrl: noticia.portadaUrl || '',
      reacciones: noticia.reacciones || 0,
      idUsuario: noticia.idUsuario || null,
      idAutor: noticia.idAutor || null,
    };
    this.cdr.detectChanges();
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.modoEdicion = false;
    this.noticiaId = null;
    this.cdr.detectChanges();
  }

  guardarNoticia(): void {
    if (!this.noticiaForm.titulo.trim() || !this.noticiaForm.contenido.trim()) {
      this.error = 'Completa el título y el contenido.';
      return;
    }

    const request: NoticiaRequest = {
      titulo: this.noticiaForm.titulo.trim(),
      contenido: this.noticiaForm.contenido.trim(),
      trailer: this.noticiaForm.trailer?.trim() || '',
      portadaUrl: this.noticiaForm.portadaUrl?.trim() || null,
      reacciones: Number(this.noticiaForm.reacciones || 0),
    };

    this.guardando = true;
    const obs = this.modoEdicion && this.noticiaId
      ? this.noticiasService.actualizar(this.noticiaId, request)
      : this.noticiasService.crear(request);

    obs.subscribe({
      next: () => {
        this.guardando = false;
        this.modalVisible = false;
        this.mensaje = this.modoEdicion
          ? 'Noticia actualizada correctamente.'
          : 'Noticia creada correctamente.';
        this.cargarNoticias();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.guardando = false;
        this.error = 'No se pudo guardar la noticia.';
        this.cdr.detectChanges();
      },
    });
  }

  fijarNoticia(noticia: Noticia): void {
    if (!noticia?.idPost) return;

    this.noticiasService.fijar(noticia.idPost).subscribe({
      next: () => {
        this.mensaje = 'Estado de fijado actualizado.';
        this.cargarNoticias();
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudo fijar la noticia.';
        this.cdr.detectChanges();
      },
    });
  }

  eliminarNoticia(noticia: Noticia): void {
    if (!noticia?.idPost || !confirm(`¿Eliminar la noticia "${noticia.titulo}"?`)) return;

    this.noticiasService.eliminar(noticia.idPost).subscribe({
      next: () => {
        this.mensaje = 'Noticia eliminada correctamente.';
        this.cargarNoticias();
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudo eliminar la noticia.';
        this.cdr.detectChanges();
      },
    });
  }

  abrirTrailer(url: string | null): void {
    if (!url) {
      this.error = 'Esta noticia no tiene tráiler.';
      return;
    }

    window.open(url, '_blank');
  }

  autor(noticia: Noticia): string {
    return noticia?.autorNombre || 'Administrador';
  }

  portada(noticia: Noticia): string | null {
    return this.noticiasService.mediaUrl(noticia.portadaUrl);
  }

  fecha(noticia: Noticia): string {
    return noticia.fechaCreacion
      ? new Date(noticia.fechaCreacion).toLocaleDateString('es-PE')
      : 'Sin fecha';
  }

  trackByNoticia(_: number, noticia: Noticia): number {
    return noticia.idPost;
  }

}
