import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { urlCompleta } from '../../services/api';

@Component({ selector: 'app-historial-client', imports: [CommonModule, RouterLink, NavbarClient], templateUrl: './historial-client.html', styleUrl: './historial-client.scss' })
export class HistorialClient implements OnInit { private contenidoService = inject(ContenidoClienteService); private router = inject(Router); private cdr = inject(ChangeDetectorRef); historial: any[] = []; cargando = false; error = ''; ngOnInit(): void { this.cargarHistorial(); } cargarHistorial(): void { this.cargando = true; this.contenidoService.historial().subscribe({ next: data => { this.historial = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error(err); this.historial = []; this.cargando = false; this.error = 'No se pudo cargar tu historial.'; this.cdr.detectChanges(); } }); } imagen(item: any): string { return urlCompleta(item?.imagenUrl); } progreso(item: any): number { if (item?.completado) return 100; const segundos = Number(item?.progresoSegundos || 0); if (segundos <= 0) return 20; return Math.min(95, Math.max(20, segundos / 10)); } continuar(item: any): void { if (!item?.contenidoId) return; localStorage.setItem('clientContenido', JSON.stringify({ id: item.contenidoId, titulo: item.titulo, imagenUrl: item.imagenUrl, categoria: item.categoria })); this.router.navigate(['/client/reproducir', item.contenidoId]); } }
