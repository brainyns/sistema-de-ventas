package sistemaventa.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import sistemaventa.model.Producto;
import sistemaventa.model.ReporteInventarioDTO;
import sistemaventa.repository.ProductoRepository;
import sistemaventa.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        Producto guardado = productoRepository.save(producto);
        System.out.println(" Producto guardado en la base de datos: " + guardado);
        return guardado;
    }

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> lista = productoRepository.findAll();
        System.out.println(" Productos en base de datos: " + lista.size());
        return lista;
    }

    @Override
    public Producto obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }
    @Override
    public Producto actualizarProducto(Producto productoActualizado) {
        if (productoRepository.existsById(productoActualizado.getId())) {
            Producto actualizado = productoRepository.save(productoActualizado);
            System.out.println("Producto actualizado: " + actualizado);
            return actualizado;
        } else {
            System.out.println("Error: Producto con ID " + productoActualizado.getId() + " no encontrado para actualizar.");
            return null;
        }
    }

    @Override
    public void eliminarProducto(Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            System.out.println(" Producto eliminado con ID: " + id);
        } else {
            System.out.println("No se encontró producto con ID: " + id);
        }
    }
    @Override
    public ReporteInventarioDTO generarReporteInventario() {
        List<Producto> productos = obtenerTodosLosProductos();

        long bajoStock = 0;
        long stockAlto = 0;
        double valorTotal = 0;
        Map<String, Long> byTipo = new LinkedHashMap<>();

        for (Producto p : productos) {
            valorTotal += p.getPrecio() * p.getCantidadStock();

            if (p.getCantidadStock() < p.getStockMinimo()) {
                bajoStock++;
            } else if (p.getCantidadStock() > p.getStockMaximo()) {
                stockAlto++;
            }

            String tipo = getTipoNormalizado(p);
            byTipo.merge(tipo, 1L, Long::sum);
        }

        long totalProductos = productos.size();
        long stockNormal = totalProductos - bajoStock - stockAlto;

        List<String> typeLabels = new ArrayList<>(byTipo.keySet());
        List<Long> typeData = new ArrayList<>(byTipo.values());

        List<String> stateLabels = List.of("Bajo", "Normal", "Alto");
        List<Long> stateData = List.of(bajoStock, stockNormal, stockAlto);

        return new ReporteInventarioDTO(
                productos,
                totalProductos,
                bajoStock,
                stockNormal,
                stockAlto,
                valorTotal,
                byTipo.size(),
                typeLabels,
                typeData,
                stateLabels,
                stateData
        );
    }

    private String getTipoNormalizado(Producto p) {
        if (p.getTipo() == null || p.getTipo().isBlank()) {
            return "Sin Clasificar";
        }
        return p.getTipo();
    }
}
