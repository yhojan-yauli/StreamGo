import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PeticionesAdminService } from '../../services/peticiones-admin';

@Component({
  selector: 'app-peticiones-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './peticiones-admin.html',
  styleUrl: './peticiones-admin.scss'
})
export class PeticionesAdmin implements OnInit {
  private service = inject(PeticionesAdminService);
  private cdr = inject(ChangeDetectorRef);

  // Propiedades del ranking
  ranking: any[] = [];
  cargando = false;

  // Propiedades de la lista completa
  listaCompleta: any[] = [];      // 👈 Declarada
  cargandoLista = false;           // 👈 Declarada

  // Propiedades del modal
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
    this.cargarRanking();
    this.cargarListaCompleta(); // 👈 Carga ambas al inicio
  }

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

  cargarListaCompleta(): void {   // 👈 Método implementado
    this.cargandoLista = true;
    this.service.lista().subscribe({
      next: data => {
        this.listaCompleta = Array.isArray(data) ? [...data] : [];
        this.cargandoLista = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error(err);
        this.listaCompleta = [];
        this.cargandoLista = false;
        this.error = 'No se pudo cargar la lista completa.';
        this.cdr.detectChanges();
      }
    });
  }

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

editar(item: any): void {
  // ✅ Busca el ID en cualquier propiedad posible
  const id = item.contenidoVotableId || item.id || item.peticionId || item.contenidoId;
  
  console.log('✏️ Editando item:', item);
  console.log('🆔 ID encontrado:', id);
  
  if (!id) {
    console.error('❌ No se encontró ID en el objeto:', item);
    this.error = 'Error: No se puede identificar la petición.';
    return;
  }

  this.modoEdicion = true;
  this.peticionId = id;
  this.form = {
    titulo: item.titulo || '',
    descripcion: item.descripcion || '',
    posterUrl: item.posterUrl || '',
    imagenUrl: item.imagenUrl || ''
  };
  this.modalVisible = true;
  this.cdr.detectChanges();
}

  cerrar(): void {
    this.modalVisible = false;
    this.peticionId = null;
    this.cdr.detectChanges();
  }

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
        this.mensaje = this.modoEdicion ? 'Petición actualizada.' : 'Petición agregada.';
        this.cargarRanking();       // 👈 Recarga ranking
        this.cargarListaCompleta(); // 👈 Recarga lista completa
        this.cdr.detectChanges();
      },
      error: err => {
        console.error(err);
        this.guardando = false;
        this.error = 'No se pudo guardar la petición.';
        this.cdr.detectChanges();
      }
    });
  }

desactivar(item: any): void {
  // ✅ Busca el ID en cualquier propiedad posible
  const id = item.contenidoVotableId || item.id || item.peticionId || item.contenidoId;
  
  if (!id) {
    console.error('❌ No se encontró ID para desactivar:', item);
    return;
  }

  if (!confirm(`¿Seguro que quieres desactivar "${item.titulo}"?`)) {
    return;
  }

  console.log(`🗑️ Desactivando ID: ${id}`);
  
  this.service.desactivar(id).subscribe({
    next: () => {
      this.mensaje = 'Petición desactivada.';
      this.cargarRanking();
      this.cargarListaCompleta();
      this.cdr.detectChanges();
    },
    error: err => {
      console.error('❌ Error al desactivar:', err);
      this.error = 'No se pudo desactivar la petición.';
      this.cdr.detectChanges();
    }
  });
}

  porcentaje(votos: number): number {
    const max = Math.max(...this.ranking.map(x => Number(x.totalVotos || 0)), 1);
    return Math.round((Number(votos || 0) / max) * 100);
  }
}
