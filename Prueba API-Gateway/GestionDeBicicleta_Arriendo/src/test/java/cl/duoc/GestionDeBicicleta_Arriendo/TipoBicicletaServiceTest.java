package cl.duoc.GestionDeBicicleta_Arriendo;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.TipoBicicletaRepository;
import cl.duoc.GestionDeBicicleta_Arriendo.service.TipoBicicletaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Extensión de Mockito (no levanta Spring ni BD)
public class TipoBicicletaServiceTest {

    @InjectMocks // Crea la instancia del servicio e inyecta los mocks dentro de él
    private TipoBicicletaService tipoBicicletaService;

    @Mock // Crea un simulador puro del repositorio 0% Base de datos
    private TipoBicicletaRepository tipoBicicletaRepository;

    // TEST MÉTODO: listarTipos()
    @Test
    public void testListarTipos() {
        // GIVEN: Inicialización de datos simulados aislados de la BD
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Paseo");

        when(tipoBicicletaRepository.findAll()).thenReturn(List.of(tipo));

        // WHEN: Ejecución de la lógica de negocio del microservicio
        List<TipoBicicletaResponse> result = tipoBicicletaService.listarTipos();

        // THEN: Asserts precisos para asegurar la calidad del software entregado
        assertNotNull(result, "La respuesta no debería ser nula");
        assertEquals(1, result.size(), "Debería retornar un registro");
        assertEquals("Paseo", result.get(0).getNombre());
        verify(tipoBicicletaRepository, times(1)).findAll();
    }

    // TEST MÉTODO: obtenerTipoPorId()
    @Test
    public void testObtenerTipoPorIdExitoso() {
        // GIVEN
        Long id = 1L;
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(id);
        tipo.setNombre("Ruta");

        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.of(tipo));

        // WHEN
        TipoBicicletaResponse result = tipoBicicletaService.obtenerTipoPorId(id);

        // THEN
        assertNotNull(result, "El objeto de retorno está vacío");
        assertEquals(id, result.getId(), "El identificador no coincide");
        assertEquals("Ruta", result.getNombre());
    }

    @Test
    public void testObtenerTipoPorIdNoEncontrado() {
        // GIVEN
        Long id = 99L;
        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.obtenerTipoPorId(id);
        }, "Se esperaba RuntimeException del negocio");

        assertTrue(exception.getMessage().contains("Tipo de bicicleta no encontrado con ID"));
    }

    // TEST MÉTODO: crearTipo()
    @Test
    public void testCrearTipoExitoso() {
        // GIVEN
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("BMX");

        when(tipoBicicletaRepository.existsByNombreIgnoreCase("BMX")).thenReturn(false);

        TipoBicicleta tipoGuardado = new TipoBicicleta();
        tipoGuardado.setId(10L);
        tipoGuardado.setNombre("BMX");
        when(tipoBicicletaRepository.save(any(TipoBicicleta.class))).thenReturn(tipoGuardado);

        // WHEN
        TipoBicicletaResponse response = tipoBicicletaService.crearTipo(request);

        // THEN
        assertNotNull(response, "Error al guardar el tipo simulado");
        assertEquals(10L, response.getId());
        assertEquals("BMX", response.getNombre());
    }

    @Test
    public void testCrearTipoErrorNombreDuplicado() {
        // GIVEN
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("Estática");

        when(tipoBicicletaRepository.existsByNombreIgnoreCase("Estática")).thenReturn(true);

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.crearTipo(request);
        }, "Se esperaba RuntimeException por nombre duplicado");

        assertTrue(exception.getMessage().contains("El tipo de bicicleta ya existe con el nombre"));
        verify(tipoBicicletaRepository, never()).save(any(TipoBicicleta.class));
    }

    // TEST MÉTODO: actualizarTipo()
    @Test
    public void testActualizarTipoExitoso() {
        // GIVEN
        Long id = 1L;
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("Mountain Bike Pro");

        TipoBicicleta tipoExistente = new TipoBicicleta();
        tipoExistente.setId(id);
        tipoExistente.setNombre("Mountain Bike");

        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.of(tipoExistente));
        when(tipoBicicletaRepository.existsByNombreIgnoreCase("Mountain Bike Pro")).thenReturn(false);
        when(tipoBicicletaRepository.save(any(TipoBicicleta.class))).thenReturn(tipoExistente);

        // WHEN
        TipoBicicletaResponse response = tipoBicicletaService.actualizarTipo(id, request);

        // THEN
        assertNotNull(response, "La respuesta no debería ser nula al actualizar");
        assertEquals("Mountain Bike Pro", response.getNombre());
    }

    // TEST MÉTODO: eliminarTipo()
    @Test
    public void testEliminarTipoExitoso() {
        // GIVEN
        Long id = 1L;
        when(tipoBicicletaRepository.existsById(id)).thenReturn(true);
        doNothing().when(tipoBicicletaRepository).deleteById(id);

        // WHEN
        tipoBicicletaService.eliminarTipo(id);

        // THEN
        verify(tipoBicicletaRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminarTipoNoExiste() {
        // GIVEN
        Long id = 99L;
        when(tipoBicicletaRepository.existsById(id)).thenReturn(false);

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.eliminarTipo(id);
        }, "Se esperaba RuntimeException al intentar eliminar un id inexistente");

        assertTrue(exception.getMessage().contains("No se puede eliminar: el tipo de bicicleta no existe"));
        verify(tipoBicicletaRepository, never()).deleteById(anyLong());
    }
}