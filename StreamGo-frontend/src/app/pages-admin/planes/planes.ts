import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Planes } from '../../services/planes';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-planes',
  imports: [CommonModule, FormsModule],
  templateUrl: './planes.html',
  styleUrl: './planes.scss',
})
export class planes implements OnInit {

  planes: any[] = [];
  modalVisible = false;

  plan: any = {
    id: null,
    tipoPlan: 'BASICO',
    nombre: '',
    precio: 0,
    duracionHoras: 0,
    descripcion: '',
    activo: true
  };

  constructor(
    private planesService: Planes,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.listarPlanes();
  }

  listarPlanes(): void {
    this.planesService.listar().subscribe({
      next: (resp) => {
        this.planes = resp;
        this.cdr.detectChanges();
      }
    });
  }

  nuevoPlan(): void {
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

  editarPlan(plan: any): void {
    this.plan = { ...plan };
    this.modalVisible = true;
  }

  guardarPlan(): void {
    if (this.plan.id != null) {
      this.planesService.actualizar(this.plan.id, this.plan).subscribe({
        next: () => {
          this.listarPlanes();
          this.modalVisible = false;
        },
        error: (err) => {
          console.error('Error al actualizar', err);
        }
      });
    } else {
      this.planesService.crear(this.plan).subscribe({
        next: () => {
          this.listarPlanes();
          this.modalVisible = false;
        },
        error: (err) => {
          console.error('Error al crear', err);
        }
      });
    }
  }

  eliminarPlan(id: number): void {
    if (!confirm('¿Desea eliminar este plan?')) { return; }
    this.planesService.eliminar(id).subscribe({
      next: () => {
        this.listarPlanes();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('ERROR DELETE', err);
      }
    });
  }

  cerrarModal(): void {
    this.modalVisible = false;
  }
}
