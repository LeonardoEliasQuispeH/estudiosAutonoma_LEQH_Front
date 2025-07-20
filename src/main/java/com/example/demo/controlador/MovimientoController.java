package com.example.demo.controlador;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Dto.MovimientoProductoDto;
import com.example.demo.modelo.Producto;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MovimientoController {

    private final String API_URL = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net";

    @GetMapping("/listar-movimientos")
    public String listarMovimientos(Model model) {
        try {
            String url = API_URL + "/movimientos/listar-con-producto"; // Ajusta si tu endpoint es diferente
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MovimientoProductoDto[]> response = restTemplate.getForEntity(url, MovimientoProductoDto[].class);
            List<MovimientoProductoDto> movimientos = Arrays.asList(response.getBody());

            model.addAttribute("movimientos", movimientos);
            return "listar-movimientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los movimientos: " + e.getMessage());
            return "listar-movimientos";
        }
    }

    //EXPORTAR EN CSV
    @GetMapping("/exportar-csv")
    public void exportarCSV(HttpServletResponse response) {
        String externalUrl = "https://servicioestudios-ewdpfvhpdgg4ekb8.canadacentral-01.azurewebsites.net/movimientos/exportar-csv";

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Llama al microservicio
            ResponseEntity<byte[]> externalResponse = restTemplate.exchange(
                externalUrl,
                HttpMethod.GET,
                null,
                byte[].class
            );

            // Configura headers para la descarga
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=movimientos.csv");

            // Escribe los bytes en la respuesta
            response.getOutputStream().write(externalResponse.getBody());
            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //REGISTRAR MOVIMIENTO

    @GetMapping("/registrar-movimiento")
    public String mostrarFormularioMovimiento(Model model) {

        // Obtener lista de productos desde la API
        String urlProductos = API_URL + "/productos/listar";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Producto[]> response = restTemplate.getForEntity(urlProductos, Producto[].class);
        List<Producto> productos = Arrays.asList(response.getBody());

        model.addAttribute("movimiento", new MovimientoProductoDto());
        model.addAttribute("productos", productos);
        return "registrar-movimiento";
    }

    @PostMapping("/registrar-movimiento")
public String registrarMovimiento(@ModelAttribute MovimientoProductoDto movimiento, Model model) {
    try {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Registrar el movimiento
        String urlMovimiento = API_URL + "/movimientos/crear";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MovimientoProductoDto> movimientoRequest = new HttpEntity<>(movimiento, headers);
        restTemplate.postForEntity(urlMovimiento, movimientoRequest, Void.class);

        // 2. Obtener el producto actual
        String urlProducto = API_URL + "/productos/buscar/" + movimiento.getIdProducto();
        Producto producto = restTemplate.getForObject(urlProducto, Producto.class);

        // 3. Actualizar stock
        if (producto != null) {
            int stockActual = producto.getCantidad();
            int cantidad = movimiento.getCantidadMovimiento();

            if (movimiento.getTipoMovimiento().equalsIgnoreCase("entrada")) {
                producto.setCantidad(stockActual + cantidad);
            } else if (movimiento.getTipoMovimiento().equalsIgnoreCase("salida")) {
                producto.setCantidad(stockActual - cantidad);
            }

            // 4. Enviar actualización al servicio de productos
            String actualizarUrl = API_URL + "/productos/actualizar/" + producto.getId();
            HttpEntity<Producto> updateRequest = new HttpEntity<>(producto, headers);
            restTemplate.exchange(actualizarUrl, HttpMethod.PUT, updateRequest, Void.class);
        }

        return "redirect:/listar-movimientos";

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Error al registrar movimiento: " + e.getMessage());
        return "registrar-movimiento";
    }
}

    
}
