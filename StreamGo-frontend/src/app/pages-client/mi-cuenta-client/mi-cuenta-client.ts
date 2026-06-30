import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarClient } from '../../componentes/navbar-client/navbar-client';
import { Auth } from '../../services/auth';
import { ClientePlanesService } from '../../services/cliente-planes';

@Component({ selector: 'app-mi-cuenta-client', imports: [CommonModule, RouterLink, NavbarClient], templateUrl: './mi-cuenta-client.html', styleUrl: './mi-cuenta-client.scss' })
export class MiCuentaClient implements OnInit { private auth = inject(Auth); private planesService = inject(ClientePlanesService); private router = inject(Router); private cdr = inject(ChangeDetectorRef); usuario: any = null; suscripcion: any = null; mensaje = ''; error = ''; ngOnInit(): void { this.usuario = this.auth.getUser(); this.cargarSuscripcion(); } cargarSuscripcion(): void { this.planesService.miSuscripcion().subscribe({ next: data => { this.suscripcion = data; this.cdr.detectChanges(); }, error: () => { this.suscripcion = null; this.cdr.detectChanges(); } }); } verificarSuscripcion(): void { this.mensaje = ''; this.error = ''; this.planesService.verificarSuscripcion().subscribe({ next: data => { this.suscripcion = data; this.mensaje = 'Suscripción verificada correctamente.'; this.cdr.detectChanges(); }, error: err => { console.error(err); this.error = 'No se pudo verificar la suscripción.'; this.cdr.detectChanges(); } }); }  nombre(): string {
    return this.usuario?.nombre || this.usuario?.name || this.usuario?.email || this.usuario?.sub || '';
 } email(): string { return this.usuario?.email || this.usuario?.sub || 'Sin correo'; } rol(): string { return this.usuario?.rol || this.usuario?.role || 'CLIENTE'; } inicial(): string { return this.nombre().charAt(0).toUpperCase(); } cerrarSesion(): void { this.auth.logout(); this.router.navigate(['/login']); } }
