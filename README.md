# Proyecto MasterBikes

## 1. Descripción del Proyecto
Este es un proyecto de arriendo de bicicletas diseñado bajo una arquitectura de microservicios, el cual está dividido en dos servicios principales: **Arriendo** y **Cliente**. El microservicio de Clientes gestiona la información de los usuarios, mientras que el de Arriendos controla el flujo de arriendo, los tiempos y las tarifas de las bicicletas.

## 2. Nombre de Integrantes
* Tamara Gutiérrez
* Natalia Yáñez
* Martina Aedo

## 3. Aporte Realizado por cada Integrante

### Martina Aedo (Configuración de Docker, GitHub y Mejoras)
* `Dockerfile` (microservicio-arriendo)
* `Dockerfile` (microservicio-cliente)
* `Dockerfile` (API-Gateway)
* `docker-compose.yml` (Carpeta principal)
* Creación de repositorio en GitHub y organización de ramas de trabajo.
* Implementación de mejoras basadas en la retroalimentación de la Evaluación 2 *(Reglas de negocio, validaciones y Arquitectura por capas: Controller, Service, Repository/Model)*.

### Tamara Gutiérrez (Documentación de APIs)
* Implementación de la documentación interactiva de las APIs mediante **Swagger, OpenAPI y Springdoc**.
* Desarrollo y exposición de controladores: `ClienteController`, `ArriendoController` y `TipoBicicletaController`.
* Estructuración de contratos de entrada: `DTO - ClienteRequest`, `DTO - ArriendoRequest` y `DTO - TipoBicicletaRequest`.

### Natalia Yáñez (Testing y API Gateway)
* Desarrollo de pruebas unitarias estrictas en métodos de negocio mediante **Mockito**.
* Clases de prueba implementadas: `ArriendoServiceTest`, `ClienteServiceTest` y `TipoBicicletaServiceTest`.
* Creación y despliegue del **API Gateway** bajo el puerto `8084`.
* Configuración del archivo de propiedades centralizado `application.yml` del Gateway.

---

## 4. APIs y Endpoints Disponibles

### 🟢 API de Clientes (`ms-clientes`)
* `POST /api/v1/clientes` - Crear un nuevo cliente
* `GET /api/v1/clientes` - Obtener todos los clientes
* `GET /api/v1/clientes/{id}` - Obtener un cliente por su ID
* `PUT /api/v1/clientes/{id}` - Actualizar una cliente existente
* `DELETE /api/v1/clientes/{id}` - Eliminar un cliente

### 🟢 API de Arriendos (`ms-arriendos`)
* `POST /api/v1/arriendos` - Crear un nuevo arriendo
* `GET /api/v1/arriendos` - Obtener todos los arriendos
* `GET /api/v1/arriendos/{id}` - Obtener un arriendo por su ID
* `PUT /api/v1/arriendos/{id}` - Actualizar un arriendo existente
* `DELETE /api/v1/arriendos/{id}` - Eliminar un arriendo

### 🟢 API de Tipos de Bicicleta (`ms-arriendos`)
* `POST /api/v1/tipos-bicicletas` - Crear un nuevo tipo de bicicleta
* `GET /api/v1/tipos-bicicletas` - Obtener todos los tipos de bicicletas
* `GET /api/v1/tipos-bicicletas/{id}` - Obtener un tipo de bicicleta por su ID
* `PUT /api/v1/tipos-bicicletas/{id}` - Actualizar un tipo de bicicleta existente
* `DELETE /api/v1/tipos-bicicletas/{id}` - Eliminar un tipo de bicicleta

## 5. Puertos y Rutas del API Gateway
El API Gateway centraliza las peticiones entrantes bajo el puerto **8084**:

* **Clientes:** `http://localhost:8084/api/v1/clientes`
* **Arriendos:** `http://localhost:8084/api/v1/arriendos`
* **Tipos de Bicicletas:** `http://localhost:8084/api/v1/tipos-bicicletas`

## 6. Enlaces de Swagger (Documentación Local)
Para revisar y probar los contratos de manera aislada por microservicio:

* **Microservicio Clientes:** [http://localhost:8081/doc/swagger-ui/index.html](http://localhost:8081/doc/swagger-ui/index.html)
* **Microservicio Arriendos:** [http://localhost:8082/doc/swagger-ui/index.html](http://localhost:8082/doc/swagger-ui/index.html)

---

## 7. Instrucciones para Ejecutar y Probar el Sistema

1. **Levantar la Infraestructura:** Abra una terminal en la raíz del proyecto y ejecute el siguiente comando para compilar y levantar los contenedores:
   ```bash
   docker-compose up --build

  2.Verificar y Probar el Sistema: Una vez que visualice en la terminal o en la interfaz de Docker Desktop que todos los contenedores (ms-clientes, ms-arriendos y api-gateway) se encuentran encendidos y corriendo con éxito, proceda a realizar las pruebas de conectividad y lógica de negocio consumiendo las URLs centralizadas a través del API Gateway (puerto 8084) o mediante los paneles interactivos de Swagger.
