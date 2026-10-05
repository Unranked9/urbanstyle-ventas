package com.urbanstyle.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.urbanstyle.config.AppConfig;
import com.urbanstyle.dto.ProductoForm;
import com.urbanstyle.entity.Producto;
import com.urbanstyle.service.CatalogoService;
import com.urbanstyle.service.ProductoService;
import com.urbanstyle.service.ServiceException;
import com.urbanstyle.util.ImagenUtil;
import com.urbanstyle.util.Texto;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 * Productos y variantes.
 *   GET  /productos            listado con filtros (todos los roles)
 *   GET  /productos/nuevo      formulario vacío    (ADMIN, SUB_ADMIN)
 *   GET  /productos/editar     formulario con datos (ADMIN, SUB_ADMIN)
 *   POST /productos/guardar    crear / actualizar con imágenes (multipart)
 *   POST /productos/estado     activar / desactivar
 */
@WebServlet({"/productos", "/productos/nuevo", "/productos/editar", "/productos/guardar", "/productos/estado"})
@MultipartConfig(maxFileSize = ImagenUtil.TAMANO_MAXIMO, maxRequestSize = 40L * 1024 * 1024)
public class ProductoController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ProductoService productoService = new ProductoService();
    private final CatalogoService catalogoService = new CatalogoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/productos/nuevo" -> formulario(req, res, null);
            case "/productos/editar" -> {
                Integer id = Texto.entero(req.getParameter("id"));
                try {
                    formulario(req, res, id == null ? null : productoService.buscar(id));
                } catch (ServiceException e) {
                    error(req, e.getMessage());
                    redirigir(req, res, "/productos");
                }
            }
            case "/productos" -> listar(req, res);
            default -> res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/productos/guardar" -> guardar(req, res);
            case "/productos/estado" -> cambiarEstado(req, res);
            default -> res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String texto = Texto.limpiar(req.getParameter("q"));
        Integer categoriaId = Texto.entero(req.getParameter("categoria"));
        Integer marcaId = Texto.entero(req.getParameter("marca"));
        String estado = req.getParameter("estado");
        Boolean activo = "inactivos".equals(estado) ? Boolean.FALSE
                : "todos".equals(estado) ? null : Boolean.TRUE;

        req.setAttribute("productos", productoService.listar(texto, categoriaId, marcaId, activo));
        req.setAttribute("categorias", catalogoService.categorias());
        req.setAttribute("marcas", catalogoService.marcas());
        req.setAttribute("fq", texto);
        req.setAttribute("fCategoria", categoriaId);
        req.setAttribute("fMarca", marcaId);
        req.setAttribute("fEstado", estado == null ? "activos" : estado);
        req.setAttribute("umbralStock", AppConfig.stockBajo());
        vista(req, res, "productos", "Productos", "productos");
    }

    private void formulario(HttpServletRequest req, HttpServletResponse res, Producto producto)
            throws ServletException, IOException {
        req.setAttribute("producto", producto);
        req.setAttribute("categorias", catalogoService.categorias());
        req.setAttribute("marcas", catalogoService.marcas());
        req.setAttribute("colores", catalogoService.colores());
        req.setAttribute("tallas", catalogoService.tallas());
        vista(req, res, "producto-form", producto == null ? "Nuevo producto" : "Editar producto", "productos");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ProductoForm form = new ProductoForm();
        form.setId(Texto.entero(req.getParameter("id")));
        try {
            form.setNombre(req.getParameter("nombre"));
            form.setDescripcion(req.getParameter("descripcion"));
            form.setPrecio(Texto.decimal(req.getParameter("precio")));
            form.setCategoriaId(Texto.entero(req.getParameter("categoriaId")));
            form.setMarcaId(Texto.entero(req.getParameter("marcaId")));
            form.setActivo(req.getParameter("activo") != null);
            form.setVariantes(leerVariantes(req));

            Producto guardado = productoService.guardar(form);
            ok(req, "Producto \"" + guardado.getNombre() + "\" guardado correctamente.");
            redirigir(req, res, "/productos");
        } catch (ServiceException | IllegalArgumentException e) {
            error(req, e.getMessage());
            redirigir(req, res, form.getId() == null ? "/productos/nuevo" : "/productos/editar?id=" + form.getId());
        } catch (PersistenceException e) {
            log("Error al guardar producto", e);
            error(req, "No se pudo guardar el producto. Verifica que no haya variantes repetidas (mismo color y talla).");
            redirigir(req, res, form.getId() == null ? "/productos/nuevo" : "/productos/editar?id=" + form.getId());
        } catch (IllegalStateException e) {
            // Archivo demasiado grande (límite de @MultipartConfig)
            error(req, "Las imágenes superan el tamaño permitido (3 MB por imagen).");
            redirigir(req, res, form.getId() == null ? "/productos/nuevo" : "/productos/editar?id=" + form.getId());
        }
    }

    /**
     * Cada fila de variante del formulario envía los campos con el mismo
     * índice: varId[], varColor[], varTalla[], varStock[], varActivo[],
     * varQuitar[], varFila[] y el archivo "varImagen_{fila}".
     */
    private List<ProductoForm.VarianteForm> leerVariantes(HttpServletRequest req)
            throws IOException, ServletException {
        List<ProductoForm.VarianteForm> filas = new ArrayList<>();
        String[] claves = req.getParameterValues("varFila");
        if (claves == null) {
            return filas;
        }
        String[] ids = req.getParameterValues("varId");
        String[] colores = req.getParameterValues("varColor");
        String[] tallas = req.getParameterValues("varTalla");
        String[] stocks = req.getParameterValues("varStock");
        String[] activos = req.getParameterValues("varActivo");
        String[] quitar = req.getParameterValues("varQuitar");

        for (int i = 0; i < claves.length; i++) {
            ProductoForm.VarianteForm f = new ProductoForm.VarianteForm();
            f.setId(Texto.entero(valor(ids, i)));
            f.setColorId(Texto.entero(valor(colores, i)));
            f.setTallaId(Texto.entero(valor(tallas, i)));
            f.setStock(Texto.entero(valor(stocks, i)));
            f.setActivo("1".equals(valor(activos, i)));
            f.setQuitarImagen("1".equals(valor(quitar, i)));
            Part imagen = req.getPart("varImagen_" + claves[i]);
            if (ImagenUtil.tieneArchivo(imagen)) {
                f.setImagenNueva(ImagenUtil.guardar(imagen));
            }
            filas.add(f);
        }
        return filas;
    }

    private static String valor(String[] arreglo, int i) {
        return arreglo != null && i < arreglo.length ? arreglo[i] : null;
    }

    private void cambiarEstado(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer id = Texto.entero(req.getParameter("id"));
        boolean activo = "1".equals(req.getParameter("activo"));
        try {
            if (id == null) {
                throw new ServiceException("Producto no válido.");
            }
            productoService.cambiarEstado(id, activo);
            ok(req, activo ? "Producto activado." : "Producto desactivado. Ya no aparecerá en ventas.");
        } catch (ServiceException e) {
            error(req, e.getMessage());
        }
        redirigir(req, res, "/productos");
    }
}
