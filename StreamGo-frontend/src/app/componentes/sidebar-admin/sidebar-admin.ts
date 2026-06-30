import { Component, Renderer2, inject, OnInit } from '@angular/core';
import { Auth } from '../../services/auth';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-admin',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.scss',
})
export class SidebarAdmin implements OnInit {

  sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
  sidebarAbierto = true;
  isMobile = false;

  private renderer = inject(Renderer2);

  constructor(
    private authService: Auth,
    private router: Router
  ) {
    if (window.innerWidth <= 768) {
      this.isMobile = true;
      this.sidebarAbierto = false;
      this.sidebarCollapsed = true;
    }
  }

  ngOnInit(): void {
    if (this.sidebarCollapsed) {
      this.renderer.addClass(document.body, 'sidebar-collapsed');
    }
  }

  get isCollapsible(): boolean {
    return !this.isMobile;
  }

  toggleCollapse(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    localStorage.setItem('sidebarCollapsed', String(this.sidebarCollapsed));
    if (this.sidebarCollapsed) {
      this.renderer.addClass(document.body, 'sidebar-collapsed');
    } else {
      this.renderer.removeClass(document.body, 'sidebar-collapsed');
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
