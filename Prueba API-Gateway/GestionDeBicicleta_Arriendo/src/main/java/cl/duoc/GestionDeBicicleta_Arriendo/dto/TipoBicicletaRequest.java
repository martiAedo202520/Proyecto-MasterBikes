package cl.duoc.GestionDeBicicleta_Arriendo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar o actualizar una categoría o tipo de bicicleta")
public class TipoBicicletaRequest {

    @NotBlank(message = "El nombre del tipo de bicicleta es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
    @Schema(description = "Nombre descriptivo del tipo de bicicleta", example = "Montaña")
    private String nombre;
}