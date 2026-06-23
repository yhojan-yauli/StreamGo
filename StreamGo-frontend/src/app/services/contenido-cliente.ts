import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContenidoClienteService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos`);
  }

  listarSuscriptor(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos/suscriptor`);
  }

  tendencias(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos/tendencias`);
  }

  recomendados(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos/recomendados`);
  }

  porCategoria(categoria: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos/categoria/${encodeURIComponent(categoria)}`);
  }

  buscar(titulo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/contenidos/buscar?titulo=${encodeURIComponent(titulo)}`);
  }

  historial(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/historial`);
  }

  reproducirCliente(contenidoId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/reproduccion/${contenidoId}`, {});
  }

  reproducirPublico(contenidoId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/public/reproduccion/${contenidoId}`, {});
  }

  miSuscripcion(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/suscripciones/mi-suscripcion`);
  }
}
