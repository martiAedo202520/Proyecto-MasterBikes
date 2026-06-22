package cl.duoc.GestionDeBicicleta_Arriendo.client;

import cl.duoc.GestionDeBicicleta_Arriendo.dto.ApiResponse;
import cl.duoc.GestionDeBicicleta_Arriendo.dto.ClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", url = "${api.cliente.url}")
public interface ClienteFeignClient {

    @GetMapping("/{id}")
    ApiResponse<ClienteResponse> obtenerClientePorId(@PathVariable("id") Long id);
}

