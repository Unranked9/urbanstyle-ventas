package com.urbanstyle.controller.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.service.ProductoService;
import com.urbanstyle.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /api/productos/buscar?q=texto -> variantes disponibles para vender. */
@WebServlet("/api/productos/buscar")
public class ApiProductoController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductoService productoService = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ProductoVariante v : productoService.buscarParaVenta(req.getParameter("q"))) {
            // Se arma un mapa plano: serializar la entidad completa con Gson
            // recorrería las relaciones (producto -> variantes -> producto...).
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("varianteId", v.getId());
            fila.put("productoId", v.getProducto().getId());
            fila.put("producto", v.getProducto().getNombre());
            fila.put("categoria", v.getProducto().getCategoria().getNombre());
            fila.put("marca", v.getProducto().getMarca() == null ? null : v.getProducto().getMarca().getNombre());
            fila.put("color", v.getColor().getNombre());
            fila.put("talla", v.getTalla().getNombre());
            fila.put("precio", v.getProducto().getPrecio());
            fila.put("stock", v.getStock());
            fila.put("imagenUrl", v.getImagenUrl());
            resultado.add(fila);
        }
        JsonUtil.ok(res, resultado);
    }
}
