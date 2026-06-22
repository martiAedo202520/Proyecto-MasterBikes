package cl.duoc.GestionDeBicicleta_Cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para la creación o actualización de un cliente en el sistema")
public class ClienteRequest {

    @NotBlank(message = "El RUT es obligatorio.")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9kK]{1}$", message = "El formato del RUT debe ser sin puntos y con guión (ej: 12345678-9).")
    @Schema(description = "RUT del cliente sin puntos y con guion", example = "19876543-2")
    private String rut;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    @Schema(description = "Primer nombre del cliente", example = "Renata")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
    @Schema(description = "Apellidos paterno", example = "Muñoz")
    private String apellido;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "Debe ingresar un formato de correo electrónico válido.")
    @Schema(description = "Dirección de correo electrónico única", example = "Renata.muñoz@mail.cl")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Schema(description = "Número telefónico móvil o fijo de contacto", example = "+56912345678")
    private String telefono;

    @Schema(description = "Dirección de residencia particular (Opcional)", example = "Av. Concha y Toro 1340, Puente Alto")
    private String direccion; // Opcional, no requiere anotación obligatoria

    @NotNull(message = "El estado activo/inactivo es obligatorio.")
    @Schema(description = "Define si el cliente está habilitado operativamente en la plataforma", example = "true")
    private Boolean activo;
}