package sistemaventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistemaventa.model.Administrador;
import sistemaventa.repository.AdministradorRepository;
import sistemaventa.service.AdministradorService;

@Service
public class AdministradorServiceImpl implements AdministradorService {
    
    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public java.util.List<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    @Override
    public Administrador save(Administrador admin) {
        return administradorRepository.save(admin);
    }

    @Override
    public Administrador findById(Integer id) {
        return administradorRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        administradorRepository.deleteById(id);
    }

    @Override
    public Administrador findByUsuarioAndContrasena(String usuario, String contrasena) {
        return administradorRepository.findByUsuarioAndContrasena(usuario, contrasena);
    }
}