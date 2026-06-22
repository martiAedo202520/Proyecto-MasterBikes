package cl.duoc.GestionDeBicicleta_Arriendo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionDeBicicletaArriendoApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeBicicletaArriendoApplication.class, args);
	}

}
