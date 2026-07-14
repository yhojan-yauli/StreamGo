import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { UsuariosService } from '../../services/usuarios';
import { CommonModule } from '@angular/common';

@Component({ selector: 'app-usuarios', imports: [CommonModule], templateUrl: './usuarios.html', styleUrl: './usuarios.scss' })
export class Usuarios implements OnInit { private usuariosService = inject(UsuariosService); private cdr = inject(ChangeDetectorRef); usuarios: any[] = []; cargando = false; ngOnInit(): void { this.cargarUsuarios(); } cargarUsuarios(): void { this.cargando = true; this.usuariosService.listar().subscribe({ next: data => { this.usuarios = Array.isArray(data) ? [...data] : []; this.cargando = false; this.cdr.detectChanges(); }, error: error => { console.error('Error al cargar usuarios:', error); this.usuarios = []; this.cargando = false; this.cdr.detectChanges(); } }); } }
