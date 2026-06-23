import { Component, ChangeDetectorRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { ContenidoClienteService } from '../../services/contenido-cliente';

@Component({
  selector: 'app-reproducir-publico',
  imports: [CommonModule, RouterLink, NavbarPublic],
  templateUrl: './reproducir-publico.html',
  styleUrl: './reproducir-publico.scss',
})
export class ReproducirPublico implements OnInit {
  private route = inject(ActivatedRoute);
  private sanitizer = inject(DomSanitizer);
  private contenidoService = inject(ContenidoClienteService);
  private cdr = inject(ChangeDetectorRef);

  contenidoId = 0;
  contenido: any = null;
  reproduccion: any = null;
  videoSeguro: SafeResourceUrl | null = null;
  cargando = false;
  error = '';

  ngOnInit(): void {
    this.contenidoId = Number(this.route.snapshot.paramMap.get('id'));
    const guardado = localStorage.getItem('publicContenido');
    if (guardado) this.contenido = JSON.parse(guardado);
    this.reproducir();
  }

  reproducir(): void {
    if (!this.contenidoId) { this.error = 'Contenido no válido'; return; }
    this.cargando = true;
    this.contenidoService.reproducirPublico(this.contenidoId).subscribe({
      next: (data) => {
        this.reproduccion = data;
        const videoUrl = data?.videoUrl || this.contenido?.videoUrl || '';
        if (videoUrl) this.videoSeguro = this.sanitizer.bypassSecurityTrustResourceUrl(videoUrl);
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
