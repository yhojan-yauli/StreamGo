import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PagosService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  crearPago(planId: number, metodoPago: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/payments/create`, {
      planId,
      metodoPago
    });
  }
}
