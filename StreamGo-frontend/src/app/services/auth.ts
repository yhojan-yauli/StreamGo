import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';


@Injectable({
  providedIn: 'root',
})
export class Auth {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data);
  }

   saveToken(token: string) {
    localStorage.setItem('token', token);
  }
  getToken() {
    return localStorage.getItem('token');
  }

  getDecodedToken(): any {
    const token = this.getToken();
    if (!token) return null;

    return jwtDecode(token);
  }

   logout() {
    localStorage.removeItem('token');
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    
  

    const decoded: any = jwtDecode(token);
    return decoded.rol; //  clave del backend
  }

   isTokenExpired(): boolean {
    const decoded = this.getDecodedToken();
    if (!decoded) return true;

    const now = Math.floor(Date.now() / 1000);
    return decoded.exp < now;
  }

    //isLoggedIn(): boolean {
    //const token = this.getToken();
    //return !!token && !this.isTokenExpired();
  //}
  isLoggedIn(): boolean {
  const token = localStorage.getItem('token');
  return !!token;
}
  
  getUser(): any {
  return this.getDecodedToken();
}



  //registro ==============================

  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }
}
