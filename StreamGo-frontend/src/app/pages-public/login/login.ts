import { Component, OnInit } from '@angular/core';
import { NavbarPublic } from "../../componentes/navbar-public/navbar-public";
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Auth } from '../../services/auth';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-login',
  imports: [NavbarPublic, RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

  mensajeInfo: string | null = null;
  mensajeError: string | null = null;

  constructor(
    private authService: Auth,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['registro'] === 'exitoso') {
        this.mensajeInfo = '¡Registro exitoso con Google! Ya puedes iniciar sesión.';
        this.mensajeError = null;
      }
      if (params['error'] === 'usuario_no_registrado') {
        this.mensajeError = 'Tu cuenta de Google no está registrada. Crea una cuenta primero.';
        this.mensajeInfo = null;
      }
    });
  }

  get f() { return this.loginForm.controls; }

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (res: any) => {
        this.authService.saveToken(res.token);
        const role = this.authService.getRole();
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/home']);
        } else {
          this.router.navigate(['/client/home']);
        }
      },
      error: () => {
        this.mensajeError = 'Credenciales incorrectas.';
        this.mensajeInfo = null;
      }
    });
  }

  loginConGoogle(): void {
    window.location.href = `${environment.apiUrl}/auth/google-init?action=login`;
  }
}
