package sistemaventa.service;

import java.util.List;
import sistemaventa.model.Cliente;

public interface ClienteService {

    Cliente guardarCliente(Cliente nuevoCliente, Integer adminId);

    Cliente buscarClientePorId(Integer identificacion);

    Cliente actualizarCliente(Cliente clienteActualizado, Integer adminId);

    List<Cliente> listarClientes();

    void eliminarCliente(Integer identificacion);
}
