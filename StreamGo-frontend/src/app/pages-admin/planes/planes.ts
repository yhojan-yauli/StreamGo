import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Planes  } from '../../services/planes';
import { SidebarAdmin } from "../../componentes/sidebar-admin/sidebar-admin";
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-planes',
  imports: [CommonModule, FormsModule, SidebarAdmin],
  templateUrl: './planes.html',
  styleUrl: './planes.scss',
})
export class planes implements OnInit {

  

// Lista que almacenará los planes obtenidos desde la API
  planes: any[] = [];

  // Controla si el modal está visible o no
  modalVisible = false;

  // Objeto que se enviará al backend
  plan: any = {
    id: null,
    tipoPlan: 'BASICO',
    nombre: '',
    precio: 0,
    duracionHoras: 0,
    descripcion: '',
    activo: true
  };

  // Inyección del servicio
  constructor(
  private planesService: Planes,
  private cdr: ChangeDetectorRef
) {}

  // Se ejecuta automáticamente cuando se carga el componente
  ngOnInit(): void {
  console.log('ngOnInit ejecutado');
  this.listarPlanes();
}

  // Obtiene todos los planes desde la API
  listarPlanes(): void {
  this.planesService.listar().subscribe({
    next: (resp) => {
      console.log('Respuesta API:', resp);
      this.planes = resp;

      this.cdr.detectChanges();

    }
  });
}

  // Abre el modal para crear un nuevo plan
  nuevoPlan(): void {

    // Limpia el formulario
    this.plan = {
      id: null,
      tipoPlan: 'BASICO',
      nombre: '',
      precio: 0,
      duracionHoras: 0,
      descripcion: '',
      activo: true
    };

    this.modalVisible = true;
  }

  // Abre el modal cargando los datos del plan seleccionado
  editarPlan(plan: any): void {

    // Copia los datos para no modificar directamente la tabla
    this.plan = { ...plan };

    this.modalVisible = true;
  }

  // Guarda (crear o actualizar)
  guardarPlan(): void {

    // Si existe ID => actualizar
    if (this.plan.id != null) {

      this.planesService.actualizar(this.plan.id, this.plan)
        .subscribe({
          next: () => {

            // Recargar tabla
            this.listarPlanes();

            // Cerrar modal
            this.modalVisible = false;
          },

          error: (err) => {
            console.error('Error al actualizar', err);
          }
        });

    } else {

      // Si no existe ID => crear

      this.planesService.crear(this.plan)
        .subscribe({
          next: () => {

            // Recargar tabla
            this.listarPlanes();

            // Cerrar modal
            this.modalVisible = false;
          },

          error: (err) => {
            console.error('Error al crear', err);
          }
        });
    }
  }

  // Elimina un plan

  eliminarPlan(id: number): void {

  if (!confirm('¿Desea eliminar este plan?')) {
    return;
  }

  console.log('ANTES:', this.planes);

  this.planesService.eliminar(id).subscribe({

    next: (resp) => {

      console.log('DELETE RESPUESTA:', resp);

      this.planes = this.planes.filter(
        p => p.id !== id
      );

      console.log('DESPUES:', this.planes);

      this.listarPlanes();
      this.cdr.detectChanges()

    },

    error: (err) => {
      console.error('ERROR DELETE', err);
    }

  });

}

  // Cierra el modal
  cerrarModal(): void {
    this.modalVisible = false;
  }

}