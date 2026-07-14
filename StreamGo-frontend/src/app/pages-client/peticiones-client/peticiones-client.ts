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
  seleccionadas = new Set<number>();
  cargando = false;
  votandoId: number | null = null;
  mensaje = '';
  error = '';

  // ============================================
  // LIFECYCLE HOOKS
  // ============================================
  ngOnInit(): void {
    this.cargarPeticiones();
    this.cargarSeleccionadasLocal();
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
    // Validación
    if (!item?.id) {
      this.error = 'Petición no válida.';
      return;
    }

    // Verificar si ya está seleccionada
    if (this.seleccionadas.has(item.id)) {
      this.mensaje = 'Ya marcaste esta petición como deseada.';
      this.error = '';
      this.cdr.detectChanges();
      return;
    }

    // Marcar como votando
    this.votandoId = item.id;
    this.error = '';
    this.mensaje = '';

    // Llamar al servicio
    this.peticionesService.elegir(item.id).subscribe({
      next: () => {
        this.seleccionadas.add(item.id);
        this.guardarSeleccionadasLocal();
        this.votandoId = null;
        this.mensaje = `Marcaste "${item.titulo}" como deseada.`;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al elegir petición:', err);
        this.votandoId = null;
        this.error = 'No se pudo registrar la petición.';
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Verifica si una petición está seleccionada
   * @param id - ID de la petición
   * @returns true si está seleccionada
   */
  estaSeleccionada(id: number): boolean {
    return this.seleccionadas.has(id);
  }

  /**
   * Obtiene la URL de la imagen
   * @param item - Petición
   * @returns URL de la imagen o fallback
   */
  imagen(item: any): string {
    return item?.imagenUrl || item?.posterUrl || '/background.png';
  }

  // ============================================
  // PRIVATE METHODS (LocalStorage)
  // ============================================

  /**
   * Carga las selecciones desde localStorage
   */
  private cargarSeleccionadasLocal(): void {
    const guardado = localStorage.getItem('peticionesSeleccionadasCliente');

    if (!guardado) {
      return;
    }

    try {
      const ids = JSON.parse(guardado);
      this.seleccionadas = new Set(Array.isArray(ids) ? ids : []);
    } catch (error) {
      console.error('Error al cargar selecciones locales:', error);
      this.seleccionadas = new Set<number>();
    }
  }

  /**
   * Guarda las selecciones en localStorage
   */
  private guardarSeleccionadasLocal(): void {
    try {
      localStorage.setItem(
        'peticionesSeleccionadasCliente',
        JSON.stringify(Array.from(this.seleccionadas))
      );
    } catch (error) {
      console.error('Error al guardar selecciones locales:', error);
    }
  }
}
