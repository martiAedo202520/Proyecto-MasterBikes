package cl.duoc.GestionDeBicicleta_Arriendo;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cl.duoc.GestionDeBicicleta_Arriendo.client.ClienteFeignClient;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ApiResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ClienteResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ArriendoResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.model.Arriendo;
import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.ArriendoRepository;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.TipoBicicletaRepository;
import cl.duoc.GestionDeBicicleta_Arriendo.service.ArriendoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Extensión de Mockito (no levanta Spring ni BD)
public class ArriendoServiceTest {

    @InjectMocks // Crea la instancia del servicio e inyecta los mocks dentro de él
    private ArriendoService arriendoService;

    @Mock // Crea un simulador puro del repositorio 0% Base de datos
    private ArriendoRepository arriendoRepository;

    @Mock // Crea un simulador puro del cliente Feign externo
    private ClienteFeignClient clienteFeignClient;

    @Mock // Crea un simulador puro del catálogo de bicicletas
    private TipoBicicletaRepository tipoBicicletaRepository;

    // TEST MÉTODO: listarArriendos()
    @Test
    public void testListarArriendos() {
        // GIVEN: Inicialización de datos simulados aislados de la BD
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Mountain Bike");

        Arriendo arriendo = new Arriendo();
        arriendo.setId(1L);
        arriendo.setClienteId(100L);
        arriendo.setTipoBicicleta(tipo);
        arriendo.setEstado("ACTIVO");

        when(arriendoRepository.findAll()).thenReturn(List.of(arriendo));

        // WHEN: Ejecución de la lógica de negocio del microservicio
        List<ArriendoResponse> result = arriendoService.listarArriendos();

        // THEN: Asserts precisos para asegurar la calidad del software entregado
        assertNotNull(result, "La respuesta no debería ser nula");
        assertEquals(1, result.size(), "Debería retornar un registro");
        assertEquals("Mountain Bike", result.get(0).getTipoBicicletaNombre());
        verify(arriendoRepository, times(1)).findAll();
    }

    // TEST MÉTODO: obtenerArriendo()
    @Test
    public void testObtenerArriendoExitoso() {
        // GIVEN
        Long id = 1L;
        Arriendo arriendo = new Arriendo();
        arriendo.setId(id);
        arriendo.setClienteId(100L);
        arriendo.setEstado("ACTIVO");

        when(arriendoRepository.findById(id)).thenReturn(Optional.of(arriendo));

        // WHEN
        ArriendoResponse found = arriendoService.obtenerArriendo(id);

        // THEN
        assertNotNull(found, "El objeto de retorno está vacío");
        assertEquals(id, found.getId(), "El identificador no coincide");
    }

    @Test
    public void testObtenerArriendoNoEncontrado() {
        // GIVEN
        Long id = 99L;
        when(arriendoRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        Exception exception = assertThrows(RuntimeException.class, () -> {
            arriendoService.obtenerArriendo(id);
        }, "Se esperaba RuntimeException del negocio");

        assertTrue(exception.getMessage().contains("Arriendo no encontrado con ID"));
    }

    // TEST MÉTODO: crearArriendo()
    @Test
    public void testCrearArriendoExitoso() {
        // GIVEN
        ArriendoRequest request = new ArriendoRequest();
        request.setClienteId(100L);
        request.setTipoBicicletaId(1L);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusDays(2));
        request.setCosto(15000.0);
        request.setEstado("ACTIVO");

        ApiResponse<ClienteResponse> apiResponseMock = mock(ApiResponse.class);
        when(apiResponseMock.isError()).thenReturn(false);
        when(clienteFeignClient.obtenerClientePorId(100L)).thenReturn(apiResponseMock);

        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Paseo");
        when(tipoBicicletaRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(arriendoRepository.existsByTipoBicicleta_IdAndEstado(1L, "ACTIVO")).thenReturn(false);

        Arriendo arriendoGuardado = new Arriendo();
        arriendoGuardado.setId(10L);
        arriendoGuardado.setClienteId(100L);
        arriendoGuardado.setEstado("ACTIVO");
        arriendoGuardado.setTipoBicicleta(tipo);

        when(arriendoRepository.save(any(Arriendo.class))).thenReturn(arriendoGuardado);

        // WHEN
        ArriendoResponse saved = arriendoService.crearArriendo(request);

        // THEN
        assertNotNull(saved, "Error al guardar el arriendo simulado");
        assertEquals(10L, saved.getId());
        assertEquals("ACTIVO", saved.getEstado());
    }

    // TEST MÉTODO: actualizarArriendo()
    @Test
    public void testActualizarArriendoExitoso() {
        // GIVEN
        Long idExistente = 1L;
        ArriendoRequest request = new ArriendoRequest();
        request.setClienteId(100L);
        request.setTipoBicicletaId(1L);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusDays(1));
        request.setCosto(20000.0);
        request.setEstado("FINALIZADO");

        Arriendo arriendoExistente = new Arriendo();
        arriendoExistente.setId(idExistente);
        arriendoExistente.setEstado("ACTIVO");

        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Mountain Bike");

        when(arriendoRepository.findById(idExistente)).thenReturn(Optional.of(arriendoExistente));

        ApiResponse<ClienteResponse> apiResponseMock = mock(ApiResponse.class);
        when(apiResponseMock.isError()).thenReturn(false);
        when(clienteFeignClient.obtenerClientePorId(100L)).thenReturn(apiResponseMock);
        when(tipoBicicletaRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(arriendoRepository.save(any(Arriendo.class))).thenReturn(arriendoExistente);

        // WHEN
        ArriendoResponse updated = arriendoService.actualizarArriendo(idExistente, request);

        // THEN
        assertNotNull(updated, "La respuesta no debería ser nula al actualizar");
        assertEquals("FINALIZADO", updated.getEstado());
    }

    // TEST MÉTODO: eliminarArriendo()
    @Test
    public void testEliminarArriendoExitoso() {
        // GIVEN
        Long id = 1L;
        when(arriendoRepository.existsById(id)).thenReturn(true);
        doNothing().when(arriendoRepository).deleteById(id);

        // WHEN
        arriendoService.eliminarArriendo(id);

        // THEN
        verify(arriendoRepository, times(1)).deleteById(id);
    }
}