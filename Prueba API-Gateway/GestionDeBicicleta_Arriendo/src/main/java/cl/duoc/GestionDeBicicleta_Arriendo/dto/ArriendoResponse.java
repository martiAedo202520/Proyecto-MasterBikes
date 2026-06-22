package cl.duoc.GestionDeBicicleta_Arriendo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArriendoResponse {
    private Long id;
    private Long clienteId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Double costo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String tipoBicicletaNombre;
}