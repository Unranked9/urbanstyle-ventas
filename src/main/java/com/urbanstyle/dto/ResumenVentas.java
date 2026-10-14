package com.urbanstyle.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResumenVentas {

    public static class TotalVendedor {
        private final String vendedor;
        private final long ventas;
        private final BigDecimal total;

        public TotalVendedor(String vendedor, long ventas, BigDecimal total) {
            this.vendedor = vendedor;
            this.ventas = ventas;
            this.total = total == null ? BigDecimal.ZERO : total;
        }

        public String getVendedor() {
            return vendedor;
        }

        public long getVentas() {
            return ventas;
        }

        public BigDecimal getTotal() {
            return total;
        }
    }

    private long completadas;
    private long anuladas;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal igv = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private long prendas;
    private final List<TotalVendedor> porVendedor = new ArrayList<>();

    public long getRegistros() {
        return completadas + anuladas;
    }

    public long getCompletadas() {
        return completadas;
    }

    public void setCompletadas(long completadas) {
        this.completadas = completadas;
    }

    public long getAnuladas() {
        return anuladas;
    }

    public void setAnuladas(long anuladas) {
        this.anuladas = anuladas;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv == null ? BigDecimal.ZERO : igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total == null ? BigDecimal.ZERO : total;
    }

    public long getPrendas() {
        return prendas;
    }

    public void setPrendas(long prendas) {
        this.prendas = prendas;
    }

    public List<TotalVendedor> getPorVendedor() {
        return porVendedor;
    }
}
