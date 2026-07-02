import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Noticia, NoticiaRequest } from '../models/noticia.model';

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

  obtener(idPost: number): Observable<Noticia> {
    return this.http.get<Noticia>(`${this.apiUrl}/noticias/${idPost}`);
  }

  crear(data: NoticiaRequest): Observable<Noticia> {
    return this.http.post<Noticia>(`${this.apiUrl}/admin/noticias`, data);
  }

  actualizar(idPost: number, data: NoticiaRequest): Observable<Noticia> {
    return this.http.put<Noticia>(`${this.apiUrl}/admin/noticias/${idPost}`, data);
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
}
