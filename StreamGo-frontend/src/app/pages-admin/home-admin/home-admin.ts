import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({ selector: 'app-home-admin', imports: [CommonModule], templateUrl: './home-admin.html', styleUrl: './home-admin.scss' })
export class HomeAdmin { constructor(private router: Router) {} irA(ruta: string): void { this.router.navigateByUrl(ruta); } }
