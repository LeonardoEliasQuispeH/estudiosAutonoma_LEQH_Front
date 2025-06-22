package com.example.demo.Dto;

public class LoginRequest {
    private String usuario;
    private String clave;

    // Getters y setters
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }
}
