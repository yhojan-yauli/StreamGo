import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PeticionesAdminService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  ranking(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/peticiones/ranking`);
  }

  agregar(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/admin/peticiones/agregar`, data);
  }

  editar(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/peticiones/editar/${id}`, data);
  }

  desactivar(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/admin/peticiones/desactivar/${id}`, {});
  }
  listar(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/admin/peticiones/lista`);
  }
}
