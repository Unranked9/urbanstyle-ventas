package com.urbanstyle.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.urbanstyle.dto.ProductoVendido;
import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.Venta;

/**
 * Consultas de resumen para el dashboard (totales, conteos y rankings).
 */
public interface DashboardDAO {

    /** Suma de ventas completadas en el rango [desde, hasta). */
    BigDecimal totalVendido(LocalDateTime desde, LocalDateTime hasta);

    long contarVentas(LocalDateTime desde, LocalDateTime hasta);

    /** Fecha y total de cada venta completada en el rango (para el gráfico). */
    List<Object[]> fechasYTotales(LocalDateTime desde, LocalDateTime hasta);

    List<ProductoVendido> topProductos(LocalDateTime desde, int limite);

    List<Venta> ultimasVentas(int limite);

    long contarProductosActivos();

    long contarClientesActivos();

    List<ProductoVariante> listarStockBajo(int umbral, int limite);

    long contarStockBajo(int umbral);
}
