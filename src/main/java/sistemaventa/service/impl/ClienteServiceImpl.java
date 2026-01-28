package sistemaventa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sistemaventa.model.Administrador;
import sistemaventa.model.Cliente;
import sistemaventa.repository.AdministradorRepository;
import sistemaventa.repository.ClienteRepository;
import sistemaventa.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    @Transactional
    public Cliente guardarCliente(Cliente nuevoCliente, Integer adminId) {
        // Buscar el administrador
        Administrador admin = administradorRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        
        // Asignar el administrador al cliente
        nuevoCliente.setAdministrador(admin);
        
        // Guardar el cliente
        return clienteRepository.save(nuevoCliente);
    }

    @Override
    public Cliente buscarClientePorId(Integer identificacion) {
        return clienteRepository.findById(identificacion)
            .orElse(null);
    }
@Override
@Transactional
public Cliente actualizarCliente(Cliente clienteActualizado, Integer adminId) {

    Cliente clienteExistente = clienteRepository.findById(clienteActualizado.getIdentificacion())
        .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteActualizado.getIdentificacion()));

    clienteExistente.setNombre(clienteActualizado.getNombre());
    clienteExistente.setApellido(clienteActualizado.getApellido());
    clienteExistente.setTelefono(clienteActualizado.getTelefono());
    clienteExistente.setEmail(clienteActualizado.getEmail());
    clienteExistente.setDireccion(clienteActualizado.getDireccion());

    // Asignar SIEMPRE el administrador que está logueado
    Administrador admin = administradorRepository.findById(adminId)
        .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
    clienteExistente.setAdministrador(admin);

    return clienteRepository.save(clienteExistente);
}

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarCliente(Integer identificacion) {
        if (!clienteRepository.existsById(identificacion)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clienteRepository.deleteById(identificacion);
    }
}