import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({
  selector: 'app-peliculas',
  imports: [CommonModule, NavbarPublic],
  templateUrl: './peliculas.html',
  styleUrl: './peliculas.scss',
})
export class Peliculas implements OnInit {
  private contenidoService = inject(ContenidoClienteService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  peliculas: any[] = [];
  categorias: string[] = [];
  categoriaActiva = 'Todas';
  cargando = false;

  ngOnInit(): void { this.cargarPeliculas(); }

  cargarPeliculas(): void {
    this.cargando = true;
    this.contenidoService.listar().subscribe({
      next: (data) => {
        this.peliculas = Array.isArray(data) ? [...data] : [];
        const categoriasBackend = this.peliculas.map(item => item.categoria).filter(c => !!c);
        this.categorias = ['Todas', ...Array.from(new Set(categoriasBackend))];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando películas públicas:', err);
        this.peliculas = [];
        this.categorias = ['Todas'];
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrarCategoria(categoria: string): void {
    this.categoriaActiva = categoria;
    this.cargando = true;
    if (categoria === 'Todas') { this.cargarPeliculas(); return; }

    this.contenidoService.porCategoria(categoria).subscribe({
      next: (data) => {
        this.peliculas = Array.isArray(data) ? [...data] : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error filtrando categoría:', err);
        this.peliculas = [];
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.trim();
    if (!texto) { this.cargarPeliculas(); return; }
    this.cargando = true;
    this.contenidoService.buscar(texto).subscribe({
      next: (data) => {
        this.peliculas = Array.isArray(data) ? [...data] : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error buscando contenido:', err);
        this.peliculas = [];
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  imagen(item: any): string { return item?.imagenUrl || item?.bannerUrl || '/background.png'; }
  posters(): any[] { return this.peliculas.slice(0, 4); }

  verPelicula(item: any): void {
    localStorage.setItem('publicContenido', JSON.stringify(item));
    this.router.navigate(['/peliculas', item.id]);
  }
}
