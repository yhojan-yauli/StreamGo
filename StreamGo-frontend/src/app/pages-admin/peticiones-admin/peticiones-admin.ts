import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarAdmin } from '../../componentes/sidebar-admin/sidebar-admin';
import { PeticionesAdminService } from '../../services/peticiones-admin';

@Component({
    selector: 'app-peticiones-admin',
    imports: [CommonModule, FormsModule, SidebarAdmin],
    templateUrl: './peticiones-admin.html',
    styleUrl: './peticiones-admin.scss'
})
export class PeticionesAdmin implements OnInit {

    // ============================================================
    // INYECCIONES
    // ============================================================
    private service = inject(PeticionesAdminService);
    private cdr = inject(ChangeDetectorRef);

    // ============================================================
    // VARIABLES DE ESTADO
    // ============================================================
    ranking: any[] = [];
    cargando = false;
    guardando = false;
    modalVisible = false;
    modoEdicion = false;
    peticionId: number | null = null;
    mensaje = '';
    error = '';

    // ============================================================
    // FORMULARIO
    // ============================================================
    form: any = {
        titulo: '',
        descripcion: '',
        posterUrl: '',
        imagenUrl: ''
    };

    // ============================================================
    // CICLO DE VIDA - OnInit
    // ============================================================
    ngOnInit(): void {
        this.cargarRanking();
    }

    // ============================================================
    // MÉTODOS PRINCIPALES
    // ============================================================

    /**
     * Carga el ranking de peticiones desde el servicio
     */
    cargarRanking(): void {
        this.cargando = true;
        this.service.ranking().subscribe({
            next: data => {
                this.ranking = Array.isArray(data) ? [...data] : [];
                this.cargando = false;
                this.cdr.detectChanges();
            },
            error: err => {
                console.error(err);
                this.ranking = [];
                this.cargando = false;
                this.error = 'No se pudo cargar el ranking.';
                this.cdr.detectChanges();
            }
        });
    }

    /**
     * Abre el modal para crear una nueva petición
     */
    nueva(): void {
        this.modoEdicion = false;
        this.peticionId = null;
        this.form = {
            titulo: '',
            descripcion: '',
            posterUrl: '',
            imagenUrl: ''
        };
        this.modalVisible = true;
        this.cdr.detectChanges();
    }

    /**
     * Abre el modal para editar una petición existente
     * @param item Petición a editar
     */
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

    /**
     * Cierra el modal
     */
    cerrar(): void {
        this.modalVisible = false;
        this.peticionId = null;
        this.cdr.detectChanges();
    }

    /**
     * Guarda una petición (crea o actualiza)
     */
    guardar(): void {
        if (!this.form.titulo.trim()) {
            this.error = 'El título es obligatorio.';
            return;
        }

        this.guardando = true;

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
                this.mensaje = this.modoEdicion
                    ? 'Petición actualizada.'
                    : 'Petición agregada.';
                this.cargarRanking();
            },
            error: err => {
                console.error(err);
                this.guardando = false;
                this.error = 'No se pudo guardar la petición.';
                this.cdr.detectChanges();
            }
        });
    }

    /**
     * Desactiva una petición (solicita confirmación)
     * @param item Petición a desactivar
     */
    desactivar(item: any): void {
        if (!item?.contenidoVotableId || !confirm(`¿Desactivar "${item.titulo}"?`)) {
            return;
        }

        this.service.desactivar(item.contenidoVotableId).subscribe({
            next: () => {
                this.mensaje = 'Petición desactivada.';
                this.cargarRanking();
            },
            error: err => {
                console.error(err);
                this.error = 'No se pudo desactivar.';
                this.cdr.detectChanges();
            }
        });
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /**
     * Calcula el porcentaje de votos para la barra de progreso
     * @param votos Número de votos del item
     * @returns Porcentaje redondeado (0-100)
     */
    porcentaje(votos: number): number {
        const max = Math.max(
            ...this.ranking.map(x => Number(x.totalVotos || 0)),
            1
        );
        return Math.round((Number(votos || 0) / max) * 100);
    }

}