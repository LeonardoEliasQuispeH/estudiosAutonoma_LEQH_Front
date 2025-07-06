package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class UsuarioController {

    private final String API_URL = "https://api.allorigins.win/raw?url=http://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net/usuarios/listar";
    private final String API_URL_2 = "https://api.allorigins.win/raw?url=http://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net";

    @GetMapping("/")
    public String inicio() {
        return "login";
    }

    @GetMapping("/buscar-usuario")
    public String mostrarFormulario() {
        return "buscar-usuario"; // formulario inicial
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Usuario[] usuarios = restTemplate.getForObject(API_URL, Usuario[].class);

            System.out.println("Cantidad de usuarios: " + (usuarios != null ? usuarios.length : 0));

            if (usuarios != null) {
                model.addAttribute("usuarios", usuarios);
            } else {
                model.addAttribute("usuarios", new Usuario[0]);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al consumir la API: " + e.getMessage());
            model.addAttribute("usuarios", new Usuario[0]);
        }

        return "usuarios"; // JSP: usuarios.jsp
    }

        @PostMapping("/buscar-usuario")
    public String buscarUsuarioPorId(@RequestParam Long id, Model model) {
        model.addAttribute("idBuscado", id); // Guardamos el ID buscado

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = API_URL_2 + "/usuarios/buscar/" + id;

            ResponseEntity<Usuario> response = new RestTemplate().exchange(
                url,
                HttpMethod.GET,
                entity,
                Usuario.class
            );

            Usuario usuario = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && usuario != null && usuario.getNombre() != null) {
                model.addAttribute("usuario", usuario);
            } else {
                model.addAttribute("usuario", new Usuario()); // Para que entre al bloque del front
            }

        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("error", "Usuario con ID " + id + " no existe.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al buscar el usuario: " + e.getMessage());
        }

        return "buscar-usuario";
    }





}
