package sistemaventa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaventa.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
     // JpaRepository ya tiene CRUD
    
     Administrador findByUsuarioAndContrasena(String usuario, String contrasena);
}