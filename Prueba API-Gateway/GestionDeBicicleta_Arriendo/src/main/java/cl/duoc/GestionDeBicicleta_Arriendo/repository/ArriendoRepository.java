package cl.duoc.GestionDeBicicleta_Arriendo.repository;

import cl.duoc.GestionDeBicicleta_Arriendo.model.Arriendo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArriendoRepository extends JpaRepository<Arriendo, Long> {

    // Buscar arriendos por cliente
    List<Arriendo> findByClienteId(Long clienteId);

    // Buscar arriendos por estado
    List<Arriendo> findByEstado(String estado);

    // Buscar arriendos por tipo de bicicleta
    List<Arriendo> findByTipoBicicleta_Id(Long tipoId);

    // Buscar arriendos por nombre del tipo de bicicleta
    List<Arriendo> findByTipoBicicleta_Nombre(String nombre);

    // Verificar si existe un arriendo para un tipo de bicicleta en un estado específico
    boolean existsByTipoBicicleta_IdAndEstado(Long tipoBicicletaId, String estado);
}