# Microservicio de Gestión de Usuarios

## Descripción

Este proyecto es un microservicio para la gestión de usuarios, incluyendo funcionalidades de registro y login con 
autenticación JWT. Está construido con Java 11 y Spring Boot 2.5.14, utilizando Maven para la gestión de dependencias y 
pruebas unitarias con JUnit y Mockito.

## Requisitos

- Java 11
- Maven 3.6
- Una base de datos H2 (para desarrollo y pruebas)
- Postman (para pruebas)

## Construcción del Proyecto

1. **Clonar el repositorio:**

    ```bash
    git clone https://github.com/odinallfoor/MicroServiceLT.git
    ```

2. **Navegar al directorio del proyecto:**

    ```bash
    cd repositorio
    ```

3. **Compilar el proyecto:**

    ```bash
    mvn clean install
    ```

   Esto compilará el código y ejecutará las pruebas unitarias.

## Ejecución del Proyecto

1. **Ejecutar la aplicación:**

    ```bash
    mvn spring-boot:run
    ```

   La aplicación se iniciará en el puerto `8080` por defecto.

2. **Acceder a la API:**

   > **Nota Importante:** Modificar segun corresponda los datos de los ejemplos cURL que veras a continuacion.
   
   Los endpoints disponibles son:
    - **POST /sign-up**: Para registrar un nuevo usuario.
     
    ```bash 
    http://localhost:8080/api/v1/user/sign-up
    ```
   
    - **cURL Ejemplo (Postman)**.
   
    ```bash
    curl --location 'http://localhost:8080/api/v1/user/sign-up' \
    --header 'Content-Type: application/json' \
    --header 'Cookie: JSESSIONID=10471C493B5AC0B7F55EAD1AC2633D61' \
    --data-raw '{
      "name": "John Doe",
      "email": "john.doe@example.co",
      "password": "a2asfGfdf4",
      "phones": [
        {
          "number": 1234567890,
          "citycode": 1,
          "contrycode": "1"
        }
      ]
    }'
    ```
    - **GET /login**: Para autenticar un usuario existente.
    
    ```bash 
    http://localhost:8080/api/v1/user/login
    ```

    - **cURL Ejemplo (Postman)**.
    
    ```bash
    curl --location --request GET 'http://localhost:8080/api/v1/user/login' \
    --header 'Authorization: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvIiwiaWF0IjoxNzI1NDE2NjkxLCJleHAiOjE3MjU1MDMwOTF9.dzq-v_DTTD2tj4SYNiBUA_An1-oweJOi-ZpdhWASaPjOE2VXixZvF8x3SuBJzGVh6q6PRxTbblemgtI_u3uNkA' \
    --header 'Content-Type: application/json' \
    --header 'Cookie: JSESSIONID=10471C493B5AC0B7F55EAD1AC2633D61' \
    --data-raw '{    
      "email": "john.doe@example.co",
      "password": "a2asfGfdf4"    
    }'
    ```

3. **Swagger UI:**

   Puedes ver y probar la API utilizando Swagger UI en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

4. **Base de Datos(H2):**

    Puedes ver los datos guardados ingresando a la url [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/).
    ```bash
    User : root
    Pass: password
    ```
    

## Diagramas UML

### Diagrama de Componentes

![Diagrama de Componentes](src/main/resources/uml/UML_componentes.png)

- **UserApp**: Entidad principal que representa a un usuario.
- **Phones**: Entidad relacionada que representa los teléfonos de un usuario.
- **UsersRepository**: Repositorio para la persistencia de usuarios.
- **JwtProvider**: Proveedor de tokens JWT.
- **ValidationUtils**: Utilidad para validaciones de datos.

### Diagrama de Secuencia

![Diagrama de Secuencia](src/main/resources/uml/UML_secuencia_create_v2.png)

- **Registro de Usuario:**
    1. El cliente envía una solicitud de registro al endpoint `/sign-up`.
    2. El servicio `CreateUserService` valida la solicitud.
    3. Se crea un nuevo usuario y se guarda en la base de datos.
    4. Se genera un token JWT y se retorna la respuesta.


![Diagrama de Secuencia](src/main/resources/uml/UML_secuencia_login_v2.png)
- **Inicio de Sesión:**
    1. El cliente envía una solicitud de inicio de sesión al endpoint `/login`.
    2. El servicio `LoginUserService` valida el token y las credenciales.
    3. Se actualiza el token y el último inicio de sesión en la base de datos.
    4. Se retorna la respuesta con el nuevo token.

