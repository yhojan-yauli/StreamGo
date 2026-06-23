import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PeticionesClienteService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/peticiones/lista`);
  }

  elegir(contenidoVotableId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/peticiones/elegir`, {
      contenidoVotableId
    });
  }
}
