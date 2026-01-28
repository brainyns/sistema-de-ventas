package sistemaventa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaventa.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca todos los clientes asociados a un administrador específico.
     *
     * @param administradorId ID del administrador
     * @return lista de clientes del administrador
     */
    List<Cliente> findByAdministrador_IdAdministrador(Integer administradorId);

    /**
     * Busca un cliente por su correo electrónico.
     *
     * @param email correo del cliente
     * @return cliente encontrado o null si no existe
     */
    Cliente findByEmail(String email);

    /**
     * Verifica si ya existe un cliente con una identificación específica.
     *
     * @param identificacion ID del cliente
     * @return true si existe, false si no
     */
    boolean existsByIdentificacion(Integer identificacion);
}
