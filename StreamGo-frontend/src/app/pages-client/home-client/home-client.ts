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
  recomendados: any[] = [];
  tendencias: any[] = [];
  historial: any[] = [];
  carouselCurvoLista: any[] = [];
  categorias: string[] = ['Todas'];
  categoriaActual = 'Todas';
  tipoActual: 'recomendados' | 'tendencias' = 'recomendados';
  tituloPagina = 'Inicio';
  tituloCentro = '';
  indiceCentro = 0;
  cargando = false;
  buscando = false;
  autoCarousel: any;

  carousels: CarouselData[] = [];

  ngOnInit(): void {
    this.cargarContenidoCliente();
    this.cargarHistorial();
  }

  ngOnDestroy(): void {
    clearInterval(this.autoCarousel);
    this.carousels.forEach(c => clearInterval(c.autoTimer));
  }

  cargarContenidoCliente(): void {
    this.cargando = true;
    this.contenidoService.listarSuscriptor().subscribe({
      next: (data) => {
        this.contenidos = Array.isArray(data) ? [...data] : [];
        this.procesarContenidos();
      },
      error: () => {
        this.contenidoService.listar().subscribe({
          next: (data) => {
            this.contenidos = Array.isArray(data) ? [...data] : [];
            this.procesarContenidos();
          },
          error: (err) => {
            console.error('Error cargando contenidos:', err);
            this.contenidos = [];
            this.cargando = false;
            this.cdr.detectChanges();
          }
        });
      }
    });
  }

  procesarContenidos(): void {
    const cats = this.contenidos.map(item => item.categoria).filter(c => c);
    this.categorias = ['Todas', ...Array.from(new Set(cats))];

    this.recomendados = [...this.contenidos];
    this.tendencias = [...this.contenidos].reverse();

    this.tipoActual = 'recomendados';
    this.carouselCurvoLista = [...this.recomendados];
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();

    this.construirCarousels();

    this.cargando = false;
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  construirCarousels(): void {
    const grupos = new Map<string, any[]>();
    for (const item of this.contenidos) {
      const cat = item.categoria || 'General';
      if (!grupos.has(cat)) grupos.set(cat, []);
      grupos.get(cat)!.push(item);
    }
    this.carousels = [];
    for (const [categoria, items] of grupos) {
      const base = [...items];
      const conClones = base.length > 4 ? [...base, ...base.slice(0, 4)] : [...base];
      this.carousels.push({
        categoria,
        items: base,
        itemsFiltrados: conClones,
        indiceCentro: 0,
        autoTimer: null as any,
        sinTransicion: false,
      });
    }
    for (const carousel of this.carousels) {
      this.iniciarAutoCategoria(carousel);
    }
  }

  // === CARRUSEL CURVO ===
  seleccionarTipo(tipo: 'recomendados' | 'tendencias'): void {
    this.tipoActual = tipo;
    this.categoriaActual = 'Todas';
    this.tituloPagina = 'Inicio';
    this.carouselCurvoLista = tipo === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  seleccionarCategoria(categoria: string): void {
    this.categoriaActual = categoria;
    this.tituloPagina = categoria === 'Todas' ? 'Inicio' : 'Categorías';
    this.cargando = true;

    if (categoria === 'Todas') {
      this.carouselCurvoLista = this.tipoActual === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
      this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
      this.actualizarTituloCentro();
      this.cargando = false;
      this.iniciarAutoCarousel();
      this.cdr.detectChanges();
      return;
    }

    this.contenidoService.porCategoria(categoria).subscribe({
      next: (data) => {
        this.carouselCurvoLista = Array.isArray(data) ? [...data] : [];
        this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
        this.actualizarTituloCentro();
        this.cargando = false;
        this.iniciarAutoCarousel();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error por categoría:', err);
        this.carouselCurvoLista = [];
        this.tituloCentro = 'Sin contenido';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  obtenerVisibles(): any[] {
    if (!this.carouselCurvoLista || this.carouselCurvoLista.length === 0) return [];
    const visibles: any[] = [];
    for (let i = -2; i <= 2; i++) {
      let index = this.indiceCentro + i;
      while (index < 0) index += this.carouselCurvoLista.length;
      while (index >= this.carouselCurvoLista.length) index -= this.carouselCurvoLista.length;
      visibles.push(this.carouselCurvoLista[index]);
    }
    return visibles;
  }

  moverConScroll(event: WheelEvent): void {
    event.preventDefault();
    if (!this.carouselCurvoLista.length) return;
    this.indiceCentro += event.deltaY > 0 ? 1 : -1;
    this.normalizarIndiceCurvo();
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  moverManual(direccion: number): void {
    if (!this.carouselCurvoLista.length) return;
    this.indiceCentro += direccion;
    this.normalizarIndiceCurvo();
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  normalizarIndiceCurvo(): void {
    if (!this.carouselCurvoLista.length) { this.indiceCentro = 0; return; }
    if (this.indiceCentro >= this.carouselCurvoLista.length) this.indiceCentro = 0;
    if (this.indiceCentro < 0) this.indiceCentro = this.carouselCurvoLista.length - 1;
  }

  actualizarTituloCentro(): void {
    this.tituloCentro = this.carouselCurvoLista.length
      ? (this.carouselCurvoLista[this.indiceCentro]?.titulo || '')
      : 'Sin contenido';
  }

  iniciarAutoCarousel(): void {
    clearInterval(this.autoCarousel);
    if (!this.carouselCurvoLista.length) return;
    this.autoCarousel = setInterval(() => {
      this.indiceCentro++;
      this.normalizarIndiceCurvo();
      this.actualizarTituloCentro();
      this.cdr.detectChanges();
    }, 5000);
  }

  // === CARRUSELES POR CATEGORÍA ===
  getTransform(carousel: CarouselData): string {
    const len = carousel.itemsFiltrados.length;
    if (len <= 4) return 'translateX(0)';
    return `translateX(-${carousel.indiceCentro * 25}%)`;
  }

  moverManualCategoria(direccion: number, carousel: CarouselData): void {
    if (!carousel.itemsFiltrados.length) return;
    carousel.indiceCentro += direccion;
    this.normalizarIndiceCategoria(carousel);
    this.iniciarAutoCategoria(carousel);
  }

  normalizarIndiceCategoria(carousel: CarouselData): void {
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

  iniciarAutoCategoria(carousel: CarouselData): void {
    clearInterval(carousel.autoTimer);
    if (carousel.itemsFiltrados.length <= 4) return;
    carousel.autoTimer = setInterval(() => {
      carousel.indiceCentro++;
      this.normalizarIndiceCategoria(carousel);
      this.cdr.detectChanges();
    }, 9000);
  }

  // === UTILIDADES ===
  imagen(item: any): string {
    return urlCompleta(item?.bannerUrl || item?.imagenUrl);
  }

  verContenido(item: any): void {
    if (!item?.id) return;
    localStorage.setItem('clientContenido', JSON.stringify(item));
    this.router.navigate(['/client/reproducir', item.id]);
  }

  buscar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.buscando = !!texto;
    this.tituloPagina = texto ? 'Resultados' : 'Inicio';

    clearInterval(this.autoCarousel);
    if (!texto) {
      this.carouselCurvoLista = this.tipoActual === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
      this.iniciarAutoCarousel();
    } else {
      this.carouselCurvoLista = this.contenidos.filter(item =>
        item.titulo?.toLowerCase().includes(texto)
      );
    }
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();

    for (const carousel of this.carousels) {
      clearInterval(carousel.autoTimer);
      if (!texto) {
        const base = [...carousel.items];
        carousel.itemsFiltrados = base.length > 4 ? [...base, ...base.slice(0, 4)] : base;
      } else {
        const filtrados = carousel.items.filter(item =>
          item.titulo?.toLowerCase().includes(texto)
        );
        carousel.itemsFiltrados = filtrados.length > 4 ? [...filtrados, ...filtrados.slice(0, 4)] : filtrados;
      }
      this.normalizarIndiceCategoria(carousel);
    }
    if (!texto) {
      for (const carousel of this.carousels) {
        this.iniciarAutoCategoria(carousel);
      }
    }
    this.cdr.detectChanges();
  }

  contenidoAleatorio(): void {
    if (!this.carouselCurvoLista.length) return;
    this.indiceCentro = Math.floor(Math.random() * this.carouselCurvoLista.length);
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  cargarHistorial(): void {
    this.contenidoService.historial().subscribe({
      next: (data) => {
        this.historial = Array.isArray(data) ? [...data].slice(0, 4) : [];
        this.cdr.detectChanges();
      },
      error: () => {
        this.historial = [];
        this.cdr.detectChanges();
      }
    });
  }
}