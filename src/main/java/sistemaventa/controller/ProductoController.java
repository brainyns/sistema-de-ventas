package sistemaventa.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sistemaventa.model.Producto;
import sistemaventa.service.ProductoService;


@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/agregar")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        return "Agregar_Producto";
    }


 @PostMapping("/agregar")
public String agregarProducto(@ModelAttribute Producto producto,
                              @RequestParam("archivoImagen") MultipartFile imagenFile,
                              Model model) {

    try {
        String error = validarStock(producto);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("producto", producto);
            return "Agregar_Producto";
        }

        if (!imagenFile.isEmpty()) {
            producto.setImagen(imagenFile.getBytes());
        }

        productoService.guardarProducto(producto);
        return "redirect:/productos/ver";

    } catch (IOException e) {
        model.addAttribute("error", " Error al guardar la imagen: " + e.getMessage());
        return "Agregar_Producto";
    } catch (Exception e) {
        model.addAttribute("error", "Error al guardar el producto: " + e.getMessage());
        return "Agregar_Producto";
    }
}




@GetMapping("/catalogo")
public String catalogoCliente(@RequestParam(value = "tipo", required = false) String tipo, Model model) {
    List<Producto> productos = productoService.obtenerTodosLosProductos();

    // Filtrar por tipo si el usuario lo selecciona
    if (tipo != null && !tipo.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getTipo() != null && p.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    
    List<Map<String, Object>> productosConImagen = productos.stream().map(prod -> {
        Map<String, Object> p = new HashMap<>();
        p.put("nombre", prod.getNombre());
        p.put("tipo", prod.getTipo());
        p.put("modelo", prod.getModelo());
        p.put("precio", prod.getPrecio());

        if (prod.getImagen() != null) {
            String base64 = Base64.getEncoder().encodeToString(prod.getImagen());
            p.put("imagenBase64", "data:image/jpeg;base64," + base64);
        } else {
            p.put("imagenBase64", null);
        }
        return p;
    }).collect(Collectors.toList());

    // Extraer lista de tipos únicos para el filtro
    List<String> tipos = productoService.obtenerTodosLosProductos().stream()
            .map(Producto::getTipo)
            .filter(t -> t != null && !t.isEmpty())
            .distinct()
            .collect(Collectors.toList());

    model.addAttribute("productos", productosConImagen);
    model.addAttribute("tipos", tipos);
    model.addAttribute("tipoSeleccionado", tipo);

    return "catalogo_lista"; // plantilla que muestra productos para un tipo
}



   
@GetMapping("/inventario")
  
public String verReporteInventario(Model model) {
    List<Producto> listaProductos = productoService.obtenerTodosLosProductos();

    Map<String, Map<String, List<Producto>>> reporteInventario = listaProductos.stream()
        .collect(Collectors.groupingBy(
            p -> (p.getTipo() != null && !p.getTipo().isEmpty()) ? p.getTipo() : "Sin Clasificar",
            Collectors.collectingAndThen(
                Collectors.toList(),
                productosDelGrupo -> {
                    Map<String, List<Producto>> filtros = new HashMap<>();

                    filtros.put("bajos", productosDelGrupo.stream()
                        .filter(p -> p.getCantidadStock() < p.getStockMinimo())
                        .collect(Collectors.toList()));

                    filtros.put("excedidos", productosDelGrupo.stream()
                        .filter(p -> p.getCantidadStock() > p.getStockMaximo())
                        .collect(Collectors.toList()));

                    filtros.put("ok", productosDelGrupo.stream()
                        .filter(p -> p.getCantidadStock() >= p.getStockMinimo() && p.getCantidadStock() <= p.getStockMaximo())
                        .collect(Collectors.toList()));

                    return filtros;
                }
            )
        ));

 
    for (String tipo : reporteInventario.keySet()) {
        Map<String, List<Producto>> grupo = reporteInventario.get(tipo);
        grupo.putIfAbsent("bajos", List.of());
        grupo.putIfAbsent("excedidos", List.of());
        grupo.putIfAbsent("ok", List.of());
    }

    model.addAttribute("reporteInventario", reporteInventario);
    return "inventario";
}


    @GetMapping("/ver")
    public String listarProductos(Model model) {
    List<Producto> productos = productoService.obtenerTodosLosProductos();

    // Convertir byte[] a cadena Base64 solo para mostrar
    List<Map<String, Object>> productosConImagen = productos.stream().map(prod -> {
        Map<String, Object> p = new HashMap<>();
        p.put("id", prod.getId());
        p.put("nombre", prod.getNombre());
        p.put("tipo", prod.getTipo());
        p.put("modelo", prod.getModelo());
        p.put("precio", prod.getPrecio());
        p.put("cantidadStock", prod.getCantidadStock());
        p.put("stockMinimo", prod.getStockMinimo());
        p.put("stockMaximo", prod.getStockMaximo());

        if (prod.getImagen() != null) {
            String base64 = Base64.getEncoder().encodeToString(prod.getImagen());
            p.put("imagenBase64", "data:image/jpeg;base64," + base64);
        } else {
            p.put("imagenBase64", null);
        }
        return p;
    }).collect(Collectors.toList());

    model.addAttribute("productos", productosConImagen);
    return "ver_productos";
}

    @GetMapping("/buscar")
    public String buscarProducto(@RequestParam(value = "id", required = false) Integer id, Model model) {
        
        // Obtener lista de tipos para el dropdown
        List<String> tiposDisponibles = productoService.obtenerTodosLosProductos().stream()
                .map(Producto::getTipo)
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("tiposDisponibles", tiposDisponibles);

        if (id == null) {
            return "Editar_Producto"; 
        }

        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null) {
            model.addAttribute("error", " Producto con ID " + id + " no encontrado.");
            return "Editar_Producto";
        }

        model.addAttribute("producto", producto);
        return "Editar_Producto";
    }

  
    @PostMapping("/editar")
    public String editarProducto(@ModelAttribute Producto producto, Model model) {
        
        // Obtener lista de tipos para el dropdown
        List<String> tiposDisponibles = productoService.obtenerTodosLosProductos().stream()
                .map(Producto::getTipo)
                .filter(t -> t != null && !t.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("tiposDisponibles", tiposDisponibles);
    
        String error = validarStock(producto);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("producto", producto); 
            return "Editar_Producto";
        }
        
        try {
            productoService.actualizarProducto(producto);
            model.addAttribute("exito", " Producto actualizado correctamente");
         
            return "redirect:/productos/ver"; 
        } catch (Exception e) {
            model.addAttribute("error", " Error al actualizar el producto: " + e.getMessage());
            model.addAttribute("producto", producto);
            return "Editar_Producto"; 
        }
    }

    private String validarStock(Producto producto) {
        int min = producto.getStockMinimo();
        int max = producto.getStockMaximo();
        int stock = producto.getCantidadStock();

        if (min < 0 || max < 0 || stock < 0) {
            return " Los valores de stock no pueden ser negativos.";
        }
        if (min > max) {
            return " El stock mínimo (" + min + ") no puede ser mayor que el stock máximo (" + max + ").";
        }
        if (stock < min) {
            return " El stock actual (" + stock + ") no puede ser menor que el stock mínimo (" + min + ").";
        }
        if (stock > max) {
            return " El stock actual (" + stock + ") no puede superar el stock máximo (" + max + ").";
        }
        return null; 
    }
}
