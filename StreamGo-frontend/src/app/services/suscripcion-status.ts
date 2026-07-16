import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SuscripcionStatusService {

  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;
  
  private suscripcionSubject = new BehaviorSubject<any>(null);
  suscripcion$ = this.suscripcionSubject.asObservable();

  verificarSuscripcion(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/cliente/suscripciones/mi-suscripcion`)
      .pipe(
        tap(data => this.suscripcionSubject.next(data)),
        catchError(() => {
          this.suscripcionSubject.next(null);
          return of(null);
        })
      );
  }

  getHorasRestantes(): number {
    const data = this.suscripcionSubject.value;
    return data?.horasRestantes || 0;
  }

  tieneSuscripcionActiva(): boolean {
    return this.getHorasRestantes() > 0;
  }
}