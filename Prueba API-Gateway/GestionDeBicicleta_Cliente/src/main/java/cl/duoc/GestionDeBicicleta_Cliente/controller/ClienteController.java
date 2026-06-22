package cl.duoc.GestionDeBicicleta_Cliente.controller;

import cl.duoc.GestionDeBicicleta_Cliente.dto.ApiResponse;
import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteRequest;
import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteResponse;
import cl.duoc.GestionDeBicicleta_Cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operaciones relacionadas con el módulo de clientes de MasterBikes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    /*
     * GET /api/v1/clientes
     */
    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Retorna una lista completa de todos los clientes registrados en MasterBikes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Clientes encontrados correctamente")
    })
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listarClientes() {
        logger.info("HTTP GET: Recibida solicitud para listar todos los clientes");
        List<ClienteResponse> lista = clienteService.obtenerTodos();
        logger.info("HTTP GET: Listado enviado exitosamente. Total registros: {}", lista.size());

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Clientes encontrados correctamente",
                false,
                lista
        ));
    }

    /*
     * GET /api/v1/clientes/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por su ID", description = "Busca en la base de datos un cliente específico utilizando su identificador único numérico")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente encontrado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado") // 🛠️ Pauta pág. 9
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorId(@PathVariable Long id) {
        logger.info("HTTP GET: Recibida solicitud para buscar cliente con ID: {}", id);
        ClienteResponse data = clienteService.buscarPorId(id);
        logger.info("HTTP GET: Cliente ID: {} encontrado y enviado", id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cliente encontrado correctamente",
                false,
                data
        ));
    }

    /*
     * POST /api/v1/clientes
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo cliente", description = "Permite registrar un nuevo cliente en el sistema MasterBikes validando sus campos obligatorios")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente registrado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> registrarCliente(
            @Valid @RequestBody ClienteRequest request
    ) {
        logger.info("HTTP POST: Recibida solicitud de registro para cliente con RUT: {}", request.getRut());
        ClienteResponse data = clienteService.guardar(request);
        logger.info("HTTP POST: Cliente creado exitosamente con ID asignado: {}", data.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Cliente registrado correctamente",
                        false,
                        data
                ));
    }

    /*
     * PUT /api/v1/clientes/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cliente existente", description = "Modifica los atributos de un cliente almacenado a partir de su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {
        logger.info("HTTP PUT: Recibida solicitud para actualizar cliente con ID: {}", id);
        ClienteResponse data = clienteService.actualizar(id, request);
        logger.info("HTTP PUT: Cliente ID: {} actualizado correctamente", id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cliente actualizado correctamente",
                false,
                data
        ));
    }

    /*
     * DELETE /api/v1/clientes/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente", description = "Elimina permanentemente un registro de cliente de la base de datos basándose en su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        logger.info("HTTP DELETE: Recibida solicitud para eliminar cliente con ID: {}", id);
        clienteService.eliminar(id);
        logger.info("HTTP DELETE: Cliente ID: {} eliminado correctamente", id);

        return ResponseEntity.noContent().build();
    }
}