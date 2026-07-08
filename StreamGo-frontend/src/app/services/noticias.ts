import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Noticia, NoticiaQueryParams, NoticiaRequest, PageResponse } from '../models/noticia.model';

@Injectable({
  providedIn: 'root'
})
export class NoticiasService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listar(): Observable<Noticia[]> {
    return this.http.get<Noticia[]>(`${this.apiUrl}/noticias`);
  }

  listarOrdenadas(): Observable<Noticia[]> {
    return this.http.get<Noticia[]>(`${this.apiUrl}/noticias/ordenadas`);
  }

  buscarPublicas(params: NoticiaQueryParams): Observable<PageResponse<Noticia>> {
    return this.http.get<PageResponse<Noticia>>(`${this.apiUrl}/noticias/buscar`, {
      params: this.buildParams(params),
    });
  }

  buscarAdmin(params: NoticiaQueryParams): Observable<PageResponse<Noticia>> {
    return this.http.get<PageResponse<Noticia>>(`${this.apiUrl}/admin/noticias`, {
      params: this.buildParams(params),
    });
  }

  obtener(idPost: number): Observable<Noticia> {
    return this.http.get<Noticia>(`${this.apiUrl}/noticias/${idPost}`);
  }

  crear(data: NoticiaRequest): Observable<Noticia> {
    return this.http.post<Noticia>(`${this.apiUrl}/admin/noticias`, data);
  }

  crearConPortada(data: NoticiaRequest, portada: File): Observable<Noticia> {
    return this.http.post<Noticia>(
      `${this.apiUrl}/admin/noticias`,
      this.buildNoticiaFormData(data, portada)
    );
  }

  actualizar(idPost: number, data: NoticiaRequest): Observable<Noticia> {
    return this.http.put<Noticia>(`${this.apiUrl}/admin/noticias/${idPost}`, data);
  }

  actualizarConPortada(idPost: number, data: NoticiaRequest, portada: File): Observable<Noticia> {
    return this.http.put<Noticia>(
      `${this.apiUrl}/admin/noticias/${idPost}`,
      this.buildNoticiaFormData(data, portada)
    );
  }

  eliminar(idPost: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/admin/noticias/${idPost}`, {
      responseType: 'text'
    });
  }

  reaccionar(idPost: number): Observable<Noticia> {
    return this.http.patch<Noticia>(`${this.apiUrl}/cliente/noticias/${idPost}/reaccionar`, {});
  }

  fijar(idPost: number): Observable<Noticia> {
    return this.http.patch<Noticia>(`${this.apiUrl}/admin/noticias/${idPost}/fijar`, {});
  }

  mediaUrl(url: string | null | undefined): string | null {
    if (!url) return null;

    if (/^https?:\/\//i.test(url)) {
      return url;
    }

    return `${this.apiUrl}${url.startsWith('/') ? url : `/${url}`}`;
  }

  private buildParams(params: NoticiaQueryParams): HttpParams {
    let httpParams = new HttpParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && `${value}`.trim() !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return httpParams;
  }

  private buildNoticiaFormData(data: NoticiaRequest, portada: File): FormData {
    const formData = new FormData();
    formData.append(
      'noticia',
      new Blob([JSON.stringify(data)], { type: 'application/json' })
    );
    formData.append('portada', portada);

    return formData;
  }
}
