# 🎬 Plataforma de StreamGo

Proyecto para el curso de Integrador  

StreamGo es una plataforma de streaming desarrollada con Spring Boot que permite la gestión de usuarios, contenido multimedia, suscripciones, pagos y votaciones.

El proyecto implementa arquitectura REST, seguridad con JWT y documentación con Swagger, permitiendo la administración completa del sistema por roles (CLIENTE y ADMIN).

---

# 🎯 Objetivo del Sistema

Desarrollar una plataforma de streaming con:

- Gestión de usuarios por roles  
- Control de contenido multimedia  
- Sistema de suscripciones  
- Pagos simulados  
- Sistema de votaciones (contenido futuro)  
- Noticias internas  
- Seguridad con JWT  
- API documentada profesionalmente  

---
---


## 🚀 Tecnologías utilizadas

### 🔹 Frontend
- React + Vite  
- Tailwind CSS  
- Axios  
- Autenticación con JWT  

### 🔹 Backend
- Spring Boot  
- Spring Security  
- JWT + OAuth2 (Google)  
- JPA / Hibernate  

### 🔹 Base de datos
- MySQL  

---

## 📁 Estructura del proyecto

plataforma-de-streaming/
│
├── Backend-Streaming/     # API REST (Spring Boot)
├── Frontend-Streaming/    # Frontend (React + Vite)
└── README.md

---

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java 17 o superior  
- Node.js (v18 o superior)  
- MySQL  
- Git  

---

## ▶️ Instalación y ejecución

### 1️⃣ Clonar el repositorio

git clone https://github.com/yhojan-yauli/plataforma-de-streaming.git  
cd plataforma-de-streaming  

---

## 🧠 Backend (Spring Boot)

### 🔧 Configuración

1. Crear la base de datos en MySQL:

CREATE DATABASE streaming_db;

2. Configurar credenciales en:

Backend-Streaming/src/main/resources/application.properties

Ejemplo:

spring.datasource.url=jdbc:mysql://localhost:3306/streaming_db  
spring.datasource.username=TU_USUARIO  
spring.datasource.password=TU_PASSWORD  

spring.jpa.hibernate.ddl-auto=update  
spring.jpa.show-sql=true  

---

### ▶️ Ejecutar backend

cd Backend-Streaming  
./gradlew bootRun  

El backend se ejecutará en:  
http://localhost:8080  

---

## 🎨 Frontend (React + Vite)

### ▶️ Instalación

cd Frontend-Streaming  
npm install  

---

### ▶️ Ejecutar frontend

npm run dev  

El frontend se ejecutará en:  
http://localhost:5173  

---

## 🔐 Autenticación

El sistema incluye:

- Login con usuario y contraseña (JWT)  
- Login con Google (OAuth2)  
- Manejo de roles:
  - Usuario  
  - Administrador  

---

## 📌 Funcionalidades principales

- Visualización de contenido multimedia  
- Sistema de valoraciones y comentarios  
- Recomendaciones personalizadas  
- Gestión de suscripciones  
- Panel administrativo  

---

## ⚠️ Notas importantes

- Ejecutar primero el backend antes del frontend  
- Verificar que los puertos 8080 y 5173 estén libres  
- Configurar correctamente la base de datos  

---

## 💡 Diagrama Casos de Uso
<p align="center"><img src="casos de uso.png"></p>

---


## 👤 ROL: VISITANTE

| Código | Caso de Uso | Responsable | Estado | Avance | Evidencia | Descripción |
|--------|-------------|--------------|--------|--------|------------|-------------|
| CU-01 | Ver contenido público | Frans | Completado | 100% | http://localhost:8080/reproduccion-public-controller | Permite visualizar contenido gratuito sin autenticación |
| CU-02 | Buscar contenido público | Frans | En proceso | 80% | En desarrollo | Permite buscar contenido disponible sin login |
| CU-03 | Ver noticias | Bryan | Completado | 100% | http://localhost:8080/noticias | Visualización de noticias públicas |

---

## 👤 ROL: CLIENTE

| Código | Caso de Uso | Responsable | Estado | Avance | Evidencia | Descripción |
|--------|-------------|--------------|--------|--------|------------|-------------|
| CU-04 | Registro de usuario | Yhojan | Completado | 100% | http://localhost:8080/auth/register | Registro de nuevos usuarios |
| CU-05 | Inicio de sesión | Yhojan | Completado | 100% | http://localhost:8080/auth/login | Autenticación de usuarios |
| CU-07 | Ver noticias | Bryan | Completado | 100% | http://localhost:8080/noticias | Visualización de noticias publicadas |
| CU-08 | Buscar contenido | Frans | En proceso | 100% | http://localhost:8080/reproduccion-public-controller/reproducirPublico | Búsqueda de contenido según tipo de cuenta |
| CU-09 | Votar en sistema SP | Levi | En proceso | 90% | http://localhost:8080/peticion | Votación de películas para futuros contenidos |
| CU-10 | Ver lista SP | Levi | Completado | 100% | — | Visualización de lista de votación |
| CU-11 | Ver planes | Yhojan | Completado | 100% | http://localhost:8080/planes | Consulta de planes disponibles |
| CU-12 | Filtrar por categorías | Frans | Completado | 100% | http://localhost:8080/contenido-cliente-controller/listarPorCategoria | Filtrado de contenido por categoría |
| CU-13 | Ver recomendados | Frans | Completado | 100% | http://localhost:8080/contenido-cliente-controller/listarRecomendados | Recomendaciones personalizadas |
| CU-14 | Ver plan actual | Yhojan | Completado | 100% | http://localhost:8080/cliente/suscripciones/mi-suscripcion | Consulta de suscripción activa |
| CU-15 | Pago simulado | Yhojan | Completado | 100% | http://localhost:8080/payments/create | Procesamiento de pago |
| CU-18 | Reproducir contenido | Frans | Completado | 100% | — | Reproducción de contenido por ID |
| CU-22 | Ver catálogo | Frans | Completado | 100% | — | Visualización de catálogo completo |

---

## 👨‍💼 ROL: ADMINISTRADOR

| Código | Caso de Uso | Responsable | Estado | Avance | Evidencia | Descripción |
|--------|-------------|--------------|--------|--------|------------|-------------|
| CU-23 | Crear contenido | Frans | Completado | 100% | — | Registro de nuevo contenido |
| CU-24 | Actualizar contenido | Frans | Completado | 100% | — | Edición de contenido existente |
| CU-25 | Eliminar contenido | Frans | Completado | 100% | — | Eliminación de contenido por ID |
| CU-26 | Listar contenido | Frans | Completado | 100% | — | Visualización de todo el contenido |
| CU-27 | Ver usuarios | Yhojan | Completado | 100% | http://localhost:8080/admin/clientes | Gestión de usuarios |
| CU-28 | Crear planes | Yhojan | Completado | 100% | http://localhost:8080/admin/planes | Creación de planes |
| CU-29 | Eliminar planes | Yhojan | Completado | 100% | http://localhost:8080/admin/planes/{id} | Eliminación de planes |
| CU-30 | Actualizar planes | Yhojan | Completado | 100% | http://localhost:8080/admin/planes/{id} | Modificación de planes |
| CU-31 | Ver planes | Yhojan | Completado | 100% | http://localhost:8080/admin/planes | Listado de planes |
| CU-32 | Crear noticias | Bryan | Completado | 100% | http://localhost:8080/noticias | Registro de noticias |
| CU-33 | Eliminar noticias | Bryan | Completado | 100% | http://localhost:8080/noticias/{idPost} | Eliminación de noticias |
| CU-34 | Actualizar noticias | Bryan | Completado | 100% | http://localhost:8080/noticias/{idPost} | Edición de noticias |
| CU-35 | Noticias ordenadas | Bryan | Completado | 100% | http://localhost:8080/noticias/ordenadas | Ordenamiento de noticias |
| CU-36 | Fijar noticias | Bryan | Completado | 100% | http://localhost:8080/noticias/{idPost}/fijar | Destacar noticias |
| CU-37 | Ver pagos | Yhojan | Completado | 100% | — | Visualización de pagos realizados |
| CU-38 | Crear votación SP | Levi | Completado | 100% | — | Generación de votaciones |
| CU-39 | Actualizar SP | Levi | Completado | 100% | — | Modificación de listas SP |
| CU-40 | Desactivar SP | Levi | Completado | 100% | — | Desactivación de contenido |
| CU-41 | Ver ranking | Levi | Completado | 100% | — | Ranking de votaciones |
| CU-42 | Login admin | Yhojan | Completado | 100% | http://localhost:8080/auth/login | Acceso de administrador |
| CU-43 | Ver historial | Frans | Completado | 100% | http://localhost:8080/historial/obtenerhistorial | Historial del sistema |
| CU-44 | Noticias por autor | Bryan | Completado | 100% | http://localhost:8080/noticias/autor/{idAutor} | Filtrado por autor |
| CU-45 | Noticias por usuario | Bryan | Completado | 100% | http://localhost:8080/noticias/usuario/{idUsuario} | Filtrado por usuario |
| CU-46 | Reaccionar noticia | Bryan | Completado | 100% | http://localhost:8080/noticias/{idPost}/reaccionar | Reacciones a noticias |

---


## 👨‍💻 Autor

**Yhojan Yauli** 

**levi**

**frans**

**cristian**
****
**  ** 
Proyecto académico – Ingeniería de Software  
