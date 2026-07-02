import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NoticiasService } from '../../services/noticias';
import { Auth } from '../../services/auth';
import { Noticia, NoticiaAdminFiltros, NoticiaRequest } from '../../models/noticia.model';

interface NoticiaAdminForm {
  titulo: string;
  contenido: string;
  trailer: string;
  reacciones: number;
  idUsuario?: number | null;
  idAutor?: number | null;
}

interface UsuarioToken {
  id?: number | string;
  userId?: number | string;
  idUsuario?: number | string;
}

@Component({
  selector: 'app-noticias-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './noticias-admin.html',
  styleUrl: './noticias-admin.scss',
})
export class NoticiasAdmin implements OnInit {
  private noticiasService = inject(NoticiasService);
  private auth = inject(Auth);
  private cdr = inject(ChangeDetectorRef);

  noticias: Noticia[] = [];
  cargando = false;
  guardando = false;
  modalVisible = false;
  modoEdicion = false;
  noticiaId: number | null = null;
  paginaActual = 1;
  elementosPorPagina = 6;
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
    reacciones: 0,
  };

  ngOnInit(): void {
    this.cargarNoticias();
  }

  get noticiasFiltradas(): Noticia[] {
    const termino = this.filtros.busqueda.trim().toLowerCase();
    let resultado = termino
      ? this.noticias.filter((noticia) => this.coincideBusqueda(noticia, termino))
      : [...this.noticias];

    if (this.filtros.estado === 'fijadas') {
      resultado = resultado.filter((noticia) => noticia.fijado);
    }

    if (this.filtros.estado === 'normales') {
      resultado = resultado.filter((noticia) => !noticia.fijado);
    }

    return this.ordenarNoticias(resultado);
  }

  get noticiasPaginadas(): Noticia[] {
    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;
    return this.noticiasFiltradas.slice(inicio, inicio + this.elementosPorPagina);
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.noticiasFiltradas.length / this.elementosPorPagina));
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, index) => index + 1);
  }

  get inicioResultado(): number {
    if (this.noticiasFiltradas.length === 0) return 0;
    return (this.paginaActual - 1) * this.elementosPorPagina + 1;
  }

  get finResultado(): number {
    return Math.min(this.paginaActual * this.elementosPorPagina, this.noticiasFiltradas.length);
  }

  cargarNoticias(): void {
    this.cargando = true;
    this.error = '';

    this.noticiasService.listarOrdenadas().subscribe({
      next: (data) => {
        this.noticias = Array.isArray(data) ? [...data] : [];
        this.paginaActual = 1;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.noticiasService.listar().subscribe({
          next: (data) => {
            this.noticias = Array.isArray(data) ? [...data] : [];
            this.paginaActual = 1;
            this.cargando = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error(err);
            this.noticias = [];
            this.cargando = false;
            this.error = 'No se pudieron cargar las noticias.';
            this.cdr.detectChanges();
          },
        });
      },
    });
  }

  aplicarFiltros(): void {
    this.paginaActual = 1;
  }

  cambiarPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

  nuevaNoticia(): void {
    this.modoEdicion = false;
    this.noticiaId = null;
    this.modalVisible = true;
    this.noticiaForm = { titulo: '', contenido: '', trailer: '', reacciones: 0 };
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

    const idAdmin = this.obtenerIdAdmin();

    if (!idAdmin && !this.noticiaForm.idAutor) {
      this.error = 'No se pudo identificar el ID del administrador autenticado.';
      return;
    }

    const idAutor = Number(this.noticiaForm.idAutor || idAdmin);
    const request: NoticiaRequest = {
      idUsuario: Number(this.noticiaForm.idUsuario || idAutor),
      idAutor,
      titulo: this.noticiaForm.titulo.trim(),
      contenido: this.noticiaForm.contenido.trim(),
      trailer: this.noticiaForm.trailer?.trim() || '',
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

  trackByNoticia(_: number, noticia: Noticia): number {
    return noticia.idPost;
  }

  private coincideBusqueda(noticia: Noticia, termino: string): boolean {
    return [
      noticia.titulo,
      noticia.contenido,
      noticia.autorNombre,
    ].some((valor) => (valor ?? '').toLowerCase().includes(termino));
  }

  private obtenerIdAdmin(): number | null {
    const usuario = this.auth.getUser() as UsuarioToken | null;
    const posibleId = usuario?.id ?? usuario?.userId ?? usuario?.idUsuario;
    const id = Number(posibleId);
    return Number.isFinite(id) && id > 0 ? id : null;
  }

  private ordenarNoticias(noticias: Noticia[]): Noticia[] {
    if (this.filtros.orden === 'reacciones') {
      return noticias.sort((a, b) => (b.reacciones ?? 0) - (a.reacciones ?? 0));
    }

    if (this.filtros.orden === 'titulo') {
      return noticias.sort((a, b) => a.titulo.localeCompare(b.titulo));
    }

    return noticias.sort((a, b) => b.idPost - a.idPost);
  }
}
