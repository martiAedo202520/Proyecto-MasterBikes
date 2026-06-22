package cl.duoc.GestionDeBicicleta_Arriendo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
@Data
@Entity
@Table(name = "tipos_bicicletas") // Nombre de la tabla
public class TipoBicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tipo es obligatorio")
    private String nombre; // Montaña, Paseo, Eléctrica, etc.

    @OneToMany(mappedBy = "tipoBicicleta", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Arriendo> arriendos;
}

