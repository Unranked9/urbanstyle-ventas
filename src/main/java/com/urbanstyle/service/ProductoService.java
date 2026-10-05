package com.urbanstyle.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.ProductoDAO;
import com.urbanstyle.dao.impl.ProductoDAOImpl;
import com.urbanstyle.dto.ProductoForm;
import com.urbanstyle.entity.Categoria;
import com.urbanstyle.entity.Color;
import com.urbanstyle.entity.Marca;
import com.urbanstyle.entity.Producto;
import com.urbanstyle.entity.ProductoVariante;
import com.urbanstyle.entity.Talla;
import com.urbanstyle.util.Texto;

import jakarta.persistence.EntityManager;

public class ProductoService {

    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this(new ProductoDAOImpl());
    }

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listar(String texto, Integer categoriaId, Integer marcaId, Boolean activo) {
        return productoDAO.listar(Texto.limpiar(texto), categoriaId, marcaId, activo);
    }

    public Producto buscar(int id) {
        return productoDAO.buscarPorIdConVariantes(id)
                .orElseThrow(() -> new ServiceException("El producto no existe."));
    }

    public List<ProductoVariante> buscarParaVenta(String texto) {
        return productoDAO.buscarVariantesParaVenta(Texto.limpiar(texto), 40);
    }

    public void cambiarEstado(int id, boolean activo) {
        buscar(id);
        productoDAO.cambiarEstado(id, activo);
    }

    /**
     * Crea o actualiza un producto y sus variantes en una sola transacción.
     * Las variantes existentes no se eliminan (pueden estar en ventas):
     * se activan o desactivan.
     */
    public Producto guardar(ProductoForm form) {
        validar(form);
        return JPAUtil.enTransaccion(em -> {
            Producto producto;
            if (form.getId() == null) {
                producto = new Producto();
            } else {
                producto = em.find(Producto.class, form.getId());
                if (producto == null) {
                    throw new ServiceException("El producto que intentas editar no existe.");
                }
            }

            producto.setNombre(Texto.limpiar(form.getNombre()));
            producto.setDescripcion(Texto.limpiar(form.getDescripcion()));
            producto.setPrecio(form.getPrecio().setScale(2, java.math.RoundingMode.HALF_UP));
            producto.setActivo(form.isActivo());
            producto.setCategoria(referencia(em, Categoria.class, form.getCategoriaId(), "La categoría"));
            producto.setMarca(form.getMarcaId() == null ? null
                    : referencia(em, Marca.class, form.getMarcaId(), "La marca"));

            if (producto.getId() == null) {
                em.persist(producto);
            }

            // Variantes actuales indexadas por id (lazy: se cargan aquí, dentro de la transacción).
            Map<Integer, ProductoVariante> actuales = new HashMap<>();
            for (ProductoVariante v : producto.getVariantes()) {
                actuales.put(v.getId(), v);
            }

            for (ProductoForm.VarianteForm fila : form.getVariantes()) {
                ProductoVariante variante;
                if (fila.getId() != null) {
                    variante = actuales.get(fila.getId());
                    if (variante == null) {
                        throw new ServiceException("Una de las variantes no pertenece a este producto.");
                    }
                } else {
                    variante = new ProductoVariante();
                    producto.agregarVariante(variante);
                }
                variante.setColor(referencia(em, Color.class, fila.getColorId(), "El color"));
                variante.setTalla(referencia(em, Talla.class, fila.getTallaId(), "La talla"));
                variante.setStock(fila.getStock());
                variante.setActivo(fila.isActivo());
                if (fila.getImagenNueva() != null) {
                    variante.setImagenUrl(fila.getImagenNueva());
                } else if (fila.isQuitarImagen()) {
                    variante.setImagenUrl(null);
                }
                if (variante.getId() == null) {
                    em.persist(variante);
                }
            }
            validarCombinacionesUnicas(producto);
            return producto;
        });
    }

    private void validar(ProductoForm form) {
        String nombre = Texto.limpiar(form.getNombre());
        if (nombre == null || nombre.length() > 100) {
            throw new ServiceException("El nombre es obligatorio (máximo 100 caracteres).");
        }
        String descripcion = Texto.limpiar(form.getDescripcion());
        if (descripcion != null && descripcion.length() > 255) {
            throw new ServiceException("La descripción admite máximo 255 caracteres.");
        }
        if (form.getPrecio() == null || form.getPrecio().signum() <= 0
                || form.getPrecio().compareTo(PRECIO_MAXIMO) > 0) {
            throw new ServiceException("Ingresa un precio mayor que cero.");
        }
        if (form.getCategoriaId() == null) {
            throw new ServiceException("Selecciona una categoría.");
        }
        Set<String> combinaciones = new HashSet<>();
        for (ProductoForm.VarianteForm fila : form.getVariantes()) {
            if (fila.getColorId() == null || fila.getTallaId() == null) {
                throw new ServiceException("Cada variante necesita color y talla.");
            }
            if (fila.getStock() == null || fila.getStock() < 0 || fila.getStock() > 1_000_000) {
                throw new ServiceException("El stock de cada variante debe ser un número entre 0 y 1 000 000.");
            }
            if (!combinaciones.add(fila.getColorId() + "-" + fila.getTallaId())) {
                throw new ServiceException("Hay variantes repetidas (mismo color y talla).");
            }
        }
    }

    /** Por si existen variantes en BD que no vinieron en el formulario. */
    private void validarCombinacionesUnicas(Producto producto) {
        Set<String> vistas = new HashSet<>();
        for (ProductoVariante v : producto.getVariantes()) {
            String clave = v.getColor().getId() + "-" + v.getTalla().getId();
            if (!vistas.add(clave)) {
                throw new ServiceException("La variante " + v.getDescripcionCorta() + " ya existe para este producto.");
            }
        }
    }

    private static <T> T referencia(EntityManager em, Class<T> tipo, Integer id, String nombre) {
        T entidad = id == null ? null : em.find(tipo, id);
        if (entidad == null) {
            throw new ServiceException(nombre + " seleccionada no existe.");
        }
        return entidad;
    }
}
