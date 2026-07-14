import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { NoticiasService } from '../../services/noticias';
import { Noticia, NoticiaFiltro } from '../../models/noticia.model';

@Component({ selector: 'app-noticias-client', imports: [CommonModule, FormsModule, NavbarClient], templateUrl: './noticias-client.html', styleUrl: './noticias-client.scss' })
export class NoticiasClient implements OnInit {
  private noticiasService = inject(NoticiasService);
  private cdr = inject(ChangeDetectorRef);

  noticias: Noticia[] = [];
  busqueda = '';
  cargando = false;
  reaccionandoId: number | null = null;
  filtroActivo: NoticiaFiltro = 'todas';
  mensaje = '';
  error = '';

  filtros: Array<{ valor: NoticiaFiltro; etiqueta: string }> = [
    { valor: 'todas', etiqueta: 'Todas' },
    { valor: 'destacadas', etiqueta: 'Destacadas' },
    { valor: 'populares', etiqueta: 'Populares' },
    { valor: 'recientes', etiqueta: 'Recientes' },
  ];

  ngOnInit(): void {
    this.cargarNoticias();
  }

  cargarNoticias(): void {
    this.cargando = true;
    this.error = '';
    this.mensaje = '';

    this.noticiasService.buscarPublicas({
      search: this.busqueda.trim(),
      estado: this.estadoBackend(),
      sort: this.ordenBackend(),
      page: 0,
      size: 50,
    }).subscribe({
      next: (response) => {
        this.noticias = response.content ?? [];
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
  }

  seleccionarFiltro(filtro: NoticiaFiltro): void {
    this.filtroActivo = filtro;
    this.cargarNoticias();
  }

  buscarNoticias(): void {
    this.cargarNoticias();
  }

  reaccionar(noticia: Noticia): void {
    if (!noticia?.idPost) return;

    this.error = '';
    this.reaccionandoId = noticia.idPost;

    this.noticiasService.reaccionar(noticia.idPost).subscribe({
      next: (data) => {
        const index = this.noticias.findIndex((item) => item.idPost === noticia.idPost);

        if (index >= 0) {
          this.noticias[index] = data;
          this.noticias = [...this.noticias];
        }

        this.reaccionandoId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.reaccionandoId = null;
        this.error = 'No se pudo registrar la reacción.';
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

  compartir(noticia: Noticia): void {
    const texto = `${noticia.titulo} - StreamGO`;
    const url = window.location.href;

    if (navigator.share) {
      navigator.share({ title: noticia.titulo, text: texto, url });
      return;
    }

    navigator.clipboard.writeText(`${texto} ${url}`);
    this.mensaje = 'Link copiado para compartir.';
    this.cdr.detectChanges();
  }

  autor(noticia: Noticia): string {
    return noticia?.autorNombre || 'Comunidad StreamGO';
  }

  portada(noticia: Noticia): string | null {
    return this.noticiasService.mediaUrl(noticia.portadaUrl);
  }

  fecha(noticia: Noticia): string {
    return noticia.fechaCreacion
      ? new Date(noticia.fechaCreacion).toLocaleDateString('es-PE')
      : '';
  }

  trackByNoticia(_: number, noticia: Noticia): number {
    return noticia.idPost;
  }

  private estadoBackend(): 'todos' | 'fijadas' | 'normales' {
    return this.filtroActivo === 'destacadas' ? 'fijadas' : 'todos';
  }

  private ordenBackend(): 'recientes' | 'reacciones' | 'titulo' {
    if (this.filtroActivo === 'populares') {
      return 'reacciones';
    }

    return 'recientes';
  }
}
