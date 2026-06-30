import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { urlCompleta } from '../../services/api';

interface CarouselData {
  categoria: string;
  items: any[];
  itemsFiltrados: any[];
  indiceCentro: number;
  autoTimer: any;
  sinTransicion: boolean;
}

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
  historial: any[] = [];
  carousels: CarouselData[] = [];
  tituloPagina = 'Inicio';
  cargando = false;
  buscando = false;

  ngOnInit(): void { this.cargarContenidoCliente(); this.cargarHistorial(); }
  ngOnDestroy(): void { this.carousels.forEach(c => clearInterval(c.autoTimer)); }

  cargarContenidoCliente(): void {
    this.cargando = true;
    this.contenidoService.listarSuscriptor().subscribe({
      next: (data) => { this.contenidos = Array.isArray(data) ? [...data] : []; this.construirCarousels(); },
      error: () => {
        this.contenidoService.listar().subscribe({
          next: (data) => { this.contenidos = Array.isArray(data) ? [...data] : []; this.construirCarousels(); },
          error: (err) => { console.error('Error cargando contenidos:', err); this.contenidos = []; this.cargando = false; this.cdr.detectChanges(); }
        });
      }
    });
  }

  construirCarousels(): void {
    const grupos = new Map<string, any[]>();
    for (const item of this.contenidos) {
      const cats = (item.categoria || 'General').split(',').map((c: string) => c.trim()).filter(Boolean);
      for (const cat of cats) {
        if (!grupos.has(cat)) grupos.set(cat, []);
        grupos.get(cat)!.push(item);
      }
    }
    this.carousels = [];
    for (const [categoria, items] of grupos) {
      const itemsBase = [...items];
      const itemsConClones = itemsBase.length > 4 ? [...itemsBase, ...itemsBase.slice(0, 4)] : [...itemsBase];
      this.carousels.push({
        categoria,
        items: itemsBase,
        itemsFiltrados: itemsConClones,
        indiceCentro: 0,
        autoTimer: null as any,
        sinTransicion: false,
      });
    }
    this.cargando = false;
    this.iniciarAutoCarousels();
    this.cdr.detectChanges();
  }

  iniciarAutoCarousels(): void {
    for (const carousel of this.carousels) {
      this.iniciarAutoCarousel(carousel);
    }
  }

  iniciarAutoCarousel(carousel: CarouselData): void {
    clearInterval(carousel.autoTimer);
    carousel.autoTimer = setInterval(() => {
      if (!carousel.itemsFiltrados.length) return;
      carousel.indiceCentro++;
      this.normalizarIndice(carousel);
      this.cdr.detectChanges();
    }, 9000);
  }

  getTransform(carousel: CarouselData): string {
    const len = carousel.itemsFiltrados.length;
    if (len <= 4) return 'translateX(0)';
    return `translateX(-${carousel.indiceCentro * 25}%)`;
  }

  moverManual(direccion: number, carousel: CarouselData): void {
    if (!carousel.itemsFiltrados.length) return;
    carousel.indiceCentro += direccion;
    this.normalizarIndice(carousel);
    this.iniciarAutoCarousel(carousel);
  }

  contenidoAleatorio(): void {
    const conItems = this.carousels.filter(c => c.items.length > 4);
    if (!conItems.length) return;
    const rand = conItems[Math.floor(Math.random() * conItems.length)];
    const maxIndex = rand.items.length - 4;
    rand.indiceCentro = Math.floor(Math.random() * (maxIndex + 1));
    this.iniciarAutoCarousel(rand);
  }

  normalizarIndice(carousel: CarouselData): void {
    const len = carousel.itemsFiltrados.length;
    if (len <= 4) { carousel.indiceCentro = 0; return; }
    const maxIndex = len - 4;
    if (carousel.indiceCentro > maxIndex) {
      carousel.sinTransicion = true;
      carousel.indiceCentro = 0;
      setTimeout(() => { carousel.sinTransicion = false; this.cdr.detectChanges(); }, 30);
    }
    if (carousel.indiceCentro < 0) {
      carousel.sinTransicion = true;
      carousel.indiceCentro = maxIndex;
      setTimeout(() => { carousel.sinTransicion = false; this.cdr.detectChanges(); }, 30);
    }
  }

  imagen(item: any): string { return urlCompleta(item?.bannerUrl || item?.imagenUrl); }

  verContenido(item: any): void {
    if (!item?.id) return;
    localStorage.setItem('clientContenido', JSON.stringify(item));
    this.router.navigate(['/client/reproducir', item.id]);
  }

  buscar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.buscando = !!texto;
    this.tituloPagina = texto ? 'Resultados' : 'Inicio';
    for (const carousel of this.carousels) {
      clearInterval(carousel.autoTimer);
      carousel.sinTransicion = false;
      if (!texto) {
        const base = [...carousel.items];
        carousel.itemsFiltrados = base.length > 4 ? [...base, ...base.slice(0, 4)] : base;
      } else {
        const filtrados = carousel.items.filter(item =>
          item.titulo?.toLowerCase().includes(texto)
        );
        carousel.itemsFiltrados = filtrados.length > 4 ? [...filtrados, ...filtrados.slice(0, 4)] : filtrados;
      }
      this.normalizarIndice(carousel);
    }
    if (!texto) this.iniciarAutoCarousels();
    this.cdr.detectChanges();
  }

  cargarHistorial(): void {
    this.contenidoService.historial().subscribe({
      next: (data) => { this.historial = Array.isArray(data) ? [...data].slice(0, 4) : []; this.cdr.detectChanges(); },
      error: () => { this.historial = []; this.cdr.detectChanges(); }
    });
  }
}
