package com.urbanstyle.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.urbanstyle.config.AppConfig;
import com.urbanstyle.dao.DashboardDAO;
import com.urbanstyle.dao.impl.DashboardDAOImpl;
import com.urbanstyle.dto.ResumenDashboard;

public class DashboardService {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEE dd", Locale.of("es", "PE"));

    private final DashboardDAO dashboardDAO = new DashboardDAOImpl();

    public ResumenDashboard resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime manana = hoy.plusDays(1).atStartOfDay();
        int umbral = AppConfig.stockBajo();

        ResumenDashboard r = new ResumenDashboard();
        r.setTotalHoy(dashboardDAO.totalVendido(inicioHoy, manana));
        r.setVentasHoy(dashboardDAO.contarVentas(inicioHoy, manana));
        r.setTotalMes(dashboardDAO.totalVendido(hoy.withDayOfMonth(1).atStartOfDay(), manana));
        r.setProductosActivos(dashboardDAO.contarProductosActivos());
        r.setClientesActivos(dashboardDAO.contarClientesActivos());
        r.setUmbralStockBajo(umbral);
        r.setVariantesStockBajo(dashboardDAO.contarStockBajo(umbral));
        r.setStockBajo(dashboardDAO.listarStockBajo(umbral, 8));
        r.setTopProductos(dashboardDAO.topProductos(hoy.minusDays(29).atStartOfDay(), 5));
        r.setUltimasVentas(dashboardDAO.ultimasVentas(6));

       
        Map<LocalDate, BigDecimal> porDia = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            porDia.put(hoy.minusDays(i), BigDecimal.ZERO);
        }
        for (Object[] fila : dashboardDAO.fechasYTotales(hoy.minusDays(6).atStartOfDay(), manana)) {
            LocalDate dia = ((LocalDateTime) fila[0]).toLocalDate();
            porDia.computeIfPresent(dia, (k, acumulado) -> acumulado.add((BigDecimal) fila[1]));
        }
        porDia.forEach((dia, total) -> {
            r.getEtiquetasSemana().add(DIA.format(dia));
            r.getTotalesSemana().add(total);
        });
        return r;
    }
}
