import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Auth } from '../services/auth';

@Injectable({
  providedIn: 'root'
})
export class PublicGuard implements CanActivate {

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  canActivate(): boolean {

    if (!this.authService.isLoggedIn()) {
      return true; // puede entrar a login/register/home público
    }

    const role = this.authService.getRole();

    if (role === 'ADMIN') {
      this.router.navigate(['/admin/home']);
    } else {
      this.router.navigate(['/client/home']);
    }

    return false;
  }
}