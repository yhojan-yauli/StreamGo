import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Planes {

  private apiUrl = `${environment.apiUrl}/admin/planes`;

  constructor(private http: HttpClient) {}

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  obtener(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  crear(plan: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, plan);
  }

  actualizar(id: number, plan: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, plan);
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
