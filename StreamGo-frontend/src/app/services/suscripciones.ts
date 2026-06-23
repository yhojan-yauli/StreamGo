import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SuscripcionesService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/admin/suscripciones';

  obtenerTodas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/todos`);
  }

  obtenerActivas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activas`);
  }

  obtenerVencidas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/vencidas`);
  }

  obtenerOrdenadas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ordenadas`);
  }

}