# Manual de Desarrollo - Appbito

Este documento contiene las instrucciones necesarias para levantar el proyecto desde cero en una máquina nueva, así como notas importantes de arquitectura.

## 1. Requisitos Previos
Para correr este proyecto necesitas tener instalado:
* Docker y Docker Compose
* Java 17 (Amazon Corretto)

## 2. Configuración de Entorno (.env)
**NUNCA subas contraseñas reales a Git.** El proyecto usa variables de entorno para funcionar.

1. Al clonar el repositorio, busca el archivo `.env.example`.
2. Duplícalo y renómbralo a `.env.dev` (para pruebas locales) o simplemente `.env` (para producción con Docker).
3. Llena las variables vacías (contraseñas de BD y correo).

## 3. Levantar en Desarrollo (Local)
Para programar y probar cambios en tu propia máquina sin compilar Docker:

**Backend (Spring Boot):**
* Asegúrate de tener tu `.env.dev` en la raíz del proyecto.
* En VS Code, simplemente dale "Play" al archivo `launch.json`. Este archivo inyectará automáticamente el `.env.dev` y el perfil `dev`.
* El backend correrá en: `http://localhost:8080`

## 4. Levantar en Producción (Docker)
Para desplegar la aplicación completa con bases de datos:

1. Asegúrate de tener el archivo `.env` con credenciales reales en la misma carpeta que el `docker-compose.yml`.
2. Ejecuta: `docker compose up -d`
* **Puertos de producción:**
  * Backend: `8001`
  * MySQL: `3307` (mapeado al 3306 interno)

## 5. Notas Importantes / Casos Raros
* **Base de datos:** Actualmente `ddl-auto` está en `update` para que Hibernate cree las tablas automáticamente sin borrar datos.
* **Correo:** Si el envío de correos falla, revisa que la Contraseña de Aplicación de Google en el `.env` no haya expirado o sido revocada.