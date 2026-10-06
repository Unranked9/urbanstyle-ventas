package com.urbanstyle.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.TipoComprobante;
import com.urbanstyle.entity.Venta;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.service.UsuarioService;
import com.urbanstyle.service.HistorialService;
import com.urbanstyle.util.Texto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet({"/historial", "/historial/detalle"})
public class HistorialController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final HistorialService historialService = new HistorialService();
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if ("/historial/detalle".equals(req.getServletPath())) {
            detalle(req, res);
            return;
        }
        FiltroVentas f = new FiltroVentas();
        // Por defecto: últimos 30 días.
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
        BigDecimal totalCompletadas = BigDecimal.ZERO;
        int completadas = 0;
        for (Venta v : ventas) {
            if (!v.isAnulada()) {
                totalCompletadas = totalCompletadas.add(v.getTotal());
                completadas++;
            }
        }
        req.setAttribute("ventas", ventas);
        req.setAttribute("prendas", historialService.contarPrendas(ventas));
        req.setAttribute("totalCompletadas", totalCompletadas);
        req.setAttribute("cantidadCompletadas", completadas);
        req.setAttribute("vendedores", usuarioService.listar());
        req.setAttribute("filtro", f);
        req.setAttribute("fEstado", estado == null ? "" : estado);
        req.setAttribute("fTipo", tipo == null ? "" : tipo);
        vista(req, res, "historial", "Historial de ventas", "historial");
    }

    private void detalle(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer id = Texto.entero(req.getParameter("id"));
        try {
            if (id == null) {
                throw new ServiceException("Venta no válida.");
            }
            req.setAttribute("venta", historialService.buscarDetalle(id));
            vista(req, res, "venta-detalle", "Venta #" + id, "historial");
        } catch (ServiceException e) {
            error(req, e.getMessage());
            redirigir(req, res, "/historial");
        }
    }
}
