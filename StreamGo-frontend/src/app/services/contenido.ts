import { HttpClient, HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ContenidoService {

  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/admin/contenidos`;

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  crear(contenido: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, contenido);
  }

  crearConArchivos(formData: FormData): Observable<HttpEvent<any>> {
    return this.http.post<any>(`${this.apiUrl}/crear-con-archivos`, formData, {
      reportProgress: true,
      observe: 'events',
    });
  }

  editar(id: number, contenido: any): Observable<any> {
    return this.http.put<any>(
      `${this.apiUrl}/${id}/editar`,
      contenido
    );
  }

  editarConArchivos(id: number, formData: FormData): Observable<HttpEvent<any>> {
    return this.http.put<any>(`${this.apiUrl}/${id}/editar-con-archivos`, formData, {
      reportProgress: true,
      observe: 'events',
    });
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