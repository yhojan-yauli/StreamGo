import { Component, OnInit } from '@angular/core';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-register',
  imports: [NavbarPublic, RouterLink, FormsModule, NgIf], // 4. Añadimos NgIf aquí
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register implements OnInit { // 5. Implementamos OnInit
  nombre = '';
  email = '';
  password = '';

  // Variable para controlar el mensaje de error en la interfaz
  mensajeError: string | null = null;

  constructor(
    private authService: Auth,
    private router: Router,
    private route: ActivatedRoute // 6. Inyectamos ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 7. Escuchamos si el backend nos redirige con el error de cuenta existente
    this.route.queryParams.subscribe(params => {
      if (params['error'] === 'ya_existe') {
        this.mensajeError = 'Este correo electrónico ya está registrado en StreamGO. Intenta iniciar sesión.';
      }
    });
  }

  register() {
    const data = {
      nombre: this.nombre,
      email: this.email,
      password: this.password
    };

    this.authService.register(data).subscribe({
      next: (res: any) => {
        console.log('REGISTER OK:', res);
        localStorage.setItem('token', res.token);
        this.router.navigate(['/client/home']);
      },
      error: (err) => {
        console.error(err);
        alert('Error al registrar');
      }
    });
  }

  registerConGoogle(): void {
    window.location.href = `${environment.apiUrl}/auth/google-init?action=register`;
  }
}
