package sistemaventa.service;

import java.util.List;
import sistemaventa.model.Producto;
import sistemaventa.model.ReporteInventarioDTO;

public interface ProductoService {

    Producto guardarProducto(Producto producto);

    List<Producto> obtenerTodosLosProductos();

    Producto obtenerProductoPorId(Integer id);

    Producto actualizarProducto(Producto productoActualizado);

    void eliminarProducto(Integer id);

    ReporteInventarioDTO generarReporteInventario();
}
