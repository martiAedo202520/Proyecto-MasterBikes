package cl.duoc.GestionDeBicicleta_Cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse <T>{
    private int codigo;
    private String mensaje;
    private boolean error;
    private T data;
}