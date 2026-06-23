import { Component } from '@angular/core';
import { NavbarPublic } from '../../componentes/navbar-public/navbar-public';
import { Router,RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [NavbarPublic,RouterLink,FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
   nombre = '';
  email = '';
  password = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

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
}
