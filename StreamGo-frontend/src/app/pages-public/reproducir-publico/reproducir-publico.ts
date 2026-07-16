import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { VideoPlayer } from '../../componentes/video-player/video-player';
import { AdBannerComponent } from '../../componentes/ad-banner/ad-banner';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({
  selector: 'app-reproducir-publico',
  imports: [CommonModule, NavbarPublic, VideoPlayer, AdBannerComponent],
  templateUrl: './reproducir-publico.html',
  styleUrl: './reproducir-publico.scss',
})
export class ReproducirPublico implements OnInit {
  private route = inject(ActivatedRoute);
  private contenidoService = inject(ContenidoClienteService);
  private cdr = inject(ChangeDetectorRef);

  contenidoId = 0;
  contenido: any = null;
  reproduccion: any = null;
  cargando = false;
  error = '';

  ads: string[] = [
    `<script src="https://pl30384310.effectivecpmnetwork.com/cd/17/e5/cd17e5ac7ce9f02279764089f6a86fe1.js"></script>`,
    `<script>atOptions={'key':'e50c6ee07679c8c0d7b2da54b0069224','format':'iframe','height':60,'width':468,'params':{}};</script><script src="https://www.highperformanceformat.com/e50c6ee07679c8c0d7b2da54b0069224/invoke.js"></script>`,
  ];

  ngOnInit(): void {
    this.contenidoId = Number(this.route.snapshot.paramMap.get('id'));
    const guardado = localStorage.getItem('publicContenido');
    if (guardado) this.contenido = JSON.parse(guardado);
    this.reproducir();
  }

  get videoUrl(): string {
    return this.reproduccion?.videoUrl || this.contenido?.videoUrl || '';
  }

  reproducir(): void {
    if (!this.contenidoId) { this.error = 'Contenido no válido'; return; }
    this.cargando = true;
    this.contenidoService.reproducirPublico(this.contenidoId).subscribe({
      next: (data) => {
        this.reproduccion = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error reproduciendo público:', err);
        this.error = 'No se pudo reproducir este contenido.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  titulo(): string { return this.reproduccion?.titulo || this.contenido?.titulo || 'Reproduciendo contenido'; }
  descripcion(): string { return this.contenido?.descripcion || this.reproduccion?.mensaje || 'Contenido disponible en StreamGO.'; }
}
