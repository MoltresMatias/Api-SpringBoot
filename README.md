# API REST de Gestión de Usuarios con Spring Boot

API REST para gestionar usuarios y autenticación con **JWT** (JSON Web Tokens). Incluye registro, login, listar usuarios, obtener uno por ID y eliminarlo. Las contraseñas se hashean con **Argon2** y no se exponen en las respuestas.

---

## Documentación para aprender y replicar

Hay una carpeta **`docs/`** con guías pensadas para **entender** el proyecto y **replicarlo** más adelante:

| Documento                                                            | Para qué sirve                                                                                                                                         |
| -------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **[`docs/00-COMO-REPLICAR.md`](docs/00-COMO-REPLICAR.md)**           | Pasos para replicar el proyecto: requisitos, MySQL, configuración, variables de entorno, ejecutar y probar con `curl`.                                 |
| **[`docs/01-GUIA-PARA-ENTENDER.md`](docs/01-GUIA-PARA-ENTENDER.md)** | Conceptos (REST, JWT, Spring Security, JPA, DAO), flujos de la app y qué hace cada archivo. Incluye glosario y orden sugerido para estudiar el código. |
| **[`docs/02-CREAR-DESDE-CERO.md`](docs/02-CREAR-DESDE-CERO.md)**     | Cómo crear este mismo proyecto desde cero con Spring Initializr: dependencias, modelos, DAO, JWT, Security y controllers.                              |

**Recomendación:** si estás aprendiendo, **replica** (`00-COMO-REPLICAR`) o **créalo desde cero** (`02-CREAR-DESDE-CERO`); luego usa **`01-GUIA-PARA-ENTENDER`** para profundizar.

---

## Resumen rápido

### Requisitos

- **Java 21**, **Maven** (o `mvnw` del proyecto), **MySQL 8**.

### Configuración

- Crear la base de datos (p. ej. `pruebaJava`) y ajustar `src/main/resources/application.properties` (URL, usuario, contraseña de MySQL y, si quieres, JWT).
- La API corre por defecto en `http://localhost:8080`. El _context path_ es `/sistema/api/v1`, así que la base de las rutas es:

  ```
  http://localhost:8080/sistema/api/v1
  ```

### Comandos básicos

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run

# Empaquetar (genera .war)
./mvnw clean package
```

### Endpoints principales

| Método   | Ruta            | ¿Token?                     | Descripción               |
| -------- | --------------- | --------------------------- | ------------------------- |
| `POST`   | `/usuario`      | No                          | Registrar usuario         |
| `POST`   | `/login`        | No                          | Login; devuelve JWT       |
| `GET`    | `/usuario`      | Sí (ADMIN)                  | Listar todos los usuarios |
| `GET`    | `/usuario/{id}` | Sí (ADMIN o propio usuario) | Obtener un usuario        |
| `DELETE` | `/usuario/{id}` | Sí (ADMIN o propio usuario) | Eliminar un usuario       |

Las rutas completas incluyen el _context path_, por ejemplo:  
`POST http://localhost:8080/sistema/api/v1/usuario`,  
`POST http://localhost:8080/sistema/api/v1/login`, etc.

### Estructura del proyecto

```
src/main/java/com/matias/api_rest/
├── ApiRestApplication.java    # Arranque
├── config/                    # SecurityConfig, JwtAuthenticationEntryPoint
├── controllers/               # AuthController, UsuarioController
├── dao/                       # UsuarioDao, UsuarioDaoImp
├── filter/                    # JwtAuthenticationFilter
├── models/                    # Usuario, Rol
└── utils/                     # JWTUtil
```

Detalles de cada parte en **`docs/01-GUIA-PARA-ENTENDER.md`**.

---

## Mejoras futuras

En **`MEJORAS.md`** hay una lista priorizada de mejoras (validación, manejo de errores, CORS, tests, OpenAPI, etc.) para llevar el proyecto a un nivel más profesional.

---

## Licencia y uso

Proyecto de uso educativo. Ajusta licencia y autores según tu caso.
