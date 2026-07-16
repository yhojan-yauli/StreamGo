import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { urlCompleta } from '../../services/api';

@Component({
  selector: 'app-historial-client',
  imports: [CommonModule, RouterLink, NavbarClient],
  templateUrl: './historial-client.html',
  styleUrl: './historial-client.scss'
})
export class HistorialClient implements OnInit {
  private contenidoService = inject(ContenidoClienteService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  historial: any[] = [];
  cargando = false;
  error = '';

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial(): void {
    this.cargando = true;
    this.contenidoService.historial().subscribe({
      next: (data) => {
        // 1. Filtrar duplicados por contenidoId
        const unique = new Map();
        if (Array.isArray(data)) {
          for (const item of data) {
            if (!unique.has(item.contenidoId)) {
              unique.set(item.contenidoId, item);
            }
          }
        }
        const historialUnico = Array.from(unique.values());

        // 2. Obtener los contenidos completos para mapear el campo "estado"
        this.contenidoService.listar().subscribe({
          next: (contenidos) => {
            const contentMap = new Map(contenidos.map(c => [c.id, c]));
            // Enriquecer el historial con el estado del contenido
            this.historial = historialUnico.map(item => ({
              ...item,
              estado: contentMap.get(item.contenidoId)?.estado || null
            }));
            this.cargando = false;
            this.cdr.detectChanges();
          },
          error: () => {
            // Fallback: si no se pueden cargar los contenidos, mostrar historial sin glow
            this.historial = historialUnico;
            this.cargando = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.historial = [];
        this.cargando = false;
        this.error = 'No se pudo cargar tu historial.';
        this.cdr.detectChanges();
      }
    });
  }

  imagen(item: any): string {
    return urlCompleta(item?.imagenUrl);
  }

  progreso(item: any): number {
    if (item?.completado) return 100;
    const segundos = Number(item?.progresoSegundos || 0);
    if (segundos <= 0) return 20;
    const duracionEstimada = 5400;
    return Math.min(95, Math.max(20, (segundos / duracionEstimada) * 100));
  }

  continuar(item: any): void {
    if (!item?.contenidoId) return;
    localStorage.setItem('clientContenido', JSON.stringify({
      id: item.contenidoId,
      titulo: item.titulo,
      imagenUrl: item.imagenUrl,
      categoria: item.categoria,
      estado: item.estado // Guardamos el estado también
    }));
    this.router.navigate(['/client/reproducir', item.contenidoId]);
  }
}