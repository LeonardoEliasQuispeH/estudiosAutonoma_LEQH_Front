package com.example.demo.Dto;

public class MovimientoProductoDto {
    private Long idMovimiento;
    private Long idProducto;
    private String nombreProducto;
    private String tipoMovimiento;
    private String fechaMovimiento;
    private int cantidadMovimiento;

    
    public MovimientoProductoDto() {
    }
    public Long getIdMovimiento() {
        return idMovimiento;
    }
    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }
    public Long getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombreProducto() {
        return nombreProducto;
    }
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    public String getFechaMovimiento() {
        return fechaMovimiento;
    }
    public void setFechaMovimiento(String fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
    public int getCantidadMovimiento() {
        return cantidadMovimiento;
    }
    public void setCantidadMovimiento(int cantidadMovimiento) {
        this.cantidadMovimiento = cantidadMovimiento;
    }

    
}

