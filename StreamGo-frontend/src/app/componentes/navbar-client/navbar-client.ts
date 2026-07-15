import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { Auth } from '../../services/auth';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar-client',
  imports: [RouterLink, RouterLinkActive, NgIf],
  templateUrl: './navbar-client.html',
  styleUrl: './navbar-client.scss',
})
export class NavbarClient {
  usuario: any;
  menuVisible = false;
  menuMobileAbierto = false;

  constructor(
    private authService: Auth,
    private router: Router
  ) {
    this.usuario = this.authService.getUser();
  }

  nombre(): string {
    return this.usuario?.nombre || this.usuario?.name || this.usuario?.email || this.usuario?.sub || 'Usuario';
  }

  email(): string {
    return this.usuario?.email || this.usuario?.sub || '';
  }

  inicial(): string {
    const nombre = this.usuario?.nombre || this.usuario?.name || '';
    if (nombre) return nombre.charAt(0).toUpperCase();
    const email = this.usuario?.email || this.usuario?.sub || '';
    return email ? email.charAt(0).toUpperCase() : '';
  }

  toggleMenu(): void {
    this.menuVisible = !this.menuVisible;
  }

  toggleMenuMobile(): void {
    this.menuMobileAbierto = !this.menuMobileAbierto;
  }

  closeMenuMobile(): void {
    this.menuMobileAbierto = false;
  }

  irAMiCuenta(): void {
    this.menuVisible = false;
    this.router.navigate(['/client/mi-cuenta']);
  }

  logout(): void {
    this.menuVisible = false;
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
