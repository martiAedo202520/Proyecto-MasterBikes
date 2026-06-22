package cl.duoc.GestionDeBicicleta_Arriendo.dto;

import lombok.Data;

public class ClienteResponse {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;
}

