import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { VideoPlayer } from '../../componentes/video-player/video-player';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { urlCompleta } from '../../services/api';

@Component({
  selector: 'app-reproducir-client',
  imports: [CommonModule, RouterLink, NavbarClient, VideoPlayer],
  templateUrl: './reproducir-client.html',
  styleUrl: './reproducir-client.scss'
})
export class ReproducirClient implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private contenidoService = inject(ContenidoClienteService);
  private cdr = inject(ChangeDetectorRef);

  contenidoId = 0;
  contenido: any = null;
  reproduccion: any = null;
  relacionados: any[] = [];
  cargando = false;
  cargandoRelacionados = false;
  error = '';

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.contenidoId = Number(params.get('id'));
      const guardado = localStorage.getItem('clientContenido');
      if (guardado) {
        try {
          this.contenido = JSON.parse(guardado);
        } catch (e) {
          this.contenido = null;
        }
      }
      this.reproducir();
      this.cargarRelacionados();
    });
  }

  get videoUrl(): string {
    return this.reproduccion?.videoUrl || this.contenido?.videoUrl || '';
  }

  reproducir(): void {
    if (!this.contenidoId) {
      this.error = 'Contenido no válido.';
      return;
    }
    this.cargando = true;
    this.error = '';
    
    this.contenidoService.reproducirCliente(this.contenidoId).subscribe({
      next: data => {
        this.reproduccion = data;
        // Si la reproducción tiene datos del contenido, actualizar
        if (data && !this.contenido) {
          this.contenido = data;
        }
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Error al reproducir:', err);
        this.error = err.error?.mensaje || 'No se pudo reproducir este contenido. Verifica tu plan o suscripción.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarRelacionados(): void {
    this.cargandoRelacionados = true;
    
    // Primero intentar obtener por categoría
    if (this.contenido?.categoria) {
      this.contenidoService.porCategoria(this.contenido.categoria).subscribe({
        next: (data) => {
          const filtrados = Array.isArray(data) 
            ? data.filter(item => item.id !== this.contenidoId).slice(0, 6) 
            : [];
          
          // Si no hay suficientes, completar con recomendados
          if (filtrados.length < 3) {
            this.completarConRecomendados(filtrados);
          } else {
            this.relacionados = filtrados;
            this.cargandoRelacionados = false;
            this.cdr.detectChanges();
          }
        },
        error: () => {
          this.completarConRecomendados([]);
        }
      });
    } else {
      // Si no tiene categoría, usar recomendados
      this.completarConRecomendados([]);
    }
  }

  completarConRecomendados(actuales: any[]): void {
    this.contenidoService.recomendados().subscribe({
      next: (data) => {
        const recomendados = Array.isArray(data) 
          ? data.filter(item => item.id !== this.contenidoId) 
          : [];
        
        // Combinar y eliminar duplicados
        const combinados = [...actuales];
        for (const item of recomendados) {
          if (!combinados.some(c => c.id === item.id)) {
            combinados.push(item);
          }
          if (combinados.length >= 6) break;
        }
        
        this.relacionados = combinados.slice(0, 6);
        this.cargandoRelacionados = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.relacionados = [];
        this.cargandoRelacionados = false;
        this.cdr.detectChanges();
      }
    });
  }

  // === UTILIDADES ===
  titulo(): string {
    return this.reproduccion?.titulo || this.contenido?.titulo || 'Reproduciendo contenido';
  }

  descripcion(): string {
    return this.contenido?.descripcion || this.reproduccion?.mensaje || 'Contenido disponible en StreamGO.';
  }

  categoria(): string {
    return this.contenido?.categoria || this.reproduccion?.categoria || 'StreamGO';
  }

  tipoContenido(): string {
    return this.contenido?.tipoContenido || this.reproduccion?.tipoContenido || 'Contenido';
  }

  promedioCalificacion(): number {
    return this.contenido?.promedioCalificacion || this.reproduccion?.promedioCalificacion || 0;
  }

  totalReproducciones(): number {
    return this.contenido?.totalReproducciones || this.reproduccion?.totalReproducciones || 0;
  }

  imagen(item: any): string {
    return urlCompleta(item?.bannerUrl || item?.imagenUrl || '/background.png');
  }

  verRelacionado(item: any): void {
    if (!item?.id) return;
    localStorage.setItem('clientContenido', JSON.stringify(item));
    // Recargar la página con el nuevo ID
    this.router.navigate(['/client/reproducir', item.id]);
    // Recargar el componente
    window.location.reload();
  }

  irAHome(): void {
    this.router.navigate(['/client/home']);
  }
}