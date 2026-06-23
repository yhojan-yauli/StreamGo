import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({ selector: 'app-reproducir-client', imports: [CommonModule, RouterLink, NavbarClient], templateUrl: './reproducir-client.html', styleUrl: './reproducir-client.scss' })
export class ReproducirClient implements OnInit {
  private route = inject(ActivatedRoute); private router = inject(Router); private sanitizer = inject(DomSanitizer); private contenidoService = inject(ContenidoClienteService); private cdr = inject(ChangeDetectorRef);
  contenidoId = 0; contenido: any = null; reproduccion: any = null; relacionados: any[] = []; videoSeguro: SafeResourceUrl | null = null; cargando = false; error = '';
  ngOnInit(): void { this.route.paramMap.subscribe(params => { this.contenidoId = Number(params.get('id')); const guardado = localStorage.getItem('clientContenido'); if (guardado) this.contenido = JSON.parse(guardado); this.reproducir(); this.cargarRelacionados(); }); }
  reproducir(): void { if (!this.contenidoId) { this.error = 'Contenido no válido.'; return; } this.cargando = true; this.error = ''; this.videoSeguro = null; this.contenidoService.reproducirCliente(this.contenidoId).subscribe({ next: data => { this.reproduccion = data; const videoUrl = data?.videoUrl || this.contenido?.videoUrl || ''; if (videoUrl) this.videoSeguro = this.sanitizer.bypassSecurityTrustResourceUrl(videoUrl); this.cargando = false; this.cdr.detectChanges(); }, error: err => { console.error('Error al reproducir:', err); this.error = 'No se pudo reproducir este contenido. Verifica tu plan o suscripción.'; this.cargando = false; this.cdr.detectChanges(); } }); }
  cargarRelacionados(): void { this.contenidoService.recomendados().subscribe({ next: data => { this.relacionados = Array.isArray(data) ? data.filter(item => item.id !== this.contenidoId).slice(0, 3) : []; this.cdr.detectChanges(); }, error: () => { this.relacionados = []; this.cdr.detectChanges(); } }); }
  titulo(): string { return this.reproduccion?.titulo || this.contenido?.titulo || 'Reproduciendo contenido'; }
  descripcion(): string { return this.contenido?.descripcion || this.reproduccion?.mensaje || 'Contenido disponible en StreamGO.'; }
  imagen(item: any): string { return item?.bannerUrl || item?.imagenUrl || '/background.png'; }
  verRelacionado(item: any): void { if (!item?.id) return; localStorage.setItem('clientContenido', JSON.stringify(item)); this.router.navigate(['/client/reproducir', item.id]); }
}
