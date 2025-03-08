# Buscador de Stickers

#### Busqueda de stickers animados usando el API de Giphy
Este proyecto es una aplicación Java con Spring Boot que implementa un sistema de internacionalización (i18n) en cual puedes buscar diferentes stickers de la aplicacion giphy.


## Requisitos

 - [Java 17 o superior](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
 - [Maven 3.x.](https://maven.apache.org/download.cgi)
 - [PostgreSQL](https://www.postgresql.org/)
 - [Spring Boot 3.x.](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0)


## Estructura del proyecto

```sh
  stickers/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/stickers/stickers/
│   │   │       ├── StickersApplication.java
│   │   │       ├── config/
│   │   │       │   ├── CookieLocaleResolver.java
│   │   │       │   ├── DataInitializer.java
│   │   │       │   ├── ThymeleafConfig.java
│   │   │       │   ├── WebClientConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── HomeController.java
│   │   │       │   ├── LoginController.java
│   │   │       │   └── UserController.java
│   │   │       ├── model/
│   │   │       │   ├── SearchHistory.java
│   │   │       │   └── User.java
│   │   │       ├── repository/
│   │   │       │   ├── SearchHistoryRepository.java
│   │   │       │   └── UserRepository.java
│   │   │       ├── security/
│   │   │       │   ├── ApiKeyAuthFilter.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       └── service/
│   │   │           ├── GiphyService.java
│   │   │           ├── SearchHistoryService.java
│   │   │           ├── SearchHistoryServiceImpl.java
│   │   │           └── UserService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── messages/
│   │       │   ├── messages_en.properties
│   │       │   └── messages_es.properties
│   │       └── templates/
│   │           ├── history.html
│   │           ├── index.html
│   │           └── login.html
│   └── test/
└── pom.xml
```

## Scripts necesarios para incializar base de datos del proyecto

#### Crear base de datos PostgreSQL:
```sh
CREATE DATABASE stickers;
```

#### Al ejecutar el proyecto las tablas se deben crear (En caso de que no se creen automaticamente ejecuta)

```sh
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS search_history (
    id SERIAL PRIMARY KEY,
    search_term VARCHAR(255) NOT NULL,
    search_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Configurar archivo application.properties:
```sh
# Configuración BD
spring.r2dbc.url=r2dbc:postgresql://localhost:5432/stickers
spring.r2dbc.username=postgres
spring.r2dbc.password=tu_contraseña
spring.r2dbc.initialization-mode=always

# API Giphy
giphy.api.url=https://api.giphy.com/v1/stickers/search
giphy.api.key=tu_api_key_de_giphy

# Token API Admin
admin.api.token=7ZpYg54Eu0hgs//KZ1fxRh2LgKlncUDzwifJ/tdfGZU=

# Thymeleaf
spring.thymeleaf.cache=false
```

Para obtener un api key ingresa [giphy](https://developers.giphy.com/)

#### Para crear un api token valido (si se desea usar uno nuevo)

Ejecutalo el siguiente comando con openssl o usando un generador en linea

```sh
openssl rand -base64 32
```

## Compila el proyecto
```sh
mvn clean package -DskipTests
```

## Ejecuta la aplicación
```sh
mvn spring-boot:run
```
para validar el funcionamiento ingresa aplicación
```sh
http://localhost:8080/
```


## Iniciar sesión:

La aplicación ya tiene dos usuarios predeterminados:
#### Usuario regular: 
username: user

password: password

#### Administrador: 
username: admin1

password: password


Si quieres crear un nuevo usuario o modificar la contraseña del actual, ve a la sesión de endpoints.

## Flujo de Ejecución de la Aplicación
- Inicio: La aplicación arranca con StickersApplication.
- Configuración: Se cargan beans y configuraciones.
- Inicialización: DataInitializer crea usuarios predeterminados.
- Seguridad: SecurityConfig establece filtros y políticas.
- Acceso:
 -- Usuario sin autenticar ve página de login.
Tras autenticación, accede a la página principal.

- Funcionalidades:

--Búsqueda de stickers usando API de Giphy.

-- Almacenamiento y consulta de historial.

--Cambio de idioma guardado en cookies.

--Administración de usuarios con protección API-KEY.


## Endpoints Disponibles

#### Envío de Login

```http
  POST /login
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Requerido**. Nombre de usuario. |
| `password` | `number` | **Requerido**. Contraseña. |

Ejemplo de solicitud:
```sh
curl -X POST "http://localhost:8080/login" \
  -d "username=admin1&password=password"
```
Respuesta exitosa:
```sh
Redirección a la página principal (/)
```

#### Cambio de Idioma

```http
  GET /change-language
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `lang` | `string` | **Requerido**. Idioma deseado (es para español, en para inglés). |

Ejemplo de solicitud:
```sh
curl -X GET "http://localhost:8080/change-language?lang=es"
```
Respuesta exitosa:
```sh
Redirección a la página desde donde se hizo la solicitud
```

### Endpoints Protegidos (requieren autenticación)

#### Buscar Stickers

```http
  GET /search
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `query` | `string` | **Requerido**. Término de búsqueda para stickers. |

Ejemplo de solicitud:
```sh
curl -X GET "http://localhost:8080/search?query=gatos" \
  -b "JSESSIONID=tu_session_id"
```
Respuesta exitosa:
```sh
Página HTML con los stickers encontrados según la búsqueda
```

#### Ver Historial de Búsquedas

```http
  GET /history
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `N/A` | `N/A` | Muestra el historial de búsquedas del usuario. |

Ejemplo de solicitud:
```sh
curl -X GET "http://localhost:8080/history" \
  -b "JSESSIONID=tu_session_id"
```
Respuesta exitosa:
```sh
Página HTML con listado del historial de búsquedas
```

### Endpoints Administrativos (requieren API KEY)

#### Listar Usuarios

```http
  GET /api/admin/users
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `N/A` | `N/A` | 	Lista todos los usuarios registrados. |

Headers:
```sh
X-API-KEY: Token de autenticación administrativo
```

Ejemplo de solicitud:
```sh
curl -X GET "http://localhost:8080/api/admin/users" \
  -H "X-API-KEY: 7ZpYg54Eu0hgs//KZ1fxRh2LgKlncUDzwifJ/tdfGZU="
```
Respuesta exitosa:
```sh
[
  {
    "id": 1,
    "username": "admin1",
    "password": "$2a$10$...[hash]...",
    "role": "ADMIN"
  },
  {
    "id": 2,
    "username": "user",
    "password": "$2a$10$...[hash]...",
    "role": "USER"
  }
]
```

#### Crear Usuario

```http
  POST /api/admin/users
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Requerido**. Nombre de usuario (debe ser único). |
| `password` | `string` | **Requerido**. Contraseña (sin encriptar). |
| `role` | `string` | **Requerido**. Rol del usuario (ADMIN o USER). |

Headers:
```sh
X-API-KEY: Token de autenticación administrativo
Content-Type: application/json
```
Ejemplo de solicitud:
```sh
curl -X POST "http://localhost:8080/api/admin/users" \
  -H "X-API-KEY: 7ZpYg54Eu0hgs//KZ1fxRh2LgKlncUDzwifJ/tdfGZU=" \
  -H "Content-Type: application/json" \
  -d '{"username":"nuevouser","password":"password123","role":"USER"}'
```

Body (JSON):
```http
{
  "username": "nuevouser",
  "password": "password123",
  "role": "USER"
}
```
Respuesta exitosa:
```sh
{
  "id": 3,
  "username": "nuevouser",
  "password": "$2a$10$...[hash]...",
  "role": "USER"
}
```

#### Cambiar Contraseña

```http
  POST /api/admin/users/change-password
```

| Campo | Tipo     | Descripción                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Requerido**. Nombre del usuario a modificar. |
| `newPassword` | `string` | **Requerido**. Nueva contraseña (sin encriptar). |

Headers:
```sh
X-API-KEY: Token de autenticación administrativo
Content-Type: application/json
```

Ejemplo de solicitud:
```sh
curl -X POST "http://localhost:8080/api/admin/users/change-password" \
  -H "X-API-KEY: 7ZpYg54Eu0hgs//KZ1fxRh2LgKlncUDzwifJ/tdfGZU=" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","newPassword":"nuevacontraseña123"}'
```

Body (JSON):
```sh
{
  "username": "admin1",
  "newPassword": "nuevacontraseña123"
}
```

Respuesta exitosa:
```sh
{
  "message": "Contraseña actualizada correctamente para admin1",
  "timestamp": "1712674356780"
}
```

Notas Adicionales:

- Para los endpoints protegidos, se necesita una sesión válida (obtenida tras hacer login).
- Para los endpoints administrativos, se requiere incluir el header X-API-KEY con el token configurado en application.properties.
- Todos los endpoints administrativos devuelven respuestas en formato JSON.
- Las contraseñas se almacenan encriptadas en la base de datos.
