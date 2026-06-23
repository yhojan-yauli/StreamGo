import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { PeticionesClienteService } from '../../services/peticiones-cliente';

@Component({ selector: 'app-peticiones-client', imports: [CommonModule, RouterLink, NavbarClient], templateUrl: './peticiones-client.html', styleUrl: './peticiones-client.scss' })
export class PeticionesClient implements OnInit {
  private peticionesService = inject(PeticionesClienteService); private cdr = inject(ChangeDetectorRef);
  peticiones: any[] = []; seleccionadas = new Set<number>(); cargando = false; votandoId: number | null = null; mensaje = ''; error = '';
  ngOnInit(): void { this.cargarPeticiones(); this.cargarSeleccionadasLocal(); }
  cargarPeticiones(): void { this.cargando = true; this.error = ''; this.mensaje = ''; this.peticionesService.listar().subscribe({ next: data => { this.peticiones = Array.isArray(data) ? data.filter(item => item.activo !== false) : []; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error(err); this.peticiones = []; this.cargando = false; this.error = 'No se pudieron cargar las peticiones.'; this.cdr.detectChanges(); } }); }
  elegirPeticion(item: any): void { if (!item?.id) { this.error = 'Petición no válida.'; return; } if (this.seleccionadas.has(item.id)) { this.mensaje = 'Ya marcaste esta petición como deseada.'; this.error = ''; this.cdr.detectChanges(); return; } this.votandoId = item.id; this.error = ''; this.mensaje = ''; this.peticionesService.elegir(item.id).subscribe({ next: () => { this.seleccionadas.add(item.id); this.guardarSeleccionadasLocal(); this.votandoId = null; this.mensaje = `Marcaste "${item.titulo}" como deseada.`; this.cdr.detectChanges(); }, error: err => { console.error(err); this.votandoId = null; this.error = 'No se pudo registrar la petición.'; this.cdr.detectChanges(); } }); }
  estaSeleccionada(id: number): boolean { return this.seleccionadas.has(id); }
  imagen(item: any): string { return item?.imagenUrl || item?.posterUrl || '/background.png'; }
  cargarSeleccionadasLocal(): void { const guardado = localStorage.getItem('peticionesSeleccionadasCliente'); if (!guardado) return; try { const ids = JSON.parse(guardado); this.seleccionadas = new Set(Array.isArray(ids) ? ids : []); } catch { this.seleccionadas = new Set<number>(); } }
  guardarSeleccionadasLocal(): void { localStorage.setItem('peticionesSeleccionadasCliente', JSON.stringify(Array.from(this.seleccionadas))); }
}
