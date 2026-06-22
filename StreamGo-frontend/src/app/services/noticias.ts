import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NoticiasService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/noticias`);
  }

  listarOrdenadas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/noticias/ordenadas`);
  }

  obtener(idPost: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/noticias/${idPost}`);
  }

  crear(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/noticias`, data);
  }

  actualizar(idPost: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/noticias/${idPost}`, data);
  }

  eliminar(idPost: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/noticias/${idPost}`, {
      responseType: 'text'
    });
  }

  reaccionar(idPost: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/noticias/${idPost}/reaccionar`, {});
  }

  fijar(idPost: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/noticias/${idPost}/fijar`, {});
  }
}
