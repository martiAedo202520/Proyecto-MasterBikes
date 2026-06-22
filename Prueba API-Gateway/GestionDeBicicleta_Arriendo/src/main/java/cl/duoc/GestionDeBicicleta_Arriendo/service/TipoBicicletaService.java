package cl.duoc.GestionDeBicicleta_Arriendo.service;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.TipoBicicletaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoBicicletaService {

    private static final Logger log = LoggerFactory.getLogger(TipoBicicletaService.class);
    private final TipoBicicletaRepository tipoBicicletaRepository;

    /*
     * Listar todos los tipos de bicicleta.
     */
    public List<TipoBicicletaResponse> listarTipos() {
        log.info("Solicitud interna: Procesando listado de tipos de bicicleta");
        List<TipoBicicleta> tipos = tipoBicicletaRepository.findAll();
        List<TipoBicicletaResponse> respuesta = new ArrayList<>();

        for (TipoBicicleta t : tipos) {
            respuesta.add(convertirAResponse(t));
        }
        log.info("Listado completado. Cantidad de tipos encontrados: {}", respuesta.size());
        return respuesta;
    }

    /*
     * Obtener tipo por ID.
     */
    public TipoBicicletaResponse obtenerTipoPorId(Long id) {
        log.info("Solicitud interna: Buscando tipo de bicicleta con ID {}", id);
        TipoBicicleta tipo = tipoBicicletaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en búsqueda: Tipo de bicicleta no encontrado con ID: {}", id);
                    return new RuntimeException("Tipo de bicicleta no encontrado con ID: " + id);
                });
        return convertirAResponse(tipo);
    }

    /*
     * Crear un nuevo tipo de bicicleta.
     */
    public TipoBicicletaResponse crearTipo(TipoBicicletaRequest request) {
        log.info("Solicitud interna: Creando tipo de bicicleta con nombre: {}", request.getNombre());

        if (tipoBicicletaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            log.error("Fallo de negocio: Ya existe un tipo de bicicleta con el nombre: {}", request.getNombre());
            throw new RuntimeException("El tipo de bicicleta ya existe con el nombre: " + request.getNombre());
        }

        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setNombre(request.getNombre());

        TipoBicicleta guardado = tipoBicicletaRepository.save(tipo);
        log.info("Tipo de bicicleta registrado con éxito. ID asignado: {}", guardado.getId());
        return convertirAResponse(guardado);
    }

    /*
     * Actualizar tipo de bicicleta.
     */
    public TipoBicicletaResponse actualizarTipo(Long id, TipoBicicletaRequest request) {
        log.info("Solicitud interna: Actualizando tipo de bicicleta ID {}", id);

        TipoBicicleta tipoExistente = tipoBicicletaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en actualización: Tipo de bicicleta no encontrado con ID: {}", id);
                    return new RuntimeException("Tipo de bicicleta no encontrado con ID: " + id);
                });

        if (tipoBicicletaRepository.existsByNombreIgnoreCase(request.getNombre()) &&
                !tipoExistente.getNombre().equalsIgnoreCase(request.getNombre())) {
            log.error("Fallo de negocio en actualización: El nombre {} ya está en uso", request.getNombre());
            throw new RuntimeException("El nombre del tipo de bicicleta ya está en uso.");
        }

        tipoExistente.setNombre(request.getNombre());
        TipoBicicleta actualizado = tipoBicicletaRepository.save(tipoExistente);
        log.info("Tipo de bicicleta ID {} actualizado correctamente", id);
        return convertirAResponse(actualizado);
    }

    /*
     * Eliminar tipo de bicicleta.
     */
    public void eliminarTipo(Long id) {
        log.info("Solicitud interna: Intentando eliminar tipo de bicicleta ID {}", id);
        if (!tipoBicicletaRepository.existsById(id)) {
            log.error("Error en eliminación: No existe el tipo de bicicleta con ID {}", id);
            throw new RuntimeException("No se puede eliminar: el tipo de bicicleta no existe con ID: " + id);
        }
        tipoBicicletaRepository.deleteById(id);
        log.info("Tipo de bicicleta ID {} eliminado exitosamente", id);
    }

    /*
     * Método Auxiliar Mapper.
     */
    private TipoBicicletaResponse convertirAResponse(TipoBicicleta tipo) {
        TipoBicicletaResponse response = new TipoBicicletaResponse();
        response.setId(tipo.getId());
        response.setNombre(tipo.getNombre());
        return response;
    }
}