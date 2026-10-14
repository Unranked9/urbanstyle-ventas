package com.urbanstyle.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.urbanstyle.dto.FiltroVentas;
import com.urbanstyle.entity.EstadoVenta;
import com.urbanstyle.entity.TipoComprobante;
import com.urbanstyle.service.ReporteService;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.service.UsuarioService;
import com.urbanstyle.util.Fechas;
import com.urbanstyle.util.Texto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet({"/reportes/clientes", "/reportes/ventas"})
public class ReporteController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ReporteService reporteService = new ReporteService();
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("fechaEmision", Fechas.fechaHora(LocalDateTime.now()));
        if ("/reportes/ventas".equals(req.getServletPath())) {
            ventas(req, res);
        } else {
            clientes(req, res);
        }
    }

   

    private void clientes(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String texto = Texto.limpiar(req.getParameter("q"));
        String estado = req.getParameter("estado") == null ? "activos" : req.getParameter("estado");
        String tipo = req.getParameter("tipo") == null ? "todos" : req.getParameter("tipo");

        Boolean activo = "inactivos".equals(estado) ? Boolean.FALSE
                : "todos".equals(estado) ? null : Boolean.TRUE;
        Boolean mayorista = "mayoristas".equals(tipo) ? Boolean.TRUE
                : "minoristas".equals(tipo) ? Boolean.FALSE : null;

        req.setAttribute("reporte", reporteService.reporteClientes(texto, activo, mayorista));
        req.setAttribute("fq", texto);
        req.setAttribute("fEstado", estado);
        req.setAttribute("fTipo", tipo);
        req.setAttribute("filtrosTexto", "Estado: " + etiquetaEstadoCliente(estado)
                + " · Tipo: " + etiquetaTipoCliente(tipo)
                + (texto == null ? "" : " · Búsqueda: \"" + texto + "\""));
        vista(req, res, "reporte-clientes", "Reporte de clientes", "reporteClientes");
    }

    private static String etiquetaEstadoCliente(String estado) {
        return switch (estado) {
            case "inactivos" -> "Inactivos";
            case "todos" -> "Todos";
            default -> "Activos";
        };
    }

    private static String etiquetaTipoCliente(String tipo) {
        return switch (tipo) {
            case "mayoristas" -> "Mayoristas";
            case "minoristas" -> "Minoristas";
            default -> "Todos";
        };
    }



    private void ventas(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        FiltroVentas f = new FiltroVentas();
        boolean sinFechas = req.getParameter("desde") == null && req.getParameter("hasta") == null;
        f.setDesde(sinFechas ? ReporteService.inicioDeMes() : Texto.fecha(req.getParameter("desde")));
        f.setHasta(sinFechas ? LocalDate.now() : Texto.fecha(req.getParameter("hasta")));
        f.setUsuarioId(Texto.entero(req.getParameter("vendedor")));

        String estado = req.getParameter("estado") == null ? "" : req.getParameter("estado");
        if ("completadas".equals(estado)) {
            f.setEstado(EstadoVenta.COMPLETADA);
        } else if ("anuladas".equals(estado)) {
            f.setEstado(EstadoVenta.ANULADA);
        }
        String tipo = req.getParameter("tipo") == null ? "" : req.getParameter("tipo");
        if ("BOLETA".equals(tipo) || "FACTURA".equals(tipo)) {
            f.setTipoComprobante(TipoComprobante.valueOf(tipo));
        }

        try {
            req.setAttribute("reporte", reporteService.reporteVentas(f));
        } catch (ServiceException e) {
            error(req, e.getMessage());
            req.setAttribute("reporte", ReporteService.reporteVentasVacio());
        }

        req.setAttribute("filtro", f);
        req.setAttribute("fEstado", estado);
        req.setAttribute("fTipo", tipo);
        req.setAttribute("vendedores", usuarioService.listar());
        req.setAttribute("maximoFilas", ReporteService.MAXIMO_FILAS_VENTAS);
        req.setAttribute("filtrosTexto", "Del " + texto(f.getDesde()) + " al " + texto(f.getHasta())
                + " · Estado: " + ("completadas".equals(estado) ? "Completadas"
                        : "anuladas".equals(estado) ? "Anuladas" : "Todas")
                + " · Comprobante: " + ("BOLETA".equals(tipo) ? "Boletas"
                        : "FACTURA".equals(tipo) ? "Facturas" : "Todos")
                + " · Vendedor: " + nombreVendedor(f.getUsuarioId()));
        vista(req, res, "reporte-ventas", "Reporte de ventas", "reporteVentas");
    }

    private static String texto(LocalDate fecha) {
        return fecha == null ? "—" : Fechas.fecha(fecha.atStartOfDay());
    }

    private String nombreVendedor(Integer id) {
        if (id == null) {
            return "Todos";
        }
        try {
            return usuarioService.buscar(id).getNombreCompleto();
        } catch (ServiceException e) {
            return "Todos";
        }
    }
}
