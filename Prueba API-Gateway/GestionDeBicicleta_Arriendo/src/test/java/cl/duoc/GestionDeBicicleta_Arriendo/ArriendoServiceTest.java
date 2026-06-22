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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=update"
})

public class ArriendoServiceTest {

    @Autowired
    private ArriendoService arriendoService;

    @MockBean
    private ArriendoRepository arriendoRepository;

    @MockBean
    private ClienteFeignClient clienteFeignClient;

    @MockBean
    private TipoBicicletaRepository tipoBicicletaRepository;

    // TEST MÉTODO: listarArriendos()
    @Test
    public void testListarArriendos() {
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Mountain Bike");

        Arriendo arriendo = new Arriendo();
        arriendo.setId(1L);
        arriendo.setClienteId(100L);
        arriendo.setTipoBicicleta(tipo);
        arriendo.setEstado("ACTIVO");

        when(arriendoRepository.findAll()).thenReturn(List.of(arriendo));

        List<ArriendoResponse> result = arriendoService.listarArriendos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mountain Bike", result.get(0).getTipoBicicletaNombre());
    }

    // TEST MÉTODO: obtenerArriendo()
    @Test
    public void testObtenerArriendoExitoso() {
        Long id = 1L;
        Arriendo arriendo = new Arriendo();
        arriendo.setId(id);
        arriendo.setClienteId(100L);
        arriendo.setEstado("ACTIVO");

        when(arriendoRepository.findById(id)).thenReturn(Optional.of(arriendo));

        ArriendoResponse found = arriendoService.obtenerArriendo(id);

        assertNotNull(found);
        assertEquals(id, found.getId());
    }

    @Test
    public void testObtenerArriendoNoEncontrado() {
        Long id = 99L;
        when(arriendoRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            arriendoService.obtenerArriendo(id);
        });

        assertTrue(exception.getMessage().contains("Arriendo no encontrado con ID"));
    }

    // TEST MÉTODO: crearArriendo()
    @Test
    public void testCrearArriendoErrorFechasInvalidas() {
        ArriendoRequest request = new ArriendoRequest();
        request.setClienteId(100L);
        request.setTipoBicicletaId(1L);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().minusDays(1)); // ERROR: Fin antes de inicio
        request.setEstado("ACTIVO");

        // MOCK REAL: Simulamos que la API remota responde un ApiResponse válido y que isError() es false
        ApiResponse<ClienteResponse> apiResponseMock = mock(ApiResponse.class);
        when(apiResponseMock.isError()).thenReturn(false);
        when(clienteFeignClient.obtenerClientePorId(100L)).thenReturn(apiResponseMock);

        TipoBicicleta tipo = new TipoBicicleta();
        when(tipoBicicletaRepository.findById(1L)).thenReturn(Optional.of(tipo));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            arriendoService.crearArriendo(request);
        });

        assertEquals("La fecha de fin debe ser posterior a la fecha de inicio.", exception.getMessage());
    }

    // TEST MÉTODO: actualizarArriendo()
    @Test
    public void testActualizarArriendoExitoso() {
        Long idExistente = 1L;

        ArriendoRequest request = new ArriendoRequest();
        request.setClienteId(200L);
        request.setTipoBicicletaId(2L);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusHours(5));
        request.setCosto(25000.0);
        request.setEstado("FINALIZADO");

        Arriendo arriendoExistente = new Arriendo();
        arriendoExistente.setId(idExistente);
        arriendoExistente.setClienteId(100L);
        arriendoExistente.setEstado("ACTIVO");
        when(arriendoRepository.findById(idExistente)).thenReturn(Optional.of(arriendoExistente));

        // MOCK REAL: Mismo comportamiento para la actualización
        ApiResponse<ClienteResponse> apiResponseMock = mock(ApiResponse.class);
        when(apiResponseMock.isError()).thenReturn(false);
        when(clienteFeignClient.obtenerClientePorId(200L)).thenReturn(apiResponseMock);

        TipoBicicleta nuevoTipo = new TipoBicicleta();
        nuevoTipo.setId(2L);
        nuevoTipo.setNombre("Bicicleta Eléctrica");
        when(tipoBicicletaRepository.findById(2L)).thenReturn(Optional.of(nuevoTipo));

        when(arriendoRepository.save(any(Arriendo.class))).thenReturn(arriendoExistente);

        ArriendoResponse response = arriendoService.actualizarArriendo(idExistente, request);

        assertNotNull(response);
        assertEquals(idExistente, response.getId());
        assertEquals("FINALIZADO", response.getEstado());
        assertEquals("Bicicleta Eléctrica", response.getTipoBicicletaNombre());
    }

    // TEST MÉTODO: eliminarArriendo()
    @Test
    public void testEliminarArriendoExitoso() {
        Long id = 1L;
        when(arriendoRepository.existsById(id)).thenReturn(true);
        doNothing().when(arriendoRepository).deleteById(id);

        arriendoService.eliminarArriendo(id);

        verify(arriendoRepository, times(1)).deleteById(id);
    }
}