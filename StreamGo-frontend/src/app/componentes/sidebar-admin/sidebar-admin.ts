import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-admin',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.scss',
})
export class SidebarAdmin {

  sidebarAbierto = true;

  constructor(
    private authService: Auth,
    private router: Router
  ) {
    if (window.innerWidth <= 768) {
      this.sidebarAbierto = false;
    }
  }

  toggleSidebar(): void {
    this.sidebarAbierto = !this.sidebarAbierto;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
