# Mejoras para llevar el proyecto a nivel profesional

Lista priorizada de lo que falta o conviene mejorar. Algunos ítems ya están resueltos (p. ej. password en responses).

---

## Crítico (seguridad y bugs)

| #   | Mejora                                      | Estado    | Descripción                                                                                                                                            |
| --- | ------------------------------------------- | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | **No exponer password en respuestas**       | ✅ Hecho  | `Usuario` se devolvía con el hash en `POST /usuario` y en `GET`. Se usó `@JsonProperty(WRITE_ONLY)` en el campo `password`.                            |
| 2   | **Validación de entrada (Bean Validation)** | Pendiente | Validar `@NotNull`, `@Email`, `@Size`, etc. en registro y login. Sin esto, datos inválidos pueden llegar a BD o generar 500.                           |
| 3   | **Secrets fuera del código**                | Pendiente | JWT secret y contraseña de BD en `application.properties`. Usar variables de entorno (`${DB_PASSWORD}`, etc.) y `.env` o secret manager en producción. |
| 4   | **Email duplicado en registro**             | Pendiente | Permitir dos usuarios con el mismo email. Al registrar, comprobar si existe y devolver `409 Conflict` con mensaje claro.                               |

---

## Importante (robustez y consistencia)

| #   | Mejora                               | Descripción                                                                                                                                                               |
| --- | ------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 5   | **Manejo global de excepciones**     | `@ControllerAdvice` + `@ExceptionHandler` para devolver JSON unificado (código, mensaje, detalle opcional) en 400/404/409/500 en vez de respuestas por defecto de Spring. |
| 6   | **CORS**                             | No hay configuración CORS. Si un frontend en otro origen llama a la API, fallará. Definir `WebMvcConfigurer` o `CorsConfigurationSource` con orígenes permitidos.         |
| 7   | **Argon2 como bean**                 | Se instancia `Argon2` en cada request (controller y DAO). Crear un `@Bean` de Argon2 y reutilizarlo.                                                                      |
| 8   | **Autorización vía Spring Security** | Hoy el controller valida token y roles a mano. Usar `@PreAuthorize("hasRole('ADMIN')")` y `SecurityContextHolder`; quitar validación manual de token en controller.       |
| 9   | **Transacciones de solo lectura**    | En `getUsuarios` y `getUsuario`, usar `@Transactional(readOnly = true)` para lecturas.                                                                                    |

---

## Nivel profesional (buenas prácticas)

| #   | Mejora                                   | Descripción                                                                                                                                                                          |
| --- | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 10  | **Tests**                                | Solo `contextLoads`. Añadir tests unitarios (controller, DAO, JWTUtil) e integración (`@WebMvcTest`, `MockMvc`) para los endpoints principales.                                      |
| 11  | **OpenAPI / Swagger**                    | Documentar la API con SpringDoc o Swagger. Incluir ejemplos de request/response, códigos de error y requisitos de autenticación.                                                     |
| 12  | **Paginación en `GET /usuario`**         | Devolver todos los usuarios no escala. Usar `Pageable` y `Page<Usuario>` con `page`, `size` y `sort`.                                                                                |
| 13  | **Actualización de usuario (PUT/PATCH)** | El README habla de CRUD pero no hay update. Añadir `PUT /usuario/{id}` o `PATCH`, con validación y permisos (admin o propio usuario).                                                |
| 14  | **DTOs para request/response**           | DTOs específicos (`RegistroRequest`, `UsuarioResponse`, `LoginRequest`) en lugar de exponer la entidad `Usuario` en todos los endpoints. Mejor control y evolución del contrato API. |
| 15  | **Logging y auditoría**                  | Log de operaciones sensibles (login, registro, borrado) y de errores. Opcional: auditoría de quién modificó qué.                                                                     |
| 16  | **Rate limiting**                        | Limitar peticiones a `/login` y `POST /usuario` para reducir fuerza bruta y abuso. Por ejemplo con Bucket4j o filtro custom.                                                         |
| 17  | **README y despliegue**                  | README con requisitos, cómo ejecutar (Maven, variables de entorno), ejemplos `curl` de cada endpoint y, si aplica, pasos de despliegue (Docker, etc.).                               |

---

## Resumen de prioridades

1. **Ya hecho:** No exponer password en JSON.
2. **Siguiente paso recomendado:** Validación (Bean Validation) + manejo global de excepciones + evitar email duplicado en registro.
3. **Después:** CORS, Argon2 bean, refactor de autorización con `@PreAuthorize`, tests y OpenAPI.
4. **Para producción:** Secrets por env, rate limiting, paginación, DTOs, logging y README completo.

Si indicas por cuáles quieres empezar, se pueden implementar en ese orden.
