package cl.duoc.GestionDeBicicleta_Cliente.service;

import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteRequest;
import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteResponse;
import cl.duoc.GestionDeBicicleta_Cliente.model.Cliente;
import cl.duoc.GestionDeBicicleta_Cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera el constructor para inyectar dependencias de forma limpia
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;

    /*
     * 1. Obtener todos los clientes
     */
    public List<ClienteResponse> obtenerTodos() {
        logger.info("Solicitud interna: Procesando listado completo de clientes");
        List<ClienteResponse> clientes = clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        logger.info("Listado completado. Se encontraron {} clientes", clientes.size());
        return clientes;
    }

    /*
     * 2. Buscar cliente por ID
     */
    public ClienteResponse buscarPorId(Long id) {
        logger.info("Solicitud interna: Buscando cliente con ID: {}", id);

        // Al lanzar RuntimeException, tu GlobalExceptionHandler responderá con 400 Bad Request
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Error en búsqueda: Cliente no encontrado con ID: {}", id);
                    return new RuntimeException("Cliente no encontrado con ID: " + id);
                });

        logger.info("Cliente obtenido con éxito para el ID {}", id);
        return toResponse(cliente);
    }

    /*
     * 3. Crear nuevo cliente
     */
    public ClienteResponse guardar(ClienteRequest dto) {
        logger.info("Solicitud interna: Iniciando proceso de registro para cliente con RUT: {}", dto.getRut());

        if (clienteRepository.findByRut(dto.getRut()).isPresent()) {
            logger.error("Fallo de negocio: El RUT {} ya existe en el sistema", dto.getRut());
            throw new RuntimeException("El RUT " + dto.getRut() + " ya se encuentra registrado.");
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            logger.error("Fallo de negocio: El email {} ya está en uso", dto.getEmail());
            throw new RuntimeException("El email " + dto.getEmail() + " ya se encuentra registrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        Cliente guardado = procesar(cliente, dto);

        logger.info("Cliente registrado exitosamente en la base de datos con el ID {}", guardado.getId());
        return toResponse(guardado);
    }

    /*
     * 4. Actualizar cliente
     */
    public ClienteResponse actualizar(Long id, ClienteRequest dto) {
        logger.info("Solicitud interna: Preparando actualización para el cliente con ID {}", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Error en actualización: Cliente no encontrado con ID: {}", id);
                    return new RuntimeException("Cliente no encontrado con ID: " + id);
                });

        if (!clienteExistente.getRut().equals(dto.getRut()) && clienteRepository.findByRut(dto.getRut()).isPresent()) {
            logger.error("Fallo de negocio en actualización: El RUT {} ya pertenece a otro cliente", dto.getRut());
            throw new RuntimeException("El RUT " + dto.getRut() + " ya existe en el sistema.");
        }

        if (!clienteExistente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            logger.error("Fallo de negocio en actualización: El email {} ya pertenece a otro cliente", dto.getEmail());
            throw new RuntimeException("El email " + dto.getEmail() + " ya está registrado.");
        }

        clienteExistente.setActivo(dto.getActivo());
        Cliente actualizado = procesar(clienteExistente, dto);
        logger.info("Cliente ID {} actualizado correctamente con nuevos datos", actualizado.getId());

        return toResponse(actualizado);
    }

    /*
     * 5. Eliminar cliente
     */
    public void eliminar(Long id) {
        logger.info("Solicitud interna: Intentando eliminar cliente con ID {}", id);

        if (!clienteRepository.existsById(id)) {
            logger.error("Error en eliminación: No existe un registro con el ID {}", id);
            throw new RuntimeException("No se puede eliminar: el cliente no existe con ID: " + id);
        }

        clienteRepository.deleteById(id);
        logger.info("Cliente con ID {} eliminado exitosamente del sistema", id);
    }

    /*
     * Métodos Mapeadores Privados (Encapsulan la conversión)
     */
    private Cliente procesar(Cliente cliente, ClienteRequest dto) {
        cliente.setRut(dto.getRut());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        return clienteRepository.save(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setRut(cliente.getRut());
        response.setNombre(cliente.getNombre());
        response.setApellido(cliente.getApellido());
        response.setEmail(cliente.getEmail());
        response.setTelefono(cliente.getTelefono());
        response.setDireccion(cliente.getDireccion());
        response.setActivo(cliente.getActivo());
        response.setFechaCreacion(cliente.getFechaCreacion());
        response.setFechaModificacion(cliente.getFechaModificacion());
        return response;
    }
}