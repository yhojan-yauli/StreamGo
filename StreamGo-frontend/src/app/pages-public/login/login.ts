import { Component } from '@angular/core';
import { NavbarPublic } from "../../componentes/navbar-public/navbar-public";
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-login',
  imports: [NavbarPublic,RouterLink,FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
   email = '';
  password = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

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
}