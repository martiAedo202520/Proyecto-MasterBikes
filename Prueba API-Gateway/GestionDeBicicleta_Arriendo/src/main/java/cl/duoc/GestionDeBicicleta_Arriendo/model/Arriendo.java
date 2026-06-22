package cl.duoc.GestionDeBicicleta_Arriendo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "arriendos")
public class Arriendo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Cliente (validado vía Feign)
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private Double costo;

    @Column(nullable = false, length = 20)
    private String estado; // Ej: "ACTIVO", "FINALIZADO", "CANCELADO"

    @ManyToOne
    @JoinColumn(name = "tipo_bicicleta_id", nullable = false)
    @JsonBackReference
    private TipoBicicleta tipoBicicleta;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}