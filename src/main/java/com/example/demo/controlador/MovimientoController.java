package com.example.demo.controlador;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Dto.MovimientoProductoDto;

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

    
}
