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
  carouselsFiltrados: CarouselData[] = [];

  private scrollTimeout: any = null;
  private scrollCooldown = false;
  private readonly SCROLL_COOLDOWN_MS = 300;

  ngOnInit(): void {
    this.cargarContenidoCliente();
    this.cargarHistorial();
  }

  ngOnDestroy(): void {
    clearInterval(this.autoCarousel);
    this.carousels.forEach(c => clearInterval(c.autoTimer));
    this.carouselsFiltrados.forEach(c => clearInterval(c.autoTimer));
    if (this.scrollTimeout) {
      clearTimeout(this.scrollTimeout);
    }
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
    this.categoriaActual = 'Todas';
    this.carouselCurvoLista = [...this.recomendados];
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();

    this.construirCarousels();
    this.actualizarCarouselsFiltrados('Todas');

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

  actualizarCarouselsFiltrados(categoria: string): void {
    this.carouselsFiltrados.forEach(c => clearInterval(c.autoTimer));
    this.carouselsFiltrados = [];

    if (categoria === 'Todas') {
      for (const carousel of this.carousels) {
        const copia = { ...carousel, itemsFiltrados: [...carousel.itemsFiltrados], autoTimer: null as any };
        this.carouselsFiltrados.push(copia);
        this.iniciarAutoCategoria(copia);
      }
    } else {
      const encontrado = this.carousels.find(c => c.categoria === categoria);
      if (encontrado) {
        const copia = { ...encontrado, itemsFiltrados: [...encontrado.itemsFiltrados], autoTimer: null as any };
        this.carouselsFiltrados.push(copia);
        this.iniciarAutoCategoria(copia);
      }
    }
    this.cdr.detectChanges();
  }

  // === CARRUSEL CURVO ===
  seleccionarTipo(tipo: 'recomendados' | 'tendencias'): void {
    this.tipoActual = tipo;
    this.tituloPagina = this.categoriaActual === 'Todas' ? 'Inicio' : 'Categorías';
    
    let listaBase = tipo === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
    if (this.categoriaActual !== 'Todas') {
      listaBase = listaBase.filter(item => item.categoria === this.categoriaActual);
    }
    
    this.carouselCurvoLista = listaBase;
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
  }

  seleccionarCategoria(categoria: string): void {
    this.categoriaActual = categoria;
    this.tituloPagina = categoria === 'Todas' ? 'Inicio' : 'Categorías';
    this.cargando = true;

    let listaBase = this.tipoActual === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
    if (categoria !== 'Todas') {
      listaBase = listaBase.filter(item => item.categoria === categoria);
    }
    this.carouselCurvoLista = listaBase;
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();

    this.actualizarCarouselsFiltrados(categoria);

    this.cargando = false;
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
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
    if (this.scrollCooldown) return;
    if (!this.carouselCurvoLista.length) return;
    
    const delta = event.deltaY;
    const threshold = 15;
    if (Math.abs(delta) < threshold) return;
    
    const direccion = delta > 0 ? 1 : -1;
    this.indiceCentro += direccion;
    this.normalizarIndiceCurvo();
    this.actualizarTituloCentro();
    this.iniciarAutoCarousel();
    this.cdr.detectChanges();
    
    this.scrollCooldown = true;
    if (this.scrollTimeout) clearTimeout(this.scrollTimeout);
    this.scrollTimeout = setTimeout(() => {
      this.scrollCooldown = false;
      this.scrollTimeout = null;
    }, this.SCROLL_COOLDOWN_MS);
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
    let contenidoCompleto = this.contenidos.find(c => c.id === item.id);
    if (!contenidoCompleto) {
      contenidoCompleto = this.recomendados.find(c => c.id === item.id);
    }
    if (!contenidoCompleto) {
      contenidoCompleto = this.tendencias.find(c => c.id === item.id);
    }
    const dataAGuardar = contenidoCompleto || item;
    localStorage.setItem('clientContenido', JSON.stringify(dataAGuardar));
    this.router.navigate(['/client/reproducir', item.id]);
  }

  buscar(event: Event): void {
    const texto = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.buscando = !!texto;
    this.tituloPagina = texto ? 'Resultados' : 'Inicio';

    clearInterval(this.autoCarousel);
    this.carouselsFiltrados.forEach(c => clearInterval(c.autoTimer));
    this.carouselsFiltrados = [];

    if (!texto) {
      let listaBase = this.tipoActual === 'recomendados' ? [...this.recomendados] : [...this.tendencias];
      if (this.categoriaActual !== 'Todas') {
        listaBase = listaBase.filter(item => item.categoria === this.categoriaActual);
      }
      this.carouselCurvoLista = listaBase;
      this.iniciarAutoCarousel();
      this.actualizarCarouselsFiltrados(this.categoriaActual);
    } else {
      this.carouselCurvoLista = this.contenidos.filter(item =>
        item.titulo?.toLowerCase().includes(texto)
      );
      for (const carousel of this.carousels) {
        const filtrados = carousel.items.filter(item =>
          item.titulo?.toLowerCase().includes(texto)
        );
        if (filtrados.length > 0) {
          const conClones = filtrados.length > 4 ? [...filtrados, ...filtrados.slice(0, 4)] : filtrados;
          const copia = { ...carousel, itemsFiltrados: conClones, autoTimer: null as any };
          this.carouselsFiltrados.push(copia);
          this.iniciarAutoCategoria(copia);
        }
      }
    }
    this.indiceCentro = this.carouselCurvoLista.length >= 3 ? 2 : 0;
    this.actualizarTituloCentro();
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
        if (Array.isArray(data) && data.length > 0) {
          const historialCompleto = data.map(item => {
            const completo = this.contenidos.find(c => c.id === item.contenidoId);
            return completo || item;
          });
          this.historial = historialCompleto.slice(0, 4);
        } else {
          this.historial = [];
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.historial = [];
        this.cdr.detectChanges();
      }
    });
  }
}