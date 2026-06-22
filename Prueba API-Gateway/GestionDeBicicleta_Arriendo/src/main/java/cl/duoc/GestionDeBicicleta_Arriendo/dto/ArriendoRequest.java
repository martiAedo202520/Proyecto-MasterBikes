package cl.duoc.GestionDeBicicleta_Arriendo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Datos necesarios para registrar o actualizar un arriendo en el sistema")
public class ArriendoRequest {

    @NotNull(message = "El clienteId es obligatorio")
    @Schema(description = "ID del cliente que realiza el arriendo (Debe existir en el microservicio de Clientes)", example = "1")
    private Long clienteId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha y hora de inicio del arriendo", example = "2026-06-18T10:00:00")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha y hora de entrega o término del arriendo", example = "2026-06-18T18:00:00")
    private LocalDateTime fechaFin;

    @NotNull(message = "El costo es obligatorio")
    @Positive(message = "El costo debe ser mayor a 0")
    @Schema(description = "Monto total del cobro por el arriendo", example = "15000.0")
    private Double costo;

    @NotBlank(message = "El estado es obligatorio")
    @Size(min = 3, max = 20, message = "El estado debe tener entre 3 y 20 caracteres")
    @Schema(description = "Estado operativo inicial del arriendo", example = "ACTIVO")
    private String estado; // Ej: ACTIVO, FINALIZADO, CANCELADO

    @NotNull(message = "El tipo de bicicleta es obligatorio")
    @Schema(description = "ID de tipo de bicicleta que se va a arrendar", example = "2")
    private Long tipoBicicletaId;
}