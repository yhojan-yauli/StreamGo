<div align="center">

# StreamGo

### Plataforma de Streaming

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](#)

<br>

Plataforma de streaming desarrollada como proyecto integrador de Ingenieria de Software.

Administra contenido multimedia, suscripciones, pagos y una comunidad activa con sistema de votaciones y noticias.

</div>

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE                                   │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐   │
│  │  Navegador │  │   Móvil   │  │  Tablet   │  │  Smart TV │   │
│  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘   │
└────────┼───────────────┼───────────────┼───────────────┼─────────┘
         │               │               │               │
         └───────────────┴───────┬───────┴───────────────┘
                                 │ HTTPS
                    ┌────────────▼────────────┐
                    │    FRONTEND (Angular)    │
                    │   Angular 21 + TS 5.9   │
                    │    Deploy: Netlify       │
                    └────────────┬────────────┘
                                 │ REST API
                    ┌────────────▼────────────┐
                    │   BACKEND (Spring Boot)  │
                    │   Java 21 + Spring 4.0   │
                    │   JWT + OAuth2 + Swagger │
                    │    Deploy: Render        │
                    └────────────┬────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                   │
     ┌────────▼────────┐ ┌──────▼──────┐  ┌────────▼────────┐
     │   MySQL 8.0     │ │  MercadoPago│  │   Google OAuth2  │
     │   Base de datos  │ │  Pagos      │  │   Autenticacion  │
     └─────────────────┘ └─────────────┘  └─────────────────┘
```

---

## Funcionalidades

### Visitante (sin cuenta)

| Funcion | Descripcion |
|---------|-------------|
| Ver contenido publico | Peliculas y series disponibles sin registro |
| Reproducir contenido | Player con control de calidad, volumen y pantalla completa |
| Buscar contenido | Busqueda por titulo, categoria y tipo |
| Ver noticias | Noticias y novedades de la plataforma |
| Registrarse | Crear cuenta nueva o vincular con Google |

### Cliente (suscriptor)

| Funcion | Descripcion |
|---------|-------------|
| Dashboard personalizado | Contenido recomendado segun suscripcion |
| Reproducir contenido completo | Acceso a todo el catalogo segun plan |
| Sistema de votaciones | Proponer y votar contenido futuro |
| Historial de reproduccion | Registro de todo lo visto |
| Gestionar suscripcion | Ver plan actual, cambiar o cancelar |
| Noticias interactivas | Comentar y reaccionar a noticias |
| Mi cuenta | Editar perfil y datos personales |

### Administrador

| Funcion | Descripcion |
|---------|-------------|
| Dashboard con metricas | Resumen de usuarios, contenido y suscripciones |
| Gestion de contenido | CRUD completo con subida de archivos multimedia |
| Gestion de usuarios | Administrar estados y roles |
| Gestion de planes | Crear, editar y eliminar planes de suscripcion |
| Gestion de suscripciones | Ver y administrar suscripciones activas |
| Gestion de noticias | Publicar, fijar y administrar noticias |
| Gestion de peticiones | Administrar votaciones y contenido propuesto |
| Historial del sistema | Registro completo de actividad |

---

## Tecnologias

### Backend

| Componente | Tecnologia | Version |
|------------|-----------|---------|
| Framework | Spring Boot | 4.0.6 |
| Lenguaje | Java | 21 |
| Seguridad | Spring Security + JWT | - |
| OAuth2 | Google Login | - |
| ORM | Spring Data JPA / Hibernate | - |
| Base de datos | MySQL | 8.0 |
| Pagos | MercadoPago SDK | 2.1.28 |
| Documentacion | SpringDoc OpenAPI (Swagger) | 3.0.2 |
| Build | Gradle (Kotlin DSL) | - |
| Contenedor | Docker | - |

### Frontend

| Componente | Tecnologia | Version |
|------------|-----------|---------|
| Framework | Angular | 21.2 |
| Lenguaje | TypeScript | 5.9 |
| Estilos | SCSS + Tailwind CSS | 4.3 |
| UI Components | Bootstrap | 5.3 |
| Routing | Angular Router | - |
| HTTP | Angular HttpClient + Interceptor JWT | - |
| Build | Angular CLI | 21.2 |

---

## Estructura del Proyecto

```
StreamGo/
├── StreamGo-backend/                    # API REST
│   └── src/main/java/com/StreamGo/
│       ├── config/                      # Configuracion general
│       ├── controller/                  # 22 controladores REST
│       │   ├── AuthController.java      # Autenticacion
│       │   ├── ContenidoAdminController.java
│       │   ├── ReproduccionController.java
│       │   └── ...
│       ├── dto/                         # Data Transfer Objects
│       │   ├── request/                 # DTOs de entrada
│       │   └── response/                # DTOs de salida
│       ├── entity/                      # Entidades JPA (12)
│       │   ├── Usuario.java
│       │   ├── Contenido.java
│       │   ├── Plan.java
│       │   └── ...
│       ├── repository/                  # Repositorios JPA
│       ├── security/                    # JWT + Filtros
│       └── service/                     # Logica de negocio (15)
│
├── StreamGo-frontend/                   # Aplicacion Angular
│   └── src/app/
│       ├── componentes/                 # Componentes reutilizables
│       │   ├── ad-banner/              # Sistema de publicidad
│       │   ├── video-player/           # Player multimedia custom
│       │   ├── navbar-public/          # Navegacion publica
│       │   ├── navbar-client/          # Navegacion cliente
│       │   ├── sidebar-admin/          # Panel administrativo
│       │   └── ui/                     # Componentes UI genericos
│       ├── guards/                      # AuthGuard, RoleGuard, PublicGuard
│       ├── interceptors/               # JWT Interceptor
│       ├── models/                      # Modelos TypeScript
│       ├── pages-public/               # Paginas publicas (5)
│       ├── pages-client/               # Paginas cliente (7)
│       ├── pages-admin/                # Paginas admin (7)
│       └── services/                   # Servicios API (16)
│
├── docker-compose.yml                   # Orquestacion Docker
└── README.md
```

---

## Endpoints Principales

### Autenticacion

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `/auth/login` | Iniciar sesion |
| POST | `/auth/register` | Registrar usuario |
| GET | `/auth/google-init` | Login con Google |
| GET | `/auth/google-callback` | Callback OAuth2 |

### Contenido

| Metodo | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/public/contenidos` | Publico |
| POST | `/public/reproduccion/{id}` | Publico |
| GET | `/contenidos/suscriptor` | Cliente |
| POST | `/reproduccion/{id}` | Cliente |
| GET/POST/PUT/DELETE | `/admin/contenidos/**` | Admin |

### Suscripciones y Pagos

| Metodo | Endpoint | Acceso |
|--------|----------|--------|
| GET | `/public/planes` | Publico |
| GET | `/cliente/planes` | Cliente |
| POST | `/payments/create` | Cliente |
| GET | `/cliente/suscripciones/mi-suscripcion` | Cliente |

### Mas endpoints documentados en Swagger UI

Acceder a: `http://localhost:8080/swagger-ui.html`

---

## Instalacion

### Requisitos

- Java 21+
- Node.js 18+
- npm 11+
- MySQL 8.0+
- Git

### Backend

```bash
# Clonar
git clone https://github.com/yhojan-yauli/StreamGo.git
cd StreamGo/StreamGo-backend

# Configurar base de datos
# Editar src/main/resources/application.properties

# Crear base de datos
mysql -u root -p -e "CREATE DATABASE streamgo"

# Ejecutar
./gradlew bootRun
```

El backend estara disponible en: `http://localhost:8080`

### Frontend

```bash
cd StreamGo/StreamGo-frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
ng serve

# Build para produccion
ng build --configuration production
```

El frontend estara disponible en: `http://localhost:4200`

### Docker

```bash
docker-compose up -d
```

---

## Seguridad

- **JWT**: Tokens de acceso con expiracion configurable
- **Roles**: CLIENTE y ADMIN con endpoints protegidos por `RoleGuard`
- **OAuth2**: Login con Google como metodo alternativo
- **Interceptors**: El frontend envia automaticamente el token en cada peticion
- **CORS**: Configurado para desarrollo y produccion

---

## Publicidad

El reproductor publico integra anuncios de **Adsterra**:

- **Banners en sidebar**: Componente `AdBannerComponent` reutilizable
- **Anuncio interstitial**: Se ejecuta en cada play/pause del video
- **Configuracion**: Los scripts de Adsterra se definen en `reproducir-publico.ts`

---

## Deployment

| Servicio | Componente | URL |
|----------|-----------|-----|
| Render | Backend (Spring Boot) | `https://streamgo-backend-3ex8.onrender.com` |
| Netlify | Frontend (Angular) | `https://streamgoaqp.netlify.app` |

---

## Diagrama de Casos de Uso

<p align="center"><img src="casos de uso.png" alt="Diagrama de Casos de Uso" width="800"></p>

---

## Equipo de Desarrollo

| Nombre | Rol | GitHub |
|--------|-----|--------|
| **Yhojan Yauli** | Full Stack - Backend & Frontend | [@yhojan-yauli](https://github.com/yhojan-yauli) |
| **Levi** | Backend - Peticiones & Votaciones | - |
| **Frans** | Backend - Contenido & Reproduccion | - |
| **Bryan** | Backend - Noticias & Comunidad | - |
| **Cristian** | Backend - Soporte | - |

---

## Licencia

Proyecto academico - Ingenieria de Software

Universidad Tecnologica del Peru (UTP)
