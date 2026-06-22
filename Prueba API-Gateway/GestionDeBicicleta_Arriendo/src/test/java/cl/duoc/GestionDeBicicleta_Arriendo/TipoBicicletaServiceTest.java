package cl.duoc.GestionDeBicicleta_Arriendo;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaRequest;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.TipoBicicletaResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.model.TipoBicicleta;
import cl.duoc.GestionDeBicicleta_Arriendo.repository.TipoBicicletaRepository;
import cl.duoc.GestionDeBicicleta_Arriendo.service.TipoBicicletaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_tipo;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=update"
})
public class TipoBicicletaServiceTest {

    @Autowired
    private TipoBicicletaService tipoBicicletaService;

    @MockBean
    private TipoBicicletaRepository tipoBicicletaRepository;

    // TEST MÉTODO: listarTipos()
    @Test
    public void testListarTipos() {
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(1L);
        tipo.setNombre("Paseo");

        when(tipoBicicletaRepository.findAll()).thenReturn(List.of(tipo));

        List<TipoBicicletaResponse> result = tipoBicicletaService.listarTipos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paseo", result.get(0).getNombre());
        verify(tipoBicicletaRepository, times(1)).findAll();
    }

    // TEST MÉTODO: obtenerTipoPorId()
    @Test
    public void testObtenerTipoPorIdExitoso() {
        Long id = 1L;
        TipoBicicleta tipo = new TipoBicicleta();
        tipo.setId(id);
        tipo.setNombre("Ruta");

        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.of(tipo));

        TipoBicicletaResponse result = tipoBicicletaService.obtenerTipoPorId(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Ruta", result.getNombre());
    }

    @Test
    public void testObtenerTipoPorIdNoEncontrado() {
        Long id = 99L;
        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.obtenerTipoPorId(id);
        });

        assertTrue(exception.getMessage().contains("Tipo de bicicleta no encontrado con ID"));
    }

    // TEST MÉTODO: crearTipo()
    @Test
    public void testCrearTipoExitoso() {
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("BMX");

        when(tipoBicicletaRepository.existsByNombreIgnoreCase("BMX")).thenReturn(false);

        TipoBicicleta tipoGuardado = new TipoBicicleta();
        tipoGuardado.setId(10L);
        tipoGuardado.setNombre("BMX");
        when(tipoBicicletaRepository.save(any(TipoBicicleta.class))).thenReturn(tipoGuardado);

        TipoBicicletaResponse response = tipoBicicletaService.crearTipo(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("BMX", response.getNombre());
    }

    @Test
    public void testCrearTipoErrorNombreDuplicado() {
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("Estática");

        when(tipoBicicletaRepository.existsByNombreIgnoreCase("Estática")).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.crearTipo(request);
        });

        assertTrue(exception.getMessage().contains("El tipo de bicicleta ya existe con el nombre"));
        verify(tipoBicicletaRepository, never()).save(any(TipoBicicleta.class));
    }

    // TEST MÉTODO: actualizarTipo()

    @Test
    public void testActualizarTipoExitoso() {
        Long id = 1L;
        TipoBicicletaRequest request = new TipoBicicletaRequest();
        request.setNombre("Mountain Bike Pro");

        TipoBicicleta tipoExistente = new TipoBicicleta();
        tipoExistente.setId(id);
        tipoExistente.setNombre("Mountain Bike");

        when(tipoBicicletaRepository.findById(id)).thenReturn(Optional.of(tipoExistente));
        when(tipoBicicletaRepository.existsByNombreIgnoreCase("Mountain Bike Pro")).thenReturn(false);
        when(tipoBicicletaRepository.save(any(TipoBicicleta.class))).thenReturn(tipoExistente);

        TipoBicicletaResponse response = tipoBicicletaService.actualizarTipo(id, request);

        assertNotNull(response);
        assertEquals("Mountain Bike Pro", response.getNombre());
    }

    // TEST MÉTODO: eliminarTipo()

    @Test
    public void testEliminarTipoExitoso() {
        Long id = 1L;
        when(tipoBicicletaRepository.existsById(id)).thenReturn(true);
        doNothing().when(tipoBicicletaRepository).deleteById(id);

        tipoBicicletaService.eliminarTipo(id);

        verify(tipoBicicletaRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminarTipoNoExiste() {
        Long id = 99L;
        when(tipoBicicletaRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tipoBicicletaService.eliminarTipo(id);
        });

        assertTrue(exception.getMessage().contains("No se puede eliminar: el tipo de bicicleta no existe"));
        verify(tipoBicicletaRepository, never()).deleteById(anyLong());
    }
}