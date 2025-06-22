package com.example.demo.Servicio;

import com.example.demo.Dto.LoginRequest;
import com.example.demo.modelo.Usuario;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {

    private final String LOGIN_API_URL = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net/login"; // Cambia esta URL

    public Usuario autenticar(LoginRequest loginRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        try {
            ResponseEntity<Usuario> response = restTemplate.postForEntity(LOGIN_API_URL, request, Usuario.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.out.println("Error en la autenticación: " + e.getMessage());
        }

        return null;
    }
}
