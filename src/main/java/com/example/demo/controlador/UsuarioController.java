package com.example.demo.controlador;

import com.example.demo.modelo.Usuario;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class UsuarioController {

    private final String API_URL = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net/usuarios/listar";
    private final String API_URL_2 = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net";

    @GetMapping("/")
    public String inicio() {
        return "login";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        return "inicio";
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

    // 👉 Eliminar usuario
    @GetMapping("/eliminar-usuario/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = API_URL_2 + "/usuarios/eliminar/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/listar";
    }

    //CREAR USUARIO

    @GetMapping("/crear-usuario")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "crear-usuario";
    }

    @PostMapping("/crear-usuario")
    public String crearUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            String url = API_URL_2 + "/usuarios/crear";
            RestTemplate restTemplate = new RestTemplate();

            System.out.println("Enviando usuario: " + usuario);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);

            restTemplate.postForEntity(url, request, Void.class);
            return "redirect:/listar";
        }catch (Exception e) {
            e.printStackTrace(); // muestra el error en consola
            model.addAttribute("error", "Error al crear usuario: " + e.getMessage());
            return "crear-usuario";
        }
    }

    //EDITAR USUARIO
    @GetMapping("/editar-usuario/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            String url = API_URL_2 + "/usuarios/buscar/" + id;
            RestTemplate restTemplate = new RestTemplate();

            Usuario usuario = restTemplate.getForObject(url, Usuario.class);
            model.addAttribute("usuario", usuario);

            return "editar-usuario";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar el usuario: " + e.getMessage());
            return "redirect:/listar";
        }
    }

    @PostMapping("/editar-usuario")
    public String actualizarUsuario(@ModelAttribute Usuario usuario, Model model) {
        try {
            String url = API_URL_2 + "/usuarios/actualizar/" + usuario.getId();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);

            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return "redirect:/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
            return "editar-usuario";
        }
    }




}
