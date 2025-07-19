package com.example.demo.controlador;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.example.demo.modelo.Producto;

@Controller
public class ProductoController {
    
    private final String API_URL = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net";


    //LISTAR PRODUCTOS

    @GetMapping("/listar-productos")
    public String listarProductos(Model model) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Producto[] productos = restTemplate.getForObject(API_URL+"/productos/listar", Producto[].class);

            System.out.println("Cantidad de productos: " + (productos != null ? productos.length : 0));

            if (productos != null) {
                model.addAttribute("productos", productos);
            } else {
                model.addAttribute("productos", new Producto[0]);
            }

        } catch (Exception e) {
            System.out.println("❌ Error al consumir la API: " + e.getMessage());
            model.addAttribute("productos", new Producto[0]);
        }

        return "productos"; // JSP: usuarios.jsp
    }

    // 👉 Eliminar usuario
    @GetMapping("/eliminar-producto/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = API_URL + "/productos/eliminar/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar producto: " + e.getMessage());
        }
        return "redirect:/listar-productos";
    }


    //CREAR USUARIO

    @GetMapping("/crear-producto")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "crear-producto";
    }

    @PostMapping("/crear-producto")
    public String crearProducto(@ModelAttribute Producto producto, Model model) {
        try {
            String url = API_URL + "/productos/crear";
            RestTemplate restTemplate = new RestTemplate();

            System.out.println("Enviando usuario: " + producto);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Producto> request = new HttpEntity<>(producto, headers);

            restTemplate.postForEntity(url, request, Void.class);
            return "redirect:/listar-productos";
        }catch (Exception e) {
            e.printStackTrace(); // muestra el error en consola
            model.addAttribute("error", "Error al crear producto: " + e.getMessage());
            return "crear-producto";
        }
    }

    //EDITAR USUARIO
    @GetMapping("/editar-producto/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        try {
            String url = API_URL + "/productos/buscar/" + id;
            RestTemplate restTemplate = new RestTemplate();

            Producto producto = restTemplate.getForObject(url, Producto.class);
            model.addAttribute("producto", producto);

            return "editar-producto";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo cargar el producto: " + e.getMessage());
            return "redirect:/listar-productos";
        }
    }

    @PostMapping("/editar-producto")
    public String actualizarProducto(@ModelAttribute Producto producto, Model model) {
        try {
            String url = API_URL + "/productos/actualizar/" + producto.getId();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Producto> request = new HttpEntity<>(producto, headers);

            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return "redirect:/listar-productos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            return "editar-producto";
        }
    }


}
