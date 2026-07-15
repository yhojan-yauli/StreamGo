import { Component, Renderer2, inject, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Auth } from '../../services/auth';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-admin',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.scss',
})
export class SidebarAdmin implements OnInit {

  sidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
  sidebarAbierto = true;
  isMobile = false;
  usuario: any;

  private renderer = inject(Renderer2);

  constructor(
    private authService: Auth,
    private router: Router
  ) {
    this.usuario = this.authService.getUser();
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

  @HostListener('window:resize')
  onResize(): void {
    const wasMobile = this.isMobile;
    this.isMobile = window.innerWidth <= 768;
    if (!wasMobile && this.isMobile) {
      this.sidebarAbierto = false;
      this.sidebarCollapsed = true;
      this.renderer.addClass(document.body, 'sidebar-collapsed');
    } else if (wasMobile && !this.isMobile) {
      this.sidebarAbierto = true;
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
