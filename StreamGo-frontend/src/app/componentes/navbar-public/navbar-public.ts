import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar-public',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar-public.html',
  styleUrl: './navbar-public.scss',
})
export class NavbarPublic {
  @Input() showAuthButtons: boolean = true;
}
