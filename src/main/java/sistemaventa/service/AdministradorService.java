package sistemaventa.service;

import java.util.List;

import sistemaventa.model.Administrador;

public interface AdministradorService {
    
    List<Administrador> findAll();
    
    Administrador save(Administrador admin);  // ✅ Debe estar declarado aquí
    
    Administrador findById(Integer id);
    
    void deleteById(Integer id);
    
    Administrador findByUsuarioAndContrasena(String usuario, String contrasena);
}