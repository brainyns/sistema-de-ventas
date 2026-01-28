package sistemaventa.service;

import java.util.List;

import sistemaventa.model.Proveedor;

public interface ProveedorService {
    
    // ✅ El tipo de retorno debe ser específico
    List<Proveedor> listarProveedores();
    
    Proveedor buscarProveedorPorId(Integer id);
    void guardarProveedor(Proveedor proveedor);
    
}