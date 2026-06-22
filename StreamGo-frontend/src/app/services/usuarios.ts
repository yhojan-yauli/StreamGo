import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/admin/clientes';

  listar(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

}