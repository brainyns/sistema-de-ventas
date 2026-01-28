package sistemaventa.service;

import java.util.List;
import sistemaventa.model.Venta;

public interface VentaService {
    Venta guardarVenta(Venta venta);
    List<Venta> obtenerTodas();
}
