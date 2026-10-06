package com.urbanstyle.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.TipoComprobante;
import com.urbanstyle.entity.Venta;
import com.urbanstyle.service.HistorialService;
import com.urbanstyle.util.Texto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Exporta a CSV las ventas del historial con los mismos filtros de la pantalla.
 */
@WebServlet("/historial/csv")
public class HistorialCsvController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String SEP = ";";

    private final HistorialService historialService = new HistorialService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        FiltroVentas f = new FiltroVentas();
        boolean sinFiltros = req.getParameter("desde") == null && req.getParameter("hasta") == null;
        f.setDesde(sinFiltros ? LocalDate.now().minusDays(29) : Texto.fecha(req.getParameter("desde")));
        f.setHasta(sinFiltros ? LocalDate.now() : Texto.fecha(req.getParameter("hasta")));
        f.setTexto(Texto.limpiar(req.getParameter("q")));
        f.setUsuarioId(Texto.entero(req.getParameter("vendedor")));
        String estado = req.getParameter("estado");
        if ("completadas".equals(estado)) {
            f.setEstado(EstadoVenta.COMPLETADA);
        } else if ("anuladas".equals(estado)) {
            f.setEstado(EstadoVenta.ANULADA);
        }
        String tipo = req.getParameter("tipo");
        if ("BOLETA".equals(tipo) || "FACTURA".equals(tipo)) {
            f.setTipoComprobante(TipoComprobante.valueOf(tipo));
        }

        List<Venta> ventas = historialService.listar(f);

        res.setContentType("text/csv; charset=UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setHeader("Content-Disposition", "attachment; filename=ventas.csv");

        PrintWriter out = res.getWriter();
        out.write('\uFEFF'); // BOM para que Excel reconozca las tildes
        out.write("Fecha;Comprobante;Cliente;Vendedor;Total;Estado\r\n");
        for (Venta v : ventas) {
            String cliente = v.getCliente() == null ? "Cliente varios" : v.getCliente().getNombreCompleto();
            out.write(campo(v.getFechaTexto()) + SEP
                    + campo(v.getComprobante().getNumeroCompleto()) + SEP
                    + campo(cliente) + SEP
                    + campo(v.getUsuario().getNombreCompleto()) + SEP
                    + campo(v.getTotal().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()) + SEP
                    + campo(v.getEstado().getEtiqueta()) + "\r\n");
        }
        out.flush();
    }

    /** Escapa un valor para CSV con separador ';'. */
    private static String campo(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(SEP) || valor.contains("\"") || valor.contains("\n") || valor.contains("\r")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
