import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClientePlanesService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listarPlanes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cliente/planes`);
  }

  obtenerPlan(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/planes/${id}`);
  }

  obtenerPlanPersonalizado(horas: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/planes/personalizado?monto=${horas}`);
  }

  miSuscripcion(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/suscripciones/mi-suscripcion`);
  }

  verificarSuscripcion(): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/cliente/suscripciones/verificar`, {});
  }
}
