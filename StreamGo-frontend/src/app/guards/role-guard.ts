import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Auth } from '../services/auth';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {

    const expectedRole = route.data['role'];
    const userRole = this.authService.getRole();

    // si no hay token
    if (!userRole) {
      this.router.navigate(['/login']);
      return false;
    }

    // si no coincide rol
    if (userRole !== expectedRole) {

      if (userRole === 'ADMIN') {
        this.router.navigate(['/admin/home']);
      } else {
        this.router.navigate(['/client/home']);
      }

      return false;
    }

    return true;
  }
}