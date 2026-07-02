import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { NoticiasService } from '../../services/noticias';
import { Auth } from '../../services/auth';
import { Noticia, NoticiaRequest } from '../../models/noticia.model';

interface NoticiaForm {
  titulo: string;
  contenido: string;
  trailer: string;
}

interface UsuarioToken {
  id?: number | string;
  userId?: number | string;
  idUsuario?: number | string;
  sub?: number | string;
}

@Component({ selector: 'app-noticias-client', imports: [CommonModule, FormsModule, NavbarClient], templateUrl: './noticias-client.html', styleUrl: './noticias-client.scss' })
export class NoticiasClient implements OnInit {
  private noticiasService = inject(NoticiasService); private auth = inject(Auth); private cdr = inject(ChangeDetectorRef);
  noticias: Noticia[] = []; cargando = false; publicando = false; reaccionandoId: number | null = null; mostrarFormulario = false; mensaje = ''; error = ''; noticiaForm: NoticiaForm = { titulo: '', contenido: '', trailer: '' };
  ngOnInit(): void { this.cargarNoticias(); }
  cargarNoticias(): void { this.cargando = true; this.error = ''; this.mensaje = ''; this.noticiasService.listarOrdenadas().subscribe({ next: data => { this.noticias = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: () => { this.noticiasService.listar().subscribe({ next: data => { this.noticias = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error(err); this.noticias = []; this.cargando = false; this.error = 'No se pudieron cargar las noticias.'; this.cdr.detectChanges(); } }); } }); }
  abrirFormulario(): void { this.mostrarFormulario = true; this.mensaje = ''; this.error = ''; this.cdr.detectChanges(); }
  cerrarFormulario(): void { this.mostrarFormulario = false; this.limpiarFormulario(); this.cdr.detectChanges(); }
  publicarNoticia(): void { if (!this.noticiaForm.titulo.trim() || !this.noticiaForm.contenido.trim()) { this.error = 'Completa el título y el contenido.'; return; } const usuario = this.auth.getUser() as UsuarioToken | null; const idUsuario = usuario?.id || usuario?.userId || usuario?.idUsuario || usuario?.sub || 1; const request: NoticiaRequest = { idUsuario: Number(idUsuario) || 1, idAutor: Number(idUsuario) || 1, titulo: this.noticiaForm.titulo.trim(), contenido: this.noticiaForm.contenido.trim(), trailer: this.noticiaForm.trailer.trim(), reacciones: 0 }; this.publicando = true; this.noticiasService.crear(request).subscribe({ next: () => { this.publicando = false; this.mostrarFormulario = false; this.mensaje = 'Noticia publicada correctamente.'; this.limpiarFormulario(); this.cargarNoticias(); this.cdr.detectChanges(); }, error: err => { console.error(err); this.publicando = false; this.error = 'No se pudo publicar la noticia.'; this.cdr.detectChanges(); } }); }
  reaccionar(noticia: Noticia): void { if (!noticia?.idPost) return; this.reaccionandoId = noticia.idPost; this.noticiasService.reaccionar(noticia.idPost).subscribe({ next: data => { const index = this.noticias.findIndex(item => item.idPost === noticia.idPost); if (index >= 0) { this.noticias[index] = data; this.noticias = [...this.noticias]; } this.reaccionandoId = null; this.cdr.detectChanges(); }, error: err => { console.error(err); this.reaccionandoId = null; this.error = 'No se pudo registrar la reacción.'; this.cdr.detectChanges(); } }); }
  abrirTrailer(url: string | null): void { if (!url) { this.error = 'Esta noticia no tiene tráiler.'; return; } window.open(url, '_blank'); }
  compartir(noticia: Noticia): void { const texto = `${noticia.titulo} - StreamGO`; const url = window.location.href; if (navigator.share) { navigator.share({ title: noticia.titulo, text: texto, url }); return; } navigator.clipboard.writeText(`${texto} ${url}`); this.mensaje = 'Link copiado para compartir.'; this.cdr.detectChanges(); }
  limpiarFormulario(): void { this.noticiaForm = { titulo: '', contenido: '', trailer: '' }; }
  autor(noticia: Noticia): string { return noticia?.autorNombre || noticia?.usuarioNombre || 'Comunidad StreamGO'; }
}
