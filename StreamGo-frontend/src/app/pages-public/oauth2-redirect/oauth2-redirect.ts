import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoadingScreenComponent } from '../../componentes/ui/loading-screen/loading-screen';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  imports: [LoadingScreenComponent],
  template: `
    <app-loading-screen [visible]="loading" mensaje="Iniciando sesión..."></app-loading-screen>
  `
})
export class Oauth2RedirectComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  loading = true;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];

      if (token) {
        localStorage.setItem('token', token);

        const decoded: any = JSON.parse(atob(token.split('.')[1]));
        const role = decoded.rol;
        setTimeout(() => {
          if (role === 'ADMIN') {
            this.router.navigate(['/admin/home']);
          } else {
            this.router.navigate(['/client/home']);
          }
        }, 800);
      } else {
        this.router.navigate(['/login']);
      }
    });
  }
}
