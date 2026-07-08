import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  template: `
    <div style="text-align: center; margin-top: 20%;">
      <h2>Procesando inicio de sesión en STREAMGO...</h2>
      <p>Por favor espera un momento.</p>
    </div>
  `
})
export class Oauth2RedirectComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  ngOnInit(): void {
    // Capturamos el token de los parámetros de la URL (?token=...)
    this.route.queryParams.subscribe(params => {
      const token = params['token'];

      if (token) {
        // Guardamos el token en el localStorage (usa la misma clave que ya manejas)
        localStorage.setItem('token', token);

        const decoded: any = JSON.parse(atob(token.split('.')[1]));
        const role = decoded.rol;
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/home']);
        } else {
          this.router.navigate(['/client/home']);
        }
      } else {
        // Si algo falló, de vuelta al login
        this.router.navigate(['/login']);
      }
    });
  }
}
