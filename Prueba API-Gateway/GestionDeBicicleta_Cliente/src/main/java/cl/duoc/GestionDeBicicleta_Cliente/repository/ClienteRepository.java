package cl.duoc.GestionDeBicicleta_Cliente.repository;

import cl.duoc.GestionDeBicicleta_Cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByRut(String rut);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);
}