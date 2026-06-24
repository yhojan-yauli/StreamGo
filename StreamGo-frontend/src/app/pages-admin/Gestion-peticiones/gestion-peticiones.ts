import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarAdmin } from '../componentes/sidebar-admin/sidebar-admin';
import { PeticionesAdminService } from '../services/peticiones-admin';

@Component({
    selector: 'app-gestion-peticiones',
    standalone: true,
    imports: [CommonModule, FormsModule, SidebarAdmin],
    templateUrl: './gestion-peticiones.html',
    styleUrl: './gestion-peticiones.scss'
})
export class GestionPeticiones implements OnInit {

    private service = inject(PeticionesAdminService);
    private cdr = inject(ChangeDetectorRef);

    peticiones: any[] = [];
    peticionesFiltradas: any[] = [];
    filtro: string = '';
    cargando = false;
    guardando = false;
    modalVisible = false;
    modoEdicion = false;
    peticionId: number | null = null;
    mensaje = '';
    error = '';

    form: any = {
        titulo: '',
        descripcion: '',
        posterUrl: '',
        imagenUrl: ''
    };

    ngOnInit(): void {
        this.cargarPeticiones();
    }

    cargarPeticiones(): void {
        this.cargando = true;
        this.service.listar().subscribe({
            next: data => {
                this.peticiones = Array.isArray(data) ? [...data] : [];
                this.peticionesFiltradas = [...this.peticiones];
                this.cargando = false;
                this.cdr.detectChanges();
            },
            error: err => {
                console.error(err);
                this.peticiones = [];
                this.peticionesFiltradas = [];
                this.cargando = false;
                this.error = 'No se pudo cargar las peticiones.';
                this.cdr.detectChanges();
            }
        });
    }

    filtrarPeticiones(): void {
        const texto = this.filtro.toLowerCase().trim();
        if (!texto) {
            this.peticionesFiltradas = [...this.peticiones];
            return;
        }
        this.peticionesFiltradas = this.peticiones.filter(item => 
            item.titulo?.toLowerCase().includes(texto) ||
            item.descripcion?.toLowerCase().includes(texto)
        );
    }

    nuevaPeticion(): void {
        this.modoEdicion = false;
        this.peticionId = null;
        this.form = { titulo: '', descripcion: '', posterUrl: '', imagenUrl: '' };
        this.modalVisible = true;
        this.cdr.detectChanges();
    }

    editar(item: any): void {
        this.modoEdicion = true;
        this.peticionId = item.contenidoVotableId;
        this.form = {
            titulo: item.titulo || '',
            descripcion: item.descripcion || '',
            posterUrl: item.posterUrl || '',
            imagenUrl: item.imagenUrl || ''
        };
        this.modalVisible = true;
        this.cdr.detectChanges();
    }

    cerrarModal(): void {
        this.modalVisible = false;
        this.peticionId = null;
        this.cdr.detectChanges();
    }

    guardar(): void {
        if (!this.form.titulo?.trim()) {
            this.error = 'El título es obligatorio.';
            return;
        }

        this.guardando = true;
        this.error = '';

        const data = {
            titulo: this.form.titulo.trim(),
            descripcion: this.form.descripcion?.trim() || '',
            posterUrl: this.form.posterUrl?.trim() || '',
            imagenUrl: this.form.imagenUrl?.trim() || ''
        };

        const obs = this.modoEdicion && this.peticionId
            ? this.service.editar(this.peticionId, data)
            : this.service.agregar(data);

        obs.subscribe({
            next: () => {
                this.guardando = false;
                this.modalVisible = false;
                this.mensaje = this.modoEdicion ? '✅ Petición actualizada.' : '✅ Petición agregada.';
                setTimeout(() => this.mensaje = '', 3000);
                this.cargarPeticiones();
                this.cdr.detectChanges();
            },
            error: err => {
                console.error(err);
                this.guardando = false;
                this.error = '❌ No se pudo guardar.';
                this.cdr.detectChanges();
            }
        });
    }

    toggleEstado(item: any): void {
        if (!item?.contenidoVotableId) return;
        const nuevoEstado = !item.activo;
        const accion = nuevoEstado ? 'activar' : 'desactivar';
        
        if (!confirm(`¿${accion} "${item.titulo}"?`)) return;

        const obs = nuevoEstado
            ? this.service.activar(item.contenidoVotableId)
            : this.service.desactivar(item.contenidoVotableId);

        obs.subscribe({
            next: () => {
                this.mensaje = `✅ Petición ${accion}da.`;
                setTimeout(() => this.mensaje = '', 3000);
                this.cargarPeticiones();
            },
            error: err => {
                console.error(err);
                this.error = `❌ No se pudo ${accion}.`;
                this.cdr.detectChanges();
            }
        });
    }

    eliminar(item: any): void {
        if (!item?.contenidoVotableId || !confirm(`¿Eliminar "${item.titulo}"?`)) return;

        this.service.eliminar(item.contenidoVotableId).subscribe({
            next: () => {
                this.mensaje = '✅ Petición eliminada.';
                setTimeout(() => this.mensaje = '', 3000);
                this.cargarPeticiones();
            },
            error: err => {
                console.error(err);
                this.error = '❌ No se pudo eliminar.';
                this.cdr.detectChanges();
            }
        });
    }

}