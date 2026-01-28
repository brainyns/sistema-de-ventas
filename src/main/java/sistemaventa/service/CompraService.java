package sistemaventa.service;

import sistemaventa.model.Compra;
import sistemaventa.model.Producto;

public interface CompraService {

    // Método que registra la compra y actualiza el stock
    Compra registrarCompra(Compra compra, Producto producto, int cantidadComprada);

    // Obtener compra por id
    Compra obtenerPorId(Integer id);
}
