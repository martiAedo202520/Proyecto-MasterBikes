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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // ************* Deshabilita la BD por completo ***************
public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    // GIVEN - WHEN - THEN
    @Test
    public void testObtenerTodos() {
        // GIVEN: Inicialización de datos simulados aislados de la BD
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setRut("12.345.678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setActivo(true);

        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        // WHEN: Ejecución de la lógica de negocio del microservicio
        List<ClienteResponse> result = clienteService.obtenerTodos();

        // THEN: Asserts precisos para asegurar la calidad del software entregado
        assertNotNull(result, "La respuesta no debería ser nula");
        assertEquals(1, result.size(), "Debería retornar un registro");
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorIdExitoso() {
        // GIVEN
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setRut("12.345.678-9");
        cliente.setNombre("Juan");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        // WHEN
        ClienteResponse result = clienteService.buscarPorId(id);

        // THEN
        assertNotNull(result, "El objeto de retorno está vacío");
        assertEquals(id, result.getId(), "El identificador no coincide");
    }

    @Test
    public void testBuscarPorIdNoEncontrado() {
        // GIVEN
        Long id = 99L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorId(id);
        }, "Se esperaba RuntimeException del negocio");
    }

    @Test
    public void testGuardarExitoso() {
        // GIVEN
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");
        request.setEmail("juan@duoc.cl");
        request.setNombre("Juan");

        when(clienteRepository.findByRut(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setId(10L);
        clienteGuardado.setRut("12.345.678-9");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        // WHEN
        ClienteResponse response = clienteService.guardar(request);

        // THEN
        assertNotNull(response, "Error al guardar el cliente simulado");
        assertEquals(10L, response.getId());
    }

    @Test
    public void testGuardarErrorRutDuplicado() {
        // GIVEN
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");

        when(clienteRepository.findByRut("12.345.678-9")).thenReturn(Optional.of(new Cliente()));

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            clienteService.guardar(request);
        });
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    public void testActualizarExitoso() {
        // GIVEN
        Long id = 1L;
        ClienteRequest request = new ClienteRequest();
        request.setRut("12.345.678-9");
        request.setEmail("nuevo@duoc.cl");
        request.setNombre("Juan Modificado");

        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(id);
        clienteExistente.setRut("12.345.678-9");
        clienteExistente.setEmail("juan@duoc.cl");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        // WHEN
        ClienteResponse response = clienteService.actualizar(id, request);

        // THEN
        assertNotNull(response, "La respuesta no debería ser nula al actualizar");
    }

    @Test
    public void testEliminarExitoso() {
        // GIVEN
        Long id = 1L;
        when(clienteRepository.existsById(id)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(id);

        // WHEN
        clienteService.eliminar(id);

        // THEN
        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    public void testEliminarNoExiste() {
        // GIVEN
        Long id = 99L;
        when(clienteRepository.existsById(id)).thenReturn(false);

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            clienteService.eliminar(id);
        });
    }

    @Test
    void dadaUnaListaVacia_cuandoSeListanTodos_entoncesRetornaListaVacia() {
        // GIVEN
        Mockito.when(clienteRepository.findAll()).thenReturn(java.util.List.of());

        // WHEN:
        List<cl.duoc.GestionDeBicicleta_Cliente.dto.ClienteResponse> resultado = clienteService.obtenerTodos();

        // THEN:
        org.junit.jupiter.api.Assertions.assertNotNull(resultado);
        org.junit.jupiter.api.Assertions.assertTrue(resultado.isEmpty());
    }


}