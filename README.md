# UrbanStyle · Sistema de ventas (v2)

Migración del sistema de venta de camisetas a **Maven + JPA (Hibernate) + Git**,
sobre la base de datos **`DB_UrbanStyle`**. Curso *Lenguaje de Programación II* (temas 1 a 3).

## Estado

Esta es la **base del proyecto**: estructura Maven, configuración de JPA y las 12 entidades.
Las pantallas se agregan por módulos, cada uno en su propia rama y con su Pull Request.

## Requisitos

JDK 21 · Eclipse IDE for Enterprise Java and Web Developers · Tomcat 11 · XAMPP (MySQL/MariaDB)

## Puesta en marcha

1. Importar `sql/01_DB_UrbanStyle.sql` en phpMyAdmin.
2. Copiar `src/main/resources/db.properties.example` como `db.properties` y completar usuario y contraseña
   (este archivo no se sube a GitHub).
3. Eclipse: *File → Import → Maven → Existing Maven Projects*.
4. Agregar el proyecto a Tomcat 11 y arrancar. En la consola debe aparecer:
   `[URBANSTYLE] JPA iniciado. Roles en BD: 3`
