package sistemaventa.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistemaventa.model.Proveedor;
import sistemaventa.repository.ProveedorRepository;
import sistemaventa.service.ProveedorService;


@Service
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> listarProveedores() {
       
        return proveedorRepository.findAll(); 
    }

    @Override
    public Proveedor buscarProveedorPorId(Integer id) {
        Optional<Proveedor> proveedor = proveedorRepository.findById(id);
        return proveedor.orElse(null);
    }
    
    @Override
    public void guardarProveedor(Proveedor proveedor) {
        proveedorRepository.save(proveedor);
    }
}