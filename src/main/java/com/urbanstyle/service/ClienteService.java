package com.urbanstyle.service;

import java.util.List;
import java.util.Locale;

import com.urbanstyle.dao.ClienteDAO;
import com.urbanstyle.dao.impl.ClienteDAOImpl;
import com.urbanstyle.entity.Cliente;
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

    /** Crea (id null) o actualiza un cliente. */
    public Cliente guardar(Integer id, String nombres, String apellidos, String dni, String telefono,
                           String email, boolean mayorista) {
        nombres = Texto.limpiar(nombres);
        apellidos = Texto.limpiar(apellidos);
        dni = Texto.limpiar(dni);
        telefono = Texto.limpiar(telefono);
        email = Texto.limpiar(email);

        UsuarioService.validarNombre(nombres, "nombres");
        UsuarioService.validarNombre(apellidos, "apellidos");
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
        if (dni != null && clienteDAO.existeDni(dni, id)) {
            throw new ServiceException("Ya existe un cliente con el DNI " + dni + ".");
        }
        if (clienteDAO.existeEmail(email, id)) {
            throw new ServiceException("Ya existe un cliente con el correo " + email + ".");
        }

        Cliente cliente;
        if (id == null) {
            cliente = new Cliente();
            cliente.setActivo(true);
        } else {
            cliente = buscar(id);
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
}
