import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { PeticionesClienteService } from '../../services/peticiones-cliente';

@Component({
  selector: 'app-peticiones-client',
  imports: [CommonModule, RouterLink, NavbarClient],
  templateUrl: './peticiones-client.html',
  styleUrl: './peticiones-client.scss'
})
export class PeticionesClient implements OnInit {
  // ============================================
  // INJECTIONS
  // ============================================
  private peticionesService = inject(PeticionesClienteService);
  private cdr = inject(ChangeDetectorRef);

  // ============================================
  // PROPERTIES
  // ============================================
  peticiones: any[] = [];
  cargando = false;
  votandoId: number | null = null;
  mensaje = '';
  error = '';

  // ============================================
  // LIFECYCLE HOOKS
  // ============================================
  ngOnInit(): void {
    this.cargarPeticiones();
  }

  // ============================================
  // PUBLIC METHODS
  // ============================================

  /**
   * Carga las peticiones desde el servicio
   */
  cargarPeticiones(): void {
    this.cargando = true;
    this.error = '';
    this.mensaje = '';

    this.peticionesService.listar().subscribe({
      next: (data) => {
        this.peticiones = Array.isArray(data)
          ? data.filter(item => item.activo !== false)
          : [];
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar peticiones:', err);
        this.peticiones = [];
        this.cargando = false;
        this.error = 'No se pudieron cargar las peticiones.';
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Marca una petición como deseada
   * @param item - Petición a marcar
   */
  elegirPeticion(item: any): void {

    if (!item?.id) {
      this.error = 'Contenido no válido.';
      return;
    }

    this.votandoId = item.id;
    this.error = '';
    this.mensaje = '';

    this.peticionesService.elegir(item.id).subscribe({

      next: () => {

        this.votandoId = null;
        this.mensaje = 'Tu voto fue registrado correctamente.';

        // Recargar la lista para actualizar la cantidad de votos
        this.cargarPeticiones();

        this.cdr.detectChanges();
      },

      error: (err) => {

        console.error(err);

        this.votandoId = null;
        this.error = 'No se pudo registrar el voto.';

        this.cdr.detectChanges();
      }

    });

  }

  /**
   * Verifica si una petición está seleccionada
   * @param id - ID de la petición
   * @returns true si está seleccionada
   */


  /**
   * Obtiene la URL de la imagen
   * @param item - Petición
   * @returns URL de la imagen o fallback
   */
  imagen(item: any): string {
    return item?.imagenUrl || item?.posterUrl || '/background.png';
  }
}
