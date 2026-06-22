import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({
  selector: 'app-home-public',
  imports: [CommonModule, RouterLink],
  templateUrl: './home-public.html',
  styleUrl: './home-public.scss',
})
export class HomePublic implements OnInit {

  private contenidoService = inject(ContenidoClienteService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  contenidos: any[] = [];
  posters: any[] = [];
  banner: any = null;

  ngOnInit(): void {
    this.cargarLanding();
  }

  cargarLanding(): void {
    this.contenidoService.listar().subscribe({
      next: (data) => {
        this.contenidos = Array.isArray(data) ? [...data] : [];
        this.posters = this.contenidos.filter(item => item.imagenUrl || item.bannerUrl).slice(0, 5);
        this.banner = this.contenidos.find(item => item.bannerUrl) || this.contenidos[0] || null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando landing pública:', err);
        this.contenidos = [];
        this.posters = [];
        this.banner = null;
        this.cdr.detectChanges();
      }
    });
  }

  imagen(item: any): string {
    return item?.imagenUrl || item?.bannerUrl || '/background.png';
  }

  irPeliculas(): void {
    this.router.navigate(['/peliculas']);
  }
}
