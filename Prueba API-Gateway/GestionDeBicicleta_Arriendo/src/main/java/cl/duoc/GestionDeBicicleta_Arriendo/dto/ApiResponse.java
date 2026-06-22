package cl.duoc.GestionDeBicicleta_Arriendo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int codigo;
    private String mensaje;
    private boolean error;
    private T data;
}
