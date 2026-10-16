package com.urbanstyle.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.Venta;

/** Datos que muestra el dashboard. */
public class ResumenDashboard {

    private BigDecimal totalHoy = BigDecimal.ZERO;
    private long ventasHoy;
    private BigDecimal totalMes = BigDecimal.ZERO;
    private long productosActivos;
    private long clientesActivos;
    private long variantesStockBajo;
    private int umbralStockBajo;
    private List<ProductoVariante> stockBajo = new ArrayList<>();
    private List<String> etiquetasSemana = new ArrayList<>();
    private List<BigDecimal> totalesSemana = new ArrayList<>();
    private List<ProductoVendido> topProductos = new ArrayList<>();
    private List<Venta> ultimasVentas = new ArrayList<>();

    public BigDecimal getTotalHoy() {
        return totalHoy;
    }

    public void setTotalHoy(BigDecimal totalHoy) {
        this.totalHoy = totalHoy;
    }

    public long getVentasHoy() {
        return ventasHoy;
    }

    public void setVentasHoy(long ventasHoy) {
        this.ventasHoy = ventasHoy;
    }

    public BigDecimal getTotalMes() {
        return totalMes;
    }

    public void setTotalMes(BigDecimal totalMes) {
        this.totalMes = totalMes;
    }

    public long getProductosActivos() {
        return productosActivos;
    }

    public void setProductosActivos(long productosActivos) {
        this.productosActivos = productosActivos;
    }

    public long getClientesActivos() {
        return clientesActivos;
    }

    public void setClientesActivos(long clientesActivos) {
        this.clientesActivos = clientesActivos;
    }

    public long getVariantesStockBajo() {
        return variantesStockBajo;
    }

    public void setVariantesStockBajo(long variantesStockBajo) {
        this.variantesStockBajo = variantesStockBajo;
    }

    public int getUmbralStockBajo() {
        return umbralStockBajo;
    }

    public void setUmbralStockBajo(int umbralStockBajo) {
        this.umbralStockBajo = umbralStockBajo;
    }

    public List<ProductoVariante> getStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(List<ProductoVariante> stockBajo) {
        this.stockBajo = stockBajo;
    }

    public List<String> getEtiquetasSemana() {
        return etiquetasSemana;
    }

    public void setEtiquetasSemana(List<String> etiquetasSemana) {
        this.etiquetasSemana = etiquetasSemana;
    }

    public List<BigDecimal> getTotalesSemana() {
        return totalesSemana;
    }

    public void setTotalesSemana(List<BigDecimal> totalesSemana) {
        this.totalesSemana = totalesSemana;
    }

    public List<ProductoVendido> getTopProductos() {
        return topProductos;
    }

    public void setTopProductos(List<ProductoVendido> topProductos) {
        this.topProductos = topProductos;
    }

    public List<Venta> getUltimasVentas() {
        return ultimasVentas;
    }

    public void setUltimasVentas(List<Venta> ultimasVentas) {
        this.ultimasVentas = ultimasVentas;
    }
}
