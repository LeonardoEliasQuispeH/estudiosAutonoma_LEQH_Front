package com.example.demo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.Dto.LoginRequest;
import com.example.demo.Servicio.LoginService;
import com.example.demo.modelo.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // login.jsp o login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@ModelAttribute LoginRequest loginRequest, HttpSession session) {
        Usuario usuario = loginService.autenticar(loginRequest);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            return "redirect:/inicio";
        } else {
            return "redirect:/login?error=true";
        }
    }


}
