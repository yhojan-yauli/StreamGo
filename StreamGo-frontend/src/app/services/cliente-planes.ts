import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ClientePlanesService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  listarPlanes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cliente/planes`);
  }

  obtenerPlan(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/planes/${id}`);
  }

  obtenerPlanPersonalizado(horas: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/planes/personalizado?arg0=${horas}`);
  }

  miSuscripcion(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/suscripciones/mi-suscripcion`);
  }

  verificarSuscripcion(): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/cliente/suscripciones/verificar`, {});
  }
}
