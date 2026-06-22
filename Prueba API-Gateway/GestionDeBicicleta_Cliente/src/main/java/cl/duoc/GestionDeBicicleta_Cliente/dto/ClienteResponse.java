package cl.duoc.GestionDeBicicleta_Cliente.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ClienteResponse {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}

