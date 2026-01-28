package sistemaventa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistemaventa.model.Compra;
import sistemaventa.model.Producto;
import sistemaventa.repository.CompraRepository;
import sistemaventa.repository.ProductoRepository;
import sistemaventa.service.CompraService;

@Service
public class CompraServiceImpl implements CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
public Compra registrarCompra(Compra compra, Producto producto, int cantidadComprada) {
    if (compra == null || producto == null) {
        throw new IllegalArgumentException("Compra o producto no pueden ser nulos");
    }

    // Guardar la compra
    compra = compraRepository.save(compra);

    // Actualizar stock
    int nuevoStock = producto.getCantidadStock() + cantidadComprada;

    // Validación extra por seguridad
    if (nuevoStock > producto.getStockMaximo()) {
        throw new IllegalStateException("Stock máximo excedido tras la compra");
    }

    producto.setCantidadStock(nuevoStock);
    productoRepository.save(producto);
    
    return compra;
}

    @Override
    public Compra obtenerPorId(Integer id) {
        return compraRepository.findById(id).orElse(null);
    }

}
