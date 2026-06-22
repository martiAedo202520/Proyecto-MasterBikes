package cl.duoc.GestionDeBicicleta_Arriendo.repository;

import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoBicicletaRepository extends JpaRepository<TipoBicicleta, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}