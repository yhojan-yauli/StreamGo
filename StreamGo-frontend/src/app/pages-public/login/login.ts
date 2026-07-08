import { Component, OnInit } from '@angular/core';
import { NavbarPublic } from "../../componentes/navbar-public/navbar-public";
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-login',
  imports: [NavbarPublic, RouterLink, FormsModule, NgIf], // 3. Añadimos NgIf aquí
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit { // 4. Implementamos OnInit
  email = '';
  password = '';
  
  // Variables para controlar los mensajes informativos
  mensajeInfo: string | null = null;
  mensajeError: string | null = null;

  constructor(
    private authService: Auth,
    private router: Router,
    private route: ActivatedRoute // 5. Inyectamos ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 6. Escuchamos los parámetros que vienen de las redirecciones del Backend
    this.route.queryParams.subscribe(params => {
      if (params['registro'] === 'exitoso') {
        this.mensajeInfo = '¡Registro exitoso con Google! Tu cuenta ha sido creada en StreamGO. Ahora puedes iniciar sesión.';
        this.mensajeError = null;
      }
      
      if (params['error'] === 'usuario_no_registrado') {
        this.mensajeError = 'Tu cuenta de Google no está registrada en el sistema. Por favor, ve a la sección de Registro.';
        this.mensajeInfo = null;
      }
    });
  }

  login() {
    const data = {
      email: this.email,
      password: this.password
    };

    this.authService.login(data).subscribe({
      next: (res: any) => {
        // 1. guardar token
        this.authService.saveToken(res.token);

        // 2. leer rol del JWT
        const role = this.authService.getRole();

        console.log("ROL:", role);

        // 3. redirigir según rol
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/home']);
        } else {
          this.router.navigate(['/client/home']);
        }
      },
      error: () => {
        alert('Credenciales incorrectas');
      }
    });
  }

  loginConGoogle(): void {
    window.location.href = `${environment.apiUrl}/auth/google-init?action=login`;
  }
}
