package com.urbanstyle.dao.impl;

import java.util.List;
import java.util.Optional;

import com.urbanstyle.config.JPAUtil;
import com.urbanstyle.dao.CatalogoDAO;
import com.urbanstyle.entity.Categoria;
import com.urbanstyle.entity.Color;
import com.urbanstyle.entity.Marca;
import com.urbanstyle.entity.Talla;

/**
 * Implementación genérica para las tablas maestras. Cada instancia trabaja
 * con una entidad distinta; el nombre de la entidad se usa en el JPQL.
 *
 * Uso: CatalogoDAOImpl.categorias(), .marcas(), .colores(), .tallas()
 */
public class CatalogoDAOImpl<T> implements CatalogoDAO<T> {

    private final Class<T> tipo;
    private final String entidad;
    /** JPQL que cuenta cuántos registros usan el elemento (:id). */
    private final String jpqlUso;

    private CatalogoDAOImpl(Class<T> tipo, String jpqlUso) {
        this.tipo = tipo;
        this.entidad = tipo.getSimpleName();
        this.jpqlUso = jpqlUso;
    }

    public static CatalogoDAOImpl<Categoria> categorias() {
        return new CatalogoDAOImpl<>(Categoria.class,
                "SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :id");
    }

    public static CatalogoDAOImpl<Marca> marcas() {
        return new CatalogoDAOImpl<>(Marca.class,
                "SELECT COUNT(p) FROM Producto p WHERE p.marca.id = :id");
    }

    public static CatalogoDAOImpl<Color> colores() {
        return new CatalogoDAOImpl<>(Color.class,
                "SELECT COUNT(v) FROM ProductoVariante v WHERE v.color.id = :id");
    }

    public static CatalogoDAOImpl<Talla> tallas() {
        return new CatalogoDAOImpl<>(Talla.class,
                "SELECT COUNT(v) FROM ProductoVariante v WHERE v.talla.id = :id");
    }

    @Override
    public List<T> listar() {
        // Las tallas se ordenan por id para respetar S, M, L, XL, 38, 39...
        String orden = tipo == Talla.class ? "e.id" : "e.nombre";
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT e FROM " + entidad + " e ORDER BY " + orden, tipo)
                .getResultList());
    }

    @Override
    public Optional<T> buscarPorId(int id) {
        return JPAUtil.consultar(em -> Optional.ofNullable(em.find(tipo, id)));
    }

    @Override
    public T guardar(T entidad) {
        // merge sirve tanto para insertar (id null) como para actualizar.
        return JPAUtil.enTransaccion(em -> em.merge(entidad));
    }

    @Override
    public void eliminar(int id) {
        JPAUtil.ejecutarEnTransaccion(em -> {
            T encontrado = em.find(tipo, id);
            if (encontrado != null) {
                em.remove(encontrado);
            }
        });
    }

    @Override
    public boolean existeNombre(String nombre, Integer excluirId) {
        return JPAUtil.consultar(em -> em
                .createQuery("SELECT COUNT(e) FROM " + entidad
                        + " e WHERE LOWER(e.nombre) = LOWER(:nombre) AND (:excluir IS NULL OR e.id <> :excluir)",
                        Long.class)
                .setParameter("nombre", nombre)
                .setParameter("excluir", excluirId)
                .getSingleResult() > 0);
    }

    @Override
    public boolean estaEnUso(int id) {
        return JPAUtil.consultar(em -> em
                .createQuery(jpqlUso, Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0);
    }
}
