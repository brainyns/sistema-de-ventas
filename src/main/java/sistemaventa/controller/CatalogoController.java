package sistemaventa.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import sistemaventa.model.Producto;
import sistemaventa.repository.ProductoRepository;
import java.util.Base64;
import java.util.stream.Collectors;

@Controller
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/")
    public String mostrarCatalogo(Model model) {
        List<Producto> productos = productoRepository.findAll();

        // Construir lista de tipos con conteo (mantener orden de aparición)
        Map<String, Integer> tiposConCuenta = new LinkedHashMap<>();
        for (Producto p : productos) {
            String tipo = (p.getTipo() != null && !p.getTipo().isEmpty()) ? p.getTipo() : "Sin clasificar";
            tiposConCuenta.put(tipo, tiposConCuenta.getOrDefault(tipo, 0) + 1);
        }

        model.addAttribute("tiposConCuenta", tiposConCuenta);
        return "catalogo"; // Vista catalogo.html (landing de tipos)
    }

    @GetMapping("/imagen/{id}")
    @ResponseBody
    public byte[] obtenerImagen(@PathVariable("id") int id) {
        return productoRepository.findById(id)
                .map(Producto::getImagen)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Nuevo endpoint: mostrar productos por tipo usando path variable (evita problemas con espacios/encoding)
    @GetMapping("/catalogo/{tipo}")
    public String mostrarCatalogoPorTipo(@PathVariable("tipo") String tipo, Model model) {
        List<Producto> productos = productoRepository.findAll().stream()
                .filter(p -> p.getTipo() != null && p.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());

        List<java.util.Map<String,Object>> productosConImagen = productos.stream().map(prod -> {
            java.util.Map<String,Object> p = new java.util.HashMap<>();
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

        model.addAttribute("productos", productosConImagen);
        model.addAttribute("tipoSeleccionado", tipo);
        return "catalogo_lista";
    }
}
