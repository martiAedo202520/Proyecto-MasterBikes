package cl.duoc.GestionDeBicicleta_Arriendo.controller;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.ApiResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.service.TipoBicicletaService;
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
@RequestMapping("/api/v1/tipos-bicicletas")
@RequiredArgsConstructor
@Tag(name = "Tipos de Bicicletas", description = "Operaciones relacionadas con la clasificación y tipos de bicicletas disponibles")
public class TipoBicicletaController {

    private static final Logger log = LoggerFactory.getLogger(TipoBicicletaController.class);
    private final TipoBicicletaService tipoBicicletaService;

    @GetMapping
    @Operation(summary = "Obtener todos los tipos de bicicletas", description = "Retorna una lista completa de las categorías o tipos de bicicletas registradas")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipos de bicicletas encontrados correctamente")
    })
    public ResponseEntity<ApiResponse<List<TipoBicicletaResponse>>> listarTipos() {
        log.info("HTTP GET: Recibida solicitud para listar todos los tipos de bicicleta");
        List<TipoBicicletaResponse> lista = tipoBicicletaService.listarTipos();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Tipos de bicicleta encontrados correctamente",
                false,
                lista
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un tipo de bicicleta por su ID", description = "Busca una categoría específica de bicicleta utilizando su identificador único")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipo de bicicleta encontrado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de bicicleta no encontrado")
    })
    public ResponseEntity<ApiResponse<TipoBicicletaResponse>> obtenerTipoPorId(@PathVariable Long id) {
        log.info("HTTP GET: Recibida solicitud para buscar tipo de bicicleta con ID: {}", id);
        TipoBicicletaResponse response = tipoBicicletaService.obtenerTipoPorId(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Tipo de bicicleta encontrado correctamente",
                false,
                response
        ));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo tipo de bicicleta", description = "Permite registrar un nuevo tipo de bicicleta (ej: Urbana, Montaña) validando sus campos")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tipo de bicicleta creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ApiResponse<TipoBicicletaResponse>> crearTipo(
            @Valid @RequestBody TipoBicicletaRequest request
    ) {
        log.info("HTTP POST: Recibida solicitud para registrar tipo de bicicleta: {}", request.getNombre());
        TipoBicicletaResponse response = tipoBicicletaService.crearTipo(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Tipo de bicicleta creado correctamente",
                        false,
                        response
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo de bicicleta existente", description = "Modifica los atributos de una categoría de bicicleta almacenada a partir de su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tipo de bicicleta actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de bicicleta no encontrado")
    })
    public ResponseEntity<ApiResponse<TipoBicicletaResponse>> actualizarTipo(
            @PathVariable Long id,
            @Valid @RequestBody TipoBicicletaRequest request
    ) {
        log.info("HTTP PUT: Recibida solicitud para actualizar tipo de bicicleta ID: {}", id);
        TipoBicicletaResponse response = tipoBicicletaService.actualizarTipo(id, request);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Tipo de bicicleta actualizado correctamente",
                false,
                response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tipo de bicicleta", description = "Elimina permanentemente un registro de tipo de bicicleta basándose en su ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Tipo de bicicleta eliminado exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tipo de bicicleta no encontrado")
    })
    public ResponseEntity<Void> eliminarTipo(@PathVariable Long id) {
        log.info("HTTP DELETE: Recibida solicitud para eliminar tipo de bicicleta ID: {}", id);
        tipoBicicletaService.eliminarTipo(id);
        return ResponseEntity.noContent().build();
    }
}