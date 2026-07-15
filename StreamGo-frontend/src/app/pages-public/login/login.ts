import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { NavbarPublic } from "../../componentes/navbar-public/navbar-public";
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Auth } from '../../services/auth';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { LoadingScreenComponent } from '../../componentes/ui/loading-screen/loading-screen';
import { timeout, TimeoutError, finalize, catchError, throwError } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [NavbarPublic, RouterLink, ReactiveFormsModule, CommonModule, LoadingScreenComponent],
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
  loading = false;
  submitting = false;

  constructor(
    private authService: Auth,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['registro'] === 'exitoso') {
        this.mensajeInfo = '¡Cuenta creada exitosamente! Ya puedes iniciar sesión.';
        this.mensajeError = null;
      }
      if (params['error'] === 'usuario_no_registrado') {
        this.mensajeError = 'Tu cuenta de Google no está registrada. Crea una cuenta primero.';
        this.mensajeInfo = null;
      }
      this.cdr.detectChanges();
    });
  }

  get f() { return this.loginForm.controls; }

  login() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.mensajeError = null;
    this.submitting = true;
    this.cdr.detectChanges();

    this.authService.login(this.loginForm.value)
      .pipe(
        timeout(12000),
        catchError((err) => {
          if (err instanceof TimeoutError) {
            return throwError(() => ({ type: 'timeout' }));
          }
          return throwError(() => err);
        }),
        finalize(() => {
          this.submitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: any) => {
          this.loading = true;
          this.authService.saveToken(res.token);
          const role = this.authService.getRole();
          this.cdr.detectChanges();
          setTimeout(() => {
            if (role === 'ADMIN') {
              this.router.navigate(['/admin/home']);
            } else {
              this.router.navigate(['/client/home']);
            }
          }, 800);
        },
        error: (err) => {
          if (err.type === 'timeout') {
            this.mensajeError = 'El servidor está tardando. Intenta de nuevo.';
          } else if (err.status === 400) {
            this.mensajeError = 'Credenciales incorrectas.';
          } else {
            this.mensajeError = 'Error de conexión.';
          }
          this.mensajeInfo = null;
          this.cdr.detectChanges();
        }
      });
  }

  loginConGoogle(): void {
    window.location.href = `${environment.apiUrl}/auth/google-init?action=login`;
  }
}
