import { Component, OnInit } from '@angular/core';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Auth } from '../../services/auth';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-register',
  imports: [NavbarPublic, RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register implements OnInit {

  registerForm = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(2)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

  mensajeError: string | null = null;

  constructor(
    private authService: Auth,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['error'] === 'ya_existe') {
        this.mensajeError = 'Este correo electrónico ya está registrado.';
      }
    });
  }

  get f() { return this.registerForm.controls; }

  register() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.authService.register(this.registerForm.value).subscribe({
      next: (res: any) => {
        localStorage.setItem('token', res.token);
        this.router.navigate(['/client/home']);
      },
      error: () => {
        this.mensajeError = 'Error al registrar. Intenta con otro correo.';
      }
    });
  }

  registerConGoogle(): void {
    window.location.href = `${environment.apiUrl}/auth/google-init?action=register`;
  }
}
