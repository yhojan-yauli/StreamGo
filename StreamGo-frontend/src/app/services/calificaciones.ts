import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CalificacionesService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  calificar(contenidoId: number, puntaje: number, comentario?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/calificaciones/${contenidoId}`, {
      puntaje,
      comentario: comentario || ''
    });
  }

  obtenerCalificacion(contenidoId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/calificaciones/${contenidoId}`);
  }
}