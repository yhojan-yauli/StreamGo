import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({
  selector: 'app-home-client',
  imports: [CommonModule, RouterLink, NavbarClient],
  templateUrl: './home-client.html',
  styleUrl: './home-client.scss',
})
export class HomeClient implements OnInit, OnDestroy {
  private contenidoService = inject(ContenidoClienteService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  contenidos: any[] = [];
  recomendados: any[] = [];
  tendencias: any[] = [];
  historial: any[] = [];
  carouselLista: any[] = [];
  categorias: string[] = ['Todas'];
  categoriaActual = 'Todas';
  tipoActual: 'recomendados' | 'tendencias' = 'recomendados';
  tituloPagina = 'Inicio';
  tituloCentro = '';
  indiceCentro = 0;
  cargando = false;
  autoCarousel: any;

  ngOnInit(): void { this.cargarContenidoCliente(); this.cargarHistorial(); }
  ngOnDestroy(): void { clearInterval(this.autoCarousel); }

  cargarContenidoCliente(): void {
    this.cargando = true;
    this.contenidoService.listarSuscriptor().subscribe({
      next: (data) => { this.contenidos = Array.isArray(data) ? [...data] : []; this.configurarCategorias(); this.cargarRecomendados(); },
      error: () => {
        this.contenidoService.listar().subscribe({
          next: (data) => { this.contenidos = Array.isArray(data) ? [...data] : []; this.configurarCategorias(); this.cargarRecomendados(); },
          error: (err) => { console.error('Error cargando contenidos cliente:', err); this.contenidos = []; this.carouselLista = []; this.cargando = false; this.cdr.detectChanges(); }
        });
      }
    });
  }

  cargarRecomendados(): void {
    this.contenidoService.recomendados().subscribe({
      next: (data) => { this.recomendados = Array.isArray(data) && data.length > 0 ? [...data] : [...this.contenidos]; this.cargarTendencias(); },
      error: () => { this.recomendados = [...this.contenidos]; this.cargarTendencias(); }
    });
  }

  cargarTendencias(): void {
    this.contenidoService.tendencias().subscribe({
      next: (data) => this.finalizarCarga(data),
      error: () => this.finalizarCarga(this.contenidos)
    });
  }

  finalizarCarga(data: any[]): void {
    this.tendencias = Array.isArray(data) && data.length > 0 ? [...data] : [...this.contenidos];
    this.tipoActual = 'recomendados';
    this.carouselLista = [...this.recomendados];
    this.indiceCentro = this.carouselLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();
    this.cargando = false;
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  cargarHistorial(): void {
    this.contenidoService.historial().subscribe({
      next: (data) => { this.historial = Array.isArray(data) ? [...data].slice(0, 4) : []; this.cdr.detectChanges(); },
      error: () => { this.historial = []; this.cdr.detectChanges(); }
    });
  }

  configurarCategorias(): void {
    const categoriasBackend = this.contenidos.map(item => item.categoria).filter(categoria => !!categoria);
    this.categorias = ['Todas', ...Array.from(new Set(categoriasBackend))];
  }

  seleccionarTipo(tipo: 'recomendados' | 'tendencias'): void {
    this.tipoActual = tipo;
    this.categoriaActual = 'Todas';
    this.tituloPagina = 'Inicio';
    this.carouselLista = tipo === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
    this.indiceCentro = this.carouselLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro(); this.iniciarAutoCarousel(); this.cdr.detectChanges();
  }

  seleccionarCategoria(categoria: string): void {
    this.categoriaActual = categoria;
    this.tituloPagina = categoria === 'Todas' ? 'Inicio' : 'Categorías';
    this.cargando = true;
    if (categoria === 'Todas') {
      this.carouselLista = this.tipoActual === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
      this.indiceCentro = this.carouselLista.length >= 3 ? 2 : 0;
      this.actualizarTituloCentro(); this.cargando = false; this.iniciarAutoCarousel(); this.cdr.detectChanges(); return;
    }
    this.contenidoService.porCategoria(categoria).subscribe({
      next: (data) => { this.carouselLista = Array.isArray(data) ? [...data] : []; this.indiceCentro = this.carouselLista.length >= 3 ? 2 : 0; this.actualizarTituloCentro(); this.cargando = false; this.iniciarAutoCarousel(); this.cdr.detectChanges(); },
      error: (err) => { console.error('Error por categoría:', err); this.carouselLista = []; this.tituloCentro = 'Sin contenido'; this.cargando = false; this.cdr.detectChanges(); }
    });
  }

  buscar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.trim();
    if (!texto) { this.seleccionarCategoria(this.categoriaActual); return; }
    clearInterval(this.autoCarousel); this.cargando = true;
    this.contenidoService.buscar(texto).subscribe({
      next: (data) => { this.carouselLista = Array.isArray(data) ? [...data] : []; this.indiceCentro = this.carouselLista.length >= 3 ? 2 : 0; this.actualizarTituloCentro(); this.cargando = false; this.cdr.detectChanges(); },
      error: (err) => { console.error('Error buscando:', err); this.carouselLista = []; this.tituloCentro = 'Sin resultados'; this.cargando = false; this.cdr.detectChanges(); }
    });
  }

  obtenerVisibles(): any[] {
    if (!this.carouselLista || this.carouselLista.length === 0) return [];
    const visibles: any[] = [];
    for (let i = -2; i <= 2; i++) {
      let index = this.indiceCentro + i;
      while (index < 0) index += this.carouselLista.length;
      while (index >= this.carouselLista.length) index -= this.carouselLista.length;
      visibles.push(this.carouselLista[index]);
    }
    return visibles;
  }

  moverConScroll(event: WheelEvent): void { event.preventDefault(); if (!this.carouselLista.length) return; this.indiceCentro += event.deltaY > 0 ? 1 : -1; this.normalizarIndice(); this.actualizarTituloCentro(); this.iniciarAutoCarousel(); this.cdr.detectChanges(); }
  moverManual(direccion: number): void { if (!this.carouselLista.length) return; this.indiceCentro += direccion; this.normalizarIndice(); this.actualizarTituloCentro(); this.iniciarAutoCarousel(); this.cdr.detectChanges(); }
  contenidoAleatorio(): void { if (!this.carouselLista.length) return; this.indiceCentro = Math.floor(Math.random() * this.carouselLista.length); this.actualizarTituloCentro(); this.iniciarAutoCarousel(); this.cdr.detectChanges(); }
  iniciarAutoCarousel(): void { clearInterval(this.autoCarousel); this.autoCarousel = setInterval(() => { if (!this.carouselLista.length) return; this.indiceCentro++; this.normalizarIndice(); this.actualizarTituloCentro(); this.cdr.detectChanges(); }, 3500); }
  normalizarIndice(): void { if (!this.carouselLista.length) { this.indiceCentro = 0; return; } if (this.indiceCentro >= this.carouselLista.length) this.indiceCentro = 0; if (this.indiceCentro < 0) this.indiceCentro = this.carouselLista.length - 1; }
  actualizarTituloCentro(): void { this.tituloCentro = this.carouselLista.length ? (this.carouselLista[this.indiceCentro]?.titulo || '') : 'Sin contenido'; }
  imagen(item: any): string { return item?.bannerUrl || item?.imagenUrl || '/background.png'; }
  verContenido(item: any): void { if (!item?.id) return; localStorage.setItem('clientContenido', JSON.stringify(item)); this.router.navigate(['/client/reproducir', item.id]); }
}
