package cl.duoc.API_Gateway.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayController {
    @GetMapping("/gateway/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("component", "API Gateway MasterBikes");
        response.put("message", "Gateway verificado en puerto 8084");
        return response;
    }
}
