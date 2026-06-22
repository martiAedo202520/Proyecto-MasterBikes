package cl.duoc.GestionDeBicicleta_Arriendo.controller;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.ApiResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.service.ArriendoService;
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
@RequestMapping("/api/v1/arriendos")
@RequiredArgsConstructor
@Tag(name = "Arriendos", description = "Operaciones relacionadas con el módulo de arriendos")
public class ArriendoController {

    private static final Logger log = LoggerFactory.getLogger(ArriendoController.class);
    private final ArriendoService arriendoService;

    /*
     * GET /api/v1/arriendos
     */
    @GetMapping
    @Operation(summary = "Obtener todos los arriendos", description = "Retorna una lista completa de todos los arriendos registrados en ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arriendos encontrados correctamente")
    })
    public ResponseEntity<ApiResponse<List<ArriendoResponse>>> listarArriendos() {
        log.info("HTTP GET: Recibida solicitud para listar todos los arriendos");
        List<ArriendoResponse> lista = arriendoService.listarArriendos();
        log.info("HTTP GET: Listado enviado exitosamente. Total registros: {}", lista.size());

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Arriendos encontrados correctamente",
                false,
                lista
        ));
    }

    /*
     * GET /api/v1/arriendos/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un arriendo por su ID", description = "Busca en la base de datos un arriendo específico utilizando su identificador único numérico")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arriendo encontrado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Arriendo no encontrado")
    })
    public ResponseEntity<ApiResponse<ArriendoResponse>> obtenerArriendo(@PathVariable Long id) {
        log.info("HTTP GET: Recibida solicitud para buscar arriendo con ID: {}", id);
        ArriendoResponse response = arriendoService.obtenerArriendo(id);
        log.info("HTTP GET: Arriendo ID: {} encontrado y enviado", id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Arriendo encontrado correctamente",
                false,
                response
        ));
    }

    /*
     * POST /api/v1/arriendos
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo arriendo", description = "Permite registrar un nuevo arriendo en el sistema MasterBikes validando al cliente mediante OpenFeign")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Arriendo creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cliente asociado no encontrado en el sistema")
    })
    public ResponseEntity<ApiResponse<ArriendoResponse>> crearArriendo(
            @Valid @RequestBody ArriendoRequest request
    ) {
        log.info("HTTP POST: Recibida solicitud de creación de arriendo para Cliente ID: {}", request.getClienteId());
        ArriendoResponse response = arriendoService.crearArriendo(request);
        log.info("HTTP POST: Arriendo creado exitosamente con el ID asignado: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Arriendo creado correctamente",
                        false,
                        response
                ));
    }

    /*
     * PUT /api/v1/arriendos/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un arriendo existente", description = "Modifica los atributos de un arriendo almacenado a partir de su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Arriendo actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Arriendo o Cliente no encontrado")
    })
    public ResponseEntity<ApiResponse<ArriendoResponse>> actualizarArriendo(
            @PathVariable Long id,
            @Valid @RequestBody ArriendoRequest request
    ) {
        log.info("HTTP PUT: Recibida solicitud para actualizar arriendo con ID: {}", id);
        ArriendoResponse response = arriendoService.actualizarArriendo(id, request);
        log.info("HTTP PUT: Arriendo ID: {} actualizado correctamente", id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Arriendo actualizado correctamente",
                false,
                response
        ));
    }

    /*
     * DELETE /api/v1/arriendos/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un arriendo", description = "Elimina permanentemente un registro de arriendo de la base de datos basándose en su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Arriendo eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Arriendo no encontrado")
    })
    public ResponseEntity<Void> eliminarArriendo(@PathVariable Long id) {
        log.info("HTTP DELETE: Recibida solicitud para eliminar arriendo con ID: {}", id);
        arriendoService.eliminarArriendo(id);
        log.info("HTTP DELETE: Arriendo ID: {} eliminado correctamente", id);

        return ResponseEntity.noContent().build();
    }
}