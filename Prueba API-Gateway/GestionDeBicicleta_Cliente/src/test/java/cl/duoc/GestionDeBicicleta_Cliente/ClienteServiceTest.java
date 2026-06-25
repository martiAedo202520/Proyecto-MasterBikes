package cl.duoc.GestionDeBicicleta_Cliente;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteRequest;
import cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteResponse;
import cl.duoc.GestionDeBicicleta_Cliente.model.Cliente;
import cl.duoc.GestionDeBicicleta_Cliente.repository.ClienteRepository;
import cl.duoc.GestionDeBicicleta_Cliente.service.ClienteService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)// se usa la extensión de Mockito (no levanta Spring ni BD)
public class ClienteServiceTest {

    @InjectMocks //@InjectMocks crea la instancia del servicio e inyecta los mocks dentro de él
    private ClienteService clienteService;


    @Mock //@Mock crea un simulador puro del repositorio 0% Base de datos
    private ClienteRepository clienteRepository;

    // TEST MÉTODO: obtenerTodos()
    @Test
    public void testObtenerTodos() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setRut("12.345.678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setActivo(true);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<ClienteResponse> result = clienteService.obtenerTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12.345.678-9", result.get(0).getRut());
        verify(clienteRepository, times(1)).findAll();
    }
    // TEST MÉTODO: buscarPorId()
    @Test
    public void testBuscarPorIdExitoso() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setRut("12.345.678-9");
        cliente.setNombre("Juan");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        ClienteResponse result = clienteService.buscarPorId(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Juan", result.getNombre());
    }

    @Test
    public void testBuscarPorIdNoEncontrado() {
        Long id = 99L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorId(id);
        });

        assertTrue(exception.getMessage().contains("Cliente no encontrado con ID"));
    }

    // TEST MÉTODO: guardar()
    @Test
    public void testGuardarExitoso() {
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");
        request.setEmail("juan@duoc.cl");
        request.setNombre("Juan");
        request.setActivo(true);

        // Simulamos que ni el RUT ni el Email existen previamente
        when(clienteRepository.findByRut("12.345.678-9")).thenReturn(Optional.empty());
        when(clienteRepository.existsByEmail("juan@duoc.cl")).thenReturn(false);

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setId(10L);
        clienteGuardado.setRut("12.345.678-9");
        clienteGuardado.setEmail("juan@duoc.cl");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        ClienteResponse response = clienteService.guardar(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("12.345.678-9", response.getRut());
    }

    @Test
    public void testGuardarErrorRutDuplicado() {
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");

        // Simulamos que el RUT ya existe
        when(clienteRepository.findByRut("12.345.678-9")).thenReturn(Optional.of(new Cliente()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.guardar(request);
        });

        assertTrue(exception.getMessage().contains("ya se encuentra registrado."));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    //  TEST MÉTODO: actualizar()
    @Test
    public void testActualizarExitoso() {
        Long id = 1L;
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");
        request.setEmail("nuevo_email@duoc.cl");
        request.setNombre("Juan Modificado");
        request.setActivo(true);

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(id);
        clienteExistente.setRut("12.345.678-9");
        clienteExistente.setEmail("juan@duoc.cl");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByEmail("nuevo_email@duoc.cl")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        ClienteResponse response = clienteService.actualizar(id, request);

        assertNotNull(response);
        assertEquals("nuevo_email@duoc.cl", response.getEmail());
    }

    // TEST MÉTODO: eliminar()
    @Test
    public void testEliminarExitoso() {
        Long id = 1L;
        when(clienteRepository.existsById(id)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(id);

        clienteService.eliminar(id);

        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminarNoExiste() {
        Long id = 99L;
        when(clienteRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.eliminar(id);
        });

        assertTrue(exception.getMessage().contains("No se puede eliminar: el cliente no existe"));
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}