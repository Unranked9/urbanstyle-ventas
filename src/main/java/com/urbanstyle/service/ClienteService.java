package com.urbanstyle.service;

import java.util.List;
import java.util.Locale;

import com.urbanstyle.dao.ClienteDAO;
import com.urbanstyle.dao.impl.ClienteDAOImpl;
import com.urbanstyle.entity.Cliente;
import com.urbanstyle.util.PasswordUtil;
import com.urbanstyle.util.Texto;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this(new ClienteDAOImpl());
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listar(String texto, Boolean activo) {
        return clienteDAO.listar(Texto.limpiar(texto), activo);
    }

    public List<Cliente> buscarParaVenta(String texto) {
        return clienteDAO.buscarActivos(Texto.limpiar(texto), 10);
    }

    public Cliente buscar(int id) {
        return clienteDAO.buscarPorId(id)
                .orElseThrow(() -> new ServiceException("El cliente no existe."));
    }

    /**
     * Crea o actualiza un cliente.
     * La tabla clientes exige username y password: si no se indican al
     * registrar desde caja, se genera el username a partir del email y una
     * contraseña aleatoria (el cliente podrá cambiarla en una futura tienda web).
     */
    public Cliente guardar(Integer id, String nombres, String apellidos, String dni, String telefono,
                           String email, String username, String password, boolean mayorista) {
        nombres = Texto.limpiar(nombres);
        apellidos = Texto.limpiar(apellidos);
        dni = Texto.limpiar(dni);
        telefono = Texto.limpiar(telefono);
        email = Texto.limpiar(email);
        username = Texto.limpiar(username);

        Validaciones.nombre(nombres, "nombres");
        Validaciones.nombre(apellidos, "apellidos");
        if (dni != null && !Texto.esDni(dni)) {
            throw new ServiceException("El DNI debe tener 8 dígitos.");
        }
        if (telefono != null && !Texto.esTelefono(telefono)) {
            throw new ServiceException("El teléfono solo admite números (6 a 15 caracteres).");
        }
        if (email == null || email.length() > 50 || !Texto.esEmail(email)) {
            throw new ServiceException("Ingresa un correo válido (máximo 50 caracteres).");
        }
        email = email.toLowerCase(Locale.ROOT);
        if (username != null && !username.matches("[A-Za-z0-9._-]{4,50}")) {
            throw new ServiceException("El usuario debe tener entre 4 y 50 caracteres (letras, números, punto, guion).");
        }
        if (dni != null && clienteDAO.existeDni(dni, id)) {
            throw new ServiceException("Ya existe un cliente con el DNI " + dni + ".");
        }
        if (clienteDAO.existeEmail(email, id)) {
            throw new ServiceException("Ya existe un cliente con el correo " + email + ".");
        }
        if (username != null && clienteDAO.existeUsername(username, id)) {
            throw new ServiceException("El usuario \"" + username + "\" ya está en uso.");
        }

        Cliente cliente;
        if (id == null) {
            cliente = new Cliente();
            cliente.setActivo(true);
            cliente.setUsername(username != null ? username : generarUsername(email));
            if (password != null && !password.isEmpty()) {
                Validaciones.password(password);
                cliente.setPassword(PasswordUtil.hashear(password));
            } else {
                cliente.setPassword(PasswordUtil.hashear(PasswordUtil.aleatoria()));
            }
        } else {
            cliente = buscar(id);
            if (username != null) {
                cliente.setUsername(username);
            }
            if (password != null && !password.isEmpty()) {
                Validaciones.password(password);
                cliente.setPassword(PasswordUtil.hashear(password));
            }
        }
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setMayorista(mayorista);
        return clienteDAO.guardar(cliente);
    }

    public void cambiarEstado(int id, boolean activo) {
        Cliente cliente = buscar(id);
        cliente.setActivo(activo);
        clienteDAO.guardar(cliente);
    }

    /** "lucia.torres@mail.com" -> "lucia.torres" (o "lucia.torres2" si ya existe). */
    String generarUsername(String email) {
        String base = email.substring(0, email.indexOf('@')).replaceAll("[^A-Za-z0-9._-]", "");
        if (base.length() < 4) {
            base = base + "cliente";   // "ab@mail.com" -> "abcliente"
        }
        if (base.length() > 45) {
            base = base.substring(0, 45);
        }
        String candidato = base;
        int n = 2;
        while (clienteDAO.existeUsername(candidato, null)) {
            candidato = base + n++;
        }
        return candidato;
    }
}
