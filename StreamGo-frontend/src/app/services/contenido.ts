import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ContenidoService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/admin/contenidos';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  crear(contenido: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, contenido);
  }

  editar(id: number, contenido: any): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/${id}/editar`,
      contenido
    );
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/${id}`
    );
  }

  desactivar(id: number): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/${id}/desactivar`
    );
  }

}