import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NoticiasService } from '../../services/noticias';
import { Auth } from '../../services/auth';
import { Noticia, NoticiaRequest } from '../../models/noticia.model';

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
  sub?: number | string;
}

@Component({ selector: 'app-noticias-admin', imports: [CommonModule, FormsModule], templateUrl: './noticias-admin.html', styleUrl: './noticias-admin.scss' })
export class NoticiasAdmin implements OnInit { private noticiasService = inject(NoticiasService); private auth = inject(Auth); private cdr = inject(ChangeDetectorRef); noticias: Noticia[] = []; cargando = false; guardando = false; modalVisible = false; modoEdicion = false; noticiaId: number | null = null; mensaje = ''; error = ''; noticiaForm: NoticiaAdminForm = { titulo: '', contenido: '', trailer: '', reacciones: 0 }; ngOnInit(): void { this.cargarNoticias(); } cargarNoticias(): void { this.cargando = true; this.noticiasService.listarOrdenadas().subscribe({ next: data => { this.noticias = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: () => { this.noticiasService.listar().subscribe({ next: data => { this.noticias = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error(err); this.noticias = []; this.cargando = false; this.error = 'No se pudieron cargar las noticias.'; this.cdr.detectChanges(); } }); } }); } nuevaNoticia(): void { this.modoEdicion = false; this.noticiaId = null; this.modalVisible = true; this.noticiaForm = { titulo: '', contenido: '', trailer: '', reacciones: 0 }; this.cdr.detectChanges(); } editarNoticia(noticia: Noticia): void { this.modoEdicion = true; this.noticiaId = noticia.idPost; this.modalVisible = true; this.noticiaForm = { titulo: noticia.titulo || '', contenido: noticia.contenido || '', trailer: noticia.trailer || '', reacciones: noticia.reacciones || 0, idUsuario: noticia.idUsuario || null, idAutor: noticia.idAutor || null }; this.cdr.detectChanges(); } cerrarModal(): void { this.modalVisible = false; this.modoEdicion = false; this.noticiaId = null; this.cdr.detectChanges(); } guardarNoticia(): void { if (!this.noticiaForm.titulo.trim() || !this.noticiaForm.contenido.trim()) { this.error = 'Completa el título y el contenido.'; return; } const usuario = this.auth.getUser() as UsuarioToken | null; const idAdmin = usuario?.id || usuario?.userId || usuario?.idUsuario || usuario?.sub || 1; const request: NoticiaRequest = { idUsuario: Number(this.noticiaForm.idUsuario || idAdmin) || 1, idAutor: Number(this.noticiaForm.idAutor || idAdmin) || 1, titulo: this.noticiaForm.titulo.trim(), contenido: this.noticiaForm.contenido.trim(), trailer: this.noticiaForm.trailer?.trim() || '', reacciones: Number(this.noticiaForm.reacciones || 0) }; this.guardando = true; const obs = this.modoEdicion && this.noticiaId ? this.noticiasService.actualizar(this.noticiaId, request) : this.noticiasService.crear(request); obs.subscribe({ next: () => { this.guardando = false; this.modalVisible = false; this.mensaje = this.modoEdicion ? 'Noticia actualizada correctamente.' : 'Noticia creada correctamente.'; this.cargarNoticias(); this.cdr.detectChanges(); }, error: err => { console.error(err); this.guardando = false; this.error = 'No se pudo guardar la noticia.'; this.cdr.detectChanges(); } }); } fijarNoticia(noticia: Noticia): void { if (!noticia?.idPost) return; this.noticiasService.fijar(noticia.idPost).subscribe({ next: () => { this.mensaje = 'Estado de fijado actualizado.'; this.cargarNoticias(); }, error: err => { console.error(err); this.error = 'No se pudo fijar la noticia.'; this.cdr.detectChanges(); } }); } eliminarNoticia(noticia: Noticia): void { if (!noticia?.idPost || !confirm(`¿Eliminar la noticia "${noticia.titulo}"?`)) return; this.noticiasService.eliminar(noticia.idPost).subscribe({ next: () => { this.mensaje = 'Noticia eliminada correctamente.'; this.cargarNoticias(); }, error: err => { console.error(err); this.error = 'No se pudo eliminar la noticia.'; this.cdr.detectChanges(); } }); } abrirTrailer(url: string | null): void { if (!url) { this.error = 'Esta noticia no tiene tráiler.'; return; } window.open(url, '_blank'); } autor(noticia: Noticia): string { return noticia?.autorNombre || noticia?.usuarioNombre || 'Administrador'; } }
