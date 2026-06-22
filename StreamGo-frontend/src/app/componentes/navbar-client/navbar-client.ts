import { Component } from '@angular/core';
import { Auth } from '../../services/auth';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar-client',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar-client.html',
  styleUrl: './navbar-client.scss',
})
export class NavbarClient {

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
