import { Routes } from '@angular/router';

// PUBLICO
import { HomePublic } from './pages-public/home-public/home-public';
import { Login } from './pages-public/login/login';
import { Register } from './pages-public/register/register';
import { Peliculas } from './pages-public/peliculas/peliculas';
import { ReproducirPublico } from './pages-public/reproducir-publico/reproducir-publico';
import { Oauth2RedirectComponent } from './pages-public/oauth2-redirect/oauth2-redirect';

// CLIENTE
import { HomeClient } from './pages-client/home-client/home-client';
import { ReproducirClient } from './pages-client/reproducir-client/reproducir-client';
import { PlanesClient } from './pages-client/planes-client/planes-client';
import { PeticionesClient } from './pages-client/peticiones-client/peticiones-client';
import { HistorialClient } from './pages-client/historial-client/historial-client';
import { MiCuentaClient } from './pages-client/mi-cuenta-client/mi-cuenta-client';
import { NoticiasClient } from './pages-client/noticias-client/noticias-client';

// ADMIN
import { HomeAdmin } from './pages-admin/home-admin/home-admin';
import { planes } from './pages-admin/planes/planes';
import { Contenido } from './pages-admin/contenido/contenido';
import { Suscripciones } from './pages-admin/suscripciones/suscripciones';
import { Usuarios } from './pages-admin/usuarios/usuarios';
import { NoticiasAdmin } from './pages-admin/noticias-admin/noticias-admin';
import { PeticionesAdmin } from './pages-admin/peticiones-admin/peticiones-admin';

// GUARDS
import { AuthGuard } from './guards/auth-guard';
import { RoleGuard } from './guards/role-guard';
import { PublicGuard } from './guards/public-guard';

export const routes: Routes = [
  // PUBLICO
  { path: '', component: HomePublic, canActivate: [PublicGuard] },
  { path: 'login', component: Login, canActivate: [PublicGuard] },
  { path: 'register', component: Register, canActivate: [PublicGuard] },
  { path: 'peliculas', component: Peliculas, canActivate: [PublicGuard] },
  { path: 'peliculas/:id', component: ReproducirPublico, canActivate: [PublicGuard] },
  { path: 'oauth2/redirect', component: Oauth2RedirectComponent },

  // CLIENTE
  { path: 'client/home', component: HomeClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/reproducir/:id', component: ReproducirClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/planes', component: PlanesClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/peticiones', component: PeticionesClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/historial', component: HistorialClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/mi-cuenta', component: MiCuentaClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },
  { path: 'client/noticias', component: NoticiasClient, canActivate: [AuthGuard, RoleGuard], data: { role: 'CLIENTE' } },

  // ADMIN
  { path: 'admin/home', component: HomeAdmin, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/usuarios', component: Usuarios, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/contenido', component: Contenido, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/noticias', component: NoticiasAdmin, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/peticiones', component: PeticionesAdmin, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/planes', component: planes, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  { path: 'admin/suscripciones', component: Suscripciones, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },

  // FALLBACK
  { path: '**', redirectTo: '' }
];
