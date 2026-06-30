import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { VideoPlayer } from '../../componentes/video-player/video-player';
import { ContenidoClienteService } from '../../services/contenido-cliente';
import { urlCompleta } from '../../services/api';

@Component({ selector: 'app-reproducir-client', imports: [CommonModule, RouterLink, NavbarClient, VideoPlayer], templateUrl: './reproducir-client.html', styleUrl: './reproducir-client.scss' })
export class ReproducirClient implements OnInit {
  private route = inject(ActivatedRoute); private router = inject(Router); private contenidoService = inject(ContenidoClienteService); private cdr = inject(ChangeDetectorRef);
  contenidoId = 0; contenido: any = null; reproduccion: any = null; relacionados: any[] = []; cargando = false; error = '';
  ngOnInit(): void { this.route.paramMap.subscribe(params => { this.contenidoId = Number(params.get('id')); const guardado = localStorage.getItem('clientContenido'); if (guardado) this.contenido = JSON.parse(guardado); this.reproducir(); this.cargarRelacionados(); }); }
  get videoUrl(): string { return this.reproduccion?.videoUrl || this.contenido?.videoUrl || ''; }
  reproducir(): void { if (!this.contenidoId) { this.error = 'Contenido no válido.'; return; } this.cargando = true; this.error = ''; this.contenidoService.reproducirCliente(this.contenidoId).subscribe({ next: data => { this.reproduccion = data; this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error('Error al reproducir:', err); this.error = 'No se pudo reproducir este contenido. Verifica tu plan o suscripción.'; this.cargando = false; this.cdr.detectChanges(); } }); }
  cargarRelacionados(): void { this.contenidoService.recomendados().subscribe({ next: data => { this.relacionados = Array.isArray(data) ? data.filter(item => item.id !== this.contenidoId).slice(0, 3) : []; this.cdr.detectChanges(); }, error: () => { this.relacionados = []; this.cdr.detectChanges(); } }); }
  titulo(): string { return this.reproduccion?.titulo || this.contenido?.titulo || 'Reproduciendo contenido'; }
  descripcion(): string { return this.contenido?.descripcion || this.reproduccion?.mensaje || 'Contenido disponible en StreamGO.'; }
  imagen(item: any): string { return urlCompleta(item?.bannerUrl || item?.imagenUrl); }
  verRelacionado(item: any): void { if (!item?.id) return; localStorage.setItem('clientContenido', JSON.stringify(item)); this.router.navigate(['/client/reproducir', item.id]); }
}
