package com.urbanstyle.service;

import java.util.List;
import java.util.Map;

import com.urbanstyle.dao.impl.CatalogoDAOImpl;
import com.urbanstyle.entity.Categoria;
import com.urbanstyle.entity.Color;
import com.urbanstyle.entity.Marca;
import com.urbanstyle.entity.Talla;
import com.urbanstyle.util.Texto;

/**
 * Mantenimiento de categorías, marcas, colores y tallas.
 */
public class CatalogoService {

    public enum Tipo {
        CATEGORIAS("Categorías", 50, "productos"), MARCAS("Marcas", 50, "productos"),
        COLORES("Colores", 30, "variantes"), TALLAS("Tallas", 10, "variantes");

        private final String titulo;
        private final int largoMaximo;
        /** Que se cuenta en la columna "En uso": productos o variantes. */
        private final String unidadUso;

        Tipo(String titulo, int largoMaximo, String unidadUso) {
            this.titulo = titulo;
            this.largoMaximo = largoMaximo;
            this.unidadUso = unidadUso;
        }

        public String getUnidadUso() {
            return unidadUso;
        }

        public String getTitulo() {
            return titulo;
        }

        public int getLargoMaximo() {
            return largoMaximo;
        }

        public String getClave() {
            return name().toLowerCase();
        }

        public static Tipo desde(String valor) {
            if (valor == null) {
                return CATEGORIAS;
            }
            for (Tipo t : values()) {
                if (t.name().equalsIgnoreCase(valor.trim())) {
                    return t;
                }
            }
            return CATEGORIAS;
        }
    }

    private final CatalogoDAOImpl<Categoria> categorias = CatalogoDAOImpl.categorias();
    private final CatalogoDAOImpl<Marca> marcas = CatalogoDAOImpl.marcas();
    private final CatalogoDAOImpl<Color> colores = CatalogoDAOImpl.colores();
    private final CatalogoDAOImpl<Talla> tallas = CatalogoDAOImpl.tallas();

    public List<Categoria> categorias() {
        return categorias.listar();
    }

    public List<Marca> marcas() {
        return marcas.listar();
    }

    public List<Color> colores() {
        return colores.listar();
    }

    public List<Talla> tallas() {
        return tallas.listar();
    }

    public List<?> listar(Tipo tipo) {
        return switch (tipo) {
            case CATEGORIAS -> categorias();
            case MARCAS -> marcas();
            case COLORES -> colores();
            case TALLAS -> tallas();
        };
    }

    /** id del registro -> cantidad de productos/variantes que lo usan (sin entrada = 0). */
    public Map<Integer, Long> usoPorRegistro(Tipo tipo) {
        return switch (tipo) {
            case CATEGORIAS -> categorias.contarUsoPorRegistro();
            case MARCAS -> marcas.contarUsoPorRegistro();
            case COLORES -> colores.contarUsoPorRegistro();
            case TALLAS -> tallas.contarUsoPorRegistro();
        };
    }

    public void guardar(Tipo tipo, Integer id, String nombre, String descripcion) {
        String limpio = Texto.limpiar(nombre);
        if (limpio == null || limpio.length() > tipo.getLargoMaximo()) {
            throw new ServiceException("El nombre es obligatorio (máximo " + tipo.getLargoMaximo() + " caracteres).");
        }
        String desc = Texto.limpiar(descripcion);
        if (desc != null && desc.length() > 200) {
            throw new ServiceException("La descripción admite máximo 200 caracteres.");
        }
        switch (tipo) {
            case CATEGORIAS -> {
                verificarNombre(categorias, limpio, id);
                Categoria c = id == null ? new Categoria() : existente(categorias, id);
                c.setNombre(limpio);
                c.setDescripcion(desc);
                categorias.guardar(c);
            }
            case MARCAS -> {
                verificarNombre(marcas, limpio, id);
                Marca m = id == null ? new Marca() : existente(marcas, id);
                m.setNombre(limpio);
                marcas.guardar(m);
            }
            case COLORES -> {
                verificarNombre(colores, limpio, id);
                Color c = id == null ? new Color() : existente(colores, id);
                c.setNombre(limpio);
                colores.guardar(c);
            }
            case TALLAS -> {
                verificarNombre(tallas, limpio, id);
                Talla t = id == null ? new Talla() : existente(tallas, id);
                t.setNombre(limpio.toUpperCase());
                tallas.guardar(t);
            }
        }
    }

    public void eliminar(Tipo tipo, int id) {
        CatalogoDAOImpl<?> dao = switch (tipo) {
            case CATEGORIAS -> categorias;
            case MARCAS -> marcas;
            case COLORES -> colores;
            case TALLAS -> tallas;
        };
        existente(dao, id);
        if (dao.estaEnUso(id)) {
            throw new ServiceException("No se puede eliminar: hay productos que lo usan.");
        }
        dao.eliminar(id);
    }

    private static void verificarNombre(CatalogoDAOImpl<?> dao, String nombre, Integer id) {
        if (dao.existeNombre(nombre, id)) {
            throw new ServiceException("Ya existe un registro con el nombre \"" + nombre + "\".");
        }
    }

    private static <T> T existente(CatalogoDAOImpl<T> dao, int id) {
        return dao.buscarPorId(id).orElseThrow(() -> new ServiceException("El registro no existe."));
    }
}
