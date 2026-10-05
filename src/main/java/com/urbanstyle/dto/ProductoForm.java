package com.urbanstyle.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Datos del formulario de producto con sus filas de variantes. */
public class ProductoForm {

    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer categoriaId;
    private Integer marcaId;
    private boolean activo = true;
    private List<VarianteForm> variantes = new ArrayList<>();

    public static class VarianteForm {
        private Integer id;
        private Integer colorId;
        private Integer tallaId;
        private Integer stock;
        private boolean activo = true;
        /** URL de una imagen recién subida (null = conservar la actual). */
        private String imagenNueva;
        private boolean quitarImagen;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getColorId() {
            return colorId;
        }

        public void setColorId(Integer colorId) {
            this.colorId = colorId;
        }

        public Integer getTallaId() {
            return tallaId;
        }

        public void setTallaId(Integer tallaId) {
            this.tallaId = tallaId;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public boolean isActivo() {
            return activo;
        }

        public void setActivo(boolean activo) {
            this.activo = activo;
        }

        public String getImagenNueva() {
            return imagenNueva;
        }

        public void setImagenNueva(String imagenNueva) {
            this.imagenNueva = imagenNueva;
        }

        public boolean isQuitarImagen() {
            return quitarImagen;
        }

        public void setQuitarImagen(boolean quitarImagen) {
            this.quitarImagen = quitarImagen;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Integer marcaId) {
        this.marcaId = marcaId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<VarianteForm> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<VarianteForm> variantes) {
        this.variantes = variantes;
    }
}
