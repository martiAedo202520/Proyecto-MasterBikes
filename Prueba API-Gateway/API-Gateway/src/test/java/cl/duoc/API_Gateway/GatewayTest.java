package cl.duoc.API_Gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGatewayEndpoint() {
        // Ejecuta una prueba automática simulando entrar a http://localhost:8084/gateway/health
        webTestClient.get()
                .uri("/gateway/health")
                .exchange()
                .expectStatus().isOk() // Verifica que el servidor devuelva un estado 200 OK
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP"); // Verifica que el JSON diga status "UP"
    }
}