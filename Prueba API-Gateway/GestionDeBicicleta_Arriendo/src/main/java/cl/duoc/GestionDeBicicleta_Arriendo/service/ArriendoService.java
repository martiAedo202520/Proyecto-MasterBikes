package cl.duoc.GestionDeBicicleta_Arriendo.service;

import cl.duoc.GestionDeBicicleta_Arriendo.client.ClienteFeignClient;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.model.Arriendo;
import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.ArriendoRepository;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.TipoBicicletaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArriendoService {

    private static final Logger log = LoggerFactory.getLogger(ArriendoService.class);

    private final ArriendoRepository arriendoRepository;
    private final ClienteFeignClient clienteFeignClient;
    private final TipoBicicletaRepository tipoBicicletaRepository;

    /*
     * Lista todos los arriendos.
     */
    public List<ArriendoResponse> listarArriendos() {
        log.info("Solicitud interna: Procesando listado completo de arriendos");
        List<Arriendo> arriendos = arriendoRepository.findAll();
        List<ArriendoResponse> respuesta = new ArrayList<>();

        for (Arriendo arriendo : arriendos) {
            respuesta.add(convertirAResponse(arriendo));
        }
        log.info("Listado completado. Cantidad de arriendos encontrados: {}", respuesta.size());
        return respuesta;
    }

    /*
     * Busca un arriendo por ID.
     */
    public ArriendoResponse obtenerArriendo(Long id) {
        log.info("Solicitud interna: Buscando arriendo con ID {}", id);
        Arriendo arriendo = arriendoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en búsqueda: Arriendo no encontrado con ID: {}", id);
                    return new RuntimeException("Arriendo no encontrado con ID: " + id);
                });
        log.info("Arriendo obtenido con éxito para el ID {}", id);
        return convertirAResponse(arriendo);
    }

    /*
     * Registra un nuevo arriendo aplicando reglas de negocio.
     */
    public ArriendoResponse crearArriendo(ArriendoRequest request) {
        log.info("Solicitud interna: Iniciando proceso de registro de arriendo para Cliente ID {}", request.getClienteId());

        // Regla 1: Validar cliente vía Feign
        var cliente = clienteFeignClient.obtenerClientePorId(request.getClienteId());
        if (cliente == null || cliente.isError()) {
            log.error("Fallo de negocio: El cliente con ID {} no existe en el sistema remoto", request.getClienteId());
            throw new RuntimeException("Cliente no existe con ID: " + request.getClienteId());
        }

        //Validar existencia del tipo de bicicleta
        TipoBicicleta tipo = tipoBicicletaRepository.findById(request.getTipoBicicletaId())
                .orElseThrow(() -> {
                    log.error("Fallo de negocio: Tipo de bicicleta ID {} no encontrado en catálogo", request.getTipoBicicletaId());
                    return new RuntimeException("Tipo de bicicleta no encontrado con ID: " + request.getTipoBicicletaId());
                });

        //Validar que fecha fin sea posterior a fecha inicio
        if (request.getFechaFin().isBefore(request.getFechaInicio()) || request.getFechaFin().isEqual(request.getFechaInicio())) {
            log.error("Fallo de negocio: Conflicto de consistencia en fechas. Inicio: {}, Fin: {}", request.getFechaInicio(), request.getFechaFin());
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        //Restringir estados válidos
        String estadoUpper = request.getEstado().toUpperCase();
        if (!estadoUpper.equals("ACTIVO") && !estadoUpper.equals("FINALIZADO") && !estadoUpper.equals("CANCELADO")) {
            log.error("Fallo de negocio: Se intentó registrar un estado no permitido: {}", request.getEstado());
            throw new RuntimeException("Estado no válido. Los permitidos son: ACTIVO, FINALIZADO, CANCELADO.");
        }

        //Validar disponibilidad real (evitar duplicados si ya está ACTIVO)
        if (estadoUpper.equals("ACTIVO") && arriendoRepository.existsByTipoBicicleta_IdAndEstado(request.getTipoBicicletaId(), "ACTIVO")) {
            log.error("Fallo de negocio: El tipo de bicicleta ID {} ya cuenta con un arriendo en estado ACTIVO", request.getTipoBicicletaId());
            throw new RuntimeException("Esta bicicleta no está disponible, ya se encuentra en un arriendo ACTIVO.");
        }

        Arriendo arriendo = new Arriendo();
        arriendo.setClienteId(request.getClienteId());
        arriendo.setFechaInicio(request.getFechaInicio());
        arriendo.setFechaFin(request.getFechaFin());
        arriendo.setCosto(request.getCosto());
        arriendo.setEstado(estadoUpper);
        arriendo.setTipoBicicleta(tipo);

        Arriendo guardado = arriendoRepository.save(arriendo);
        log.info("Arriendo registrado exitosamente en la base de datos con el ID generado {}", guardado.getId());
        return convertirAResponse(guardado);
    }

    /*
     * Actualiza un arriendo existente.
     */
    public ArriendoResponse actualizarArriendo(Long id, ArriendoRequest request) {
        log.info("Solicitud interna: Preparando actualización para el arriendo con ID {}", id);

        Arriendo arriendoExistente = arriendoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en actualización: Arriendo no encontrado con ID: {}", id);
                    return new RuntimeException("Arriendo no encontrado con ID: " + id);
                });

        var cliente = clienteFeignClient.obtenerClientePorId(request.getClienteId());
        if (cliente == null || cliente.isError()) {
            log.error("Fallo de negocio en actualización: El cliente con ID {} no existe en el sistema remoto", request.getClienteId());
            throw new RuntimeException("Cliente no existe con ID: " + request.getClienteId());
        }

        TipoBicicleta tipo = tipoBicicletaRepository.findById(request.getTipoBicicletaId())
                .orElseThrow(() -> {
                    log.error("Fallo de negocio en actualización: Tipo de bicicleta ID {} no encontrado", request.getTipoBicicletaId());
                    return new RuntimeException("Tipo de bicicleta no encontrado con ID: " + request.getTipoBicicletaId());
                });

        if (request.getFechaFin().isBefore(request.getFechaInicio()) || request.getFechaFin().isEqual(request.getFechaInicio())) {
            log.error("Fallo de negocio en actualización: Inconsistencia en rango de fechas");
            throw new RuntimeException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        String estadoUpper = request.getEstado().toUpperCase();
        if (!estadoUpper.equals("ACTIVO") && !estadoUpper.equals("FINALIZADO") && !estadoUpper.equals("CANCELADO")) {
            log.error("Fallo de negocio en actualización: Estado inválido {}", request.getEstado());
            throw new RuntimeException("Estado no válido.");
        }

        arriendoExistente.setClienteId(request.getClienteId());
        arriendoExistente.setFechaInicio(request.getFechaInicio());
        arriendoExistente.setFechaFin(request.getFechaFin());
        arriendoExistente.setCosto(request.getCosto());
        arriendoExistente.setEstado(estadoUpper);
        arriendoExistente.setTipoBicicleta(tipo);

        Arriendo actualizado = arriendoRepository.save(arriendoExistente);
        log.info("Arriendo ID {} actualizado correctamente con nuevos datos", id);
        return convertirAResponse(actualizado);
    }

    /*
     * Elimina un arriendo por ID.
     */
    public void eliminarArriendo(Long id) {
        log.info("Solicitud interna: Intentando eliminar arriendo con ID {}", id);
        if (!arriendoRepository.existsById(id)) {
            log.error("Error en eliminación: No existe un registro con el ID {}", id);
            throw new RuntimeException("No se puede eliminar: el arriendo no existe con ID: " + id);
        }
        arriendoRepository.deleteById(id);
        log.info("Arriendo con ID {} eliminado exitosamente del sistema", id);
    }

    /*
     * Convierte una entidad Arriendo en ArriendoResponse.
     */
    private ArriendoResponse convertirAResponse(Arriendo arriendo) {
        ArriendoResponse response = new ArriendoResponse();
        response.setId(arriendo.getId());
        response.setClienteId(arriendo.getClienteId());
        response.setFechaInicio(arriendo.getFechaInicio());
        response.setFechaFin(arriendo.getFechaFin());
        response.setCosto(arriendo.getCosto());
        response.setEstado(arriendo.getEstado());
        response.setFechaCreacion(arriendo.getFechaCreacion());
        response.setFechaModificacion(arriendo.getFechaModificacion());

        if (arriendo.getTipoBicicleta() != null) {
            response.setTipoBicicletaNombre(arriendo.getTipoBicicleta().getNombre());
        }
        return response;
    }
}