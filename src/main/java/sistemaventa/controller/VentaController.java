package sistemaventa.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpSession;
import sistemaventa.model.Administrador;
import sistemaventa.model.Cliente;
import sistemaventa.model.DetalleVenta;
import sistemaventa.model.Producto;
import sistemaventa.model.Venta;
import sistemaventa.service.ClienteService;
import sistemaventa.service.ProductoService;
import sistemaventa.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProductoService productoService;

    private static final double IVA = 0.19; // 19% IVA

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    // Muestra el formulario
    @GetMapping("/nueva")
    public String mostrarFormularioVenta(Model model, HttpSession session,
                                         @RequestParam(value = "mensaje", required = false) String mensaje,
                                         @RequestParam(value = "error", required = false) String error) {

        try {
            // Verificar sesión del admin
            Administrador adminLogueado = (Administrador) session.getAttribute("adminLogueado");
            if (adminLogueado == null) {
                System.out.println("ERROR: No hay administrador logueado");
                return "redirect:/admin/login";
            }
            System.out.println("Admin logueado: " + adminLogueado.getIdAdministrador());

            model.addAttribute("venta", new Venta());
            model.addAttribute("admin", adminLogueado);
            model.addAttribute("mensaje", mensaje);
            model.addAttribute("error", error);

            // Obtener productos con manejo de errores
            List<Producto> productos;
            try {
                productos = productoService.obtenerTodosLosProductos();
                if (productos == null) {
                    productos = new ArrayList<>();
                }
                System.out.println("Productos obtenidos: " + productos.size());
            } catch (Exception e) {
                logger.error("ERROR al obtener productos: {}", e.getMessage(), e);
                productos = new ArrayList<>();
            }
            model.addAttribute("productos", productos);

            // Obtener categorías únicas con manejo de errores
            List<String> tipos = new ArrayList<>();
            try {
                tipos = productos.stream()
                    .map(Producto::getTipo)
                    .filter(tipo -> tipo != null && !tipo.isEmpty())
                    .distinct()
                    .toList();
                System.out.println("Tipos obtenidos: " + tipos.size());
            } catch (Exception e) {
                logger.error("ERROR al obtener tipos: {}", e.getMessage(), e);
            }
            model.addAttribute("tipos", tipos);

            return "registrar_venta";

        } catch (Exception e) {
            logger.error("ERROR GENERAL en mostrarFormularioVenta: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "error"; // Página de error genérica
        }
    }

    // Buscar cliente por ID - AJAX
    @GetMapping("/buscar-cliente")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarCliente(@RequestParam("id") Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Cliente cliente = clienteService.buscarClientePorId(id);
            
            if (cliente != null) {
                response.put("success", true);
                response.put("nombre", cliente.getNombre() + " " + cliente.getApellido());
                response.put("telefono", cliente.getTelefono() != null ? cliente.getTelefono() : "N/A");
                response.put("email", cliente.getEmail() != null ? cliente.getEmail() : "N/A");
            } else {
                response.put("success", false);
                response.put("mensaje", "Cliente no encontrado");
            }
        } catch (Exception e) {
            logger.error("ERROR en buscarCliente: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("mensaje", "Error al buscar cliente: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // Buscar producto por ID - AJAX
    @GetMapping("/buscar-producto")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarProducto(@RequestParam("id") Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Producto producto = productoService.obtenerProductoPorId(id);
            
            if (producto != null) {
                double precioConIva = producto.getPrecio() * (1 + IVA);
                
                response.put("success", true);
                response.put("id", producto.getId());
                response.put("nombre", producto.getNombre());
                response.put("precio", producto.getPrecio());
                response.put("precioConIva", precioConIva);
                response.put("stock", producto.getCantidadStock());
                response.put("tipo", producto.getTipo() != null ? producto.getTipo() : "N/A");
                response.put("modelo", producto.getModelo() != null ? producto.getModelo() : "N/A");
            } else {
                response.put("success", false);
                response.put("mensaje", "Producto no encontrado");
            }
        } catch (Exception e) {
            logger.error("ERROR en buscarProducto: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("mensaje", "Error al buscar producto: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    // Obtener productos por tipo - AJAX
    @GetMapping("/productos-por-tipo")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> productosPorTipo(@RequestParam("tipo") String tipo) {
        List<Map<String, Object>> productosResponse = new ArrayList<>();
        
        try {
            List<Producto> productos = productoService.obtenerTodosLosProductos();
            if (productos != null) {
                productos = productos.stream()
                    .filter(p -> tipo.equals(p.getTipo()))
                    .toList();
                
                for (Producto p : productos) {
                    Map<String, Object> prod = new HashMap<>();
                    prod.put("id", p.getId());
                    prod.put("nombre", p.getNombre());
                    prod.put("precio", p.getPrecio());
                    prod.put("precioConIva", p.getPrecio() * (1 + IVA));
                    prod.put("stock", p.getCantidadStock());
                    prod.put("modelo", p.getModelo() != null ? p.getModelo() : "N/A");
                    productosResponse.add(prod);
                }
            }
        } catch (Exception e) {
            logger.error("ERROR en productosPorTipo: {}", e.getMessage(), e);
        }
        
        return ResponseEntity.ok(productosResponse);
    }

    // Guardar la venta con múltiples productos
    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam("clienteId") Integer clienteId,
            @RequestParam("productosIds") String productosIds,
            @RequestParam("cantidades") String cantidades,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("totalFinal") double totalFinal,
            @RequestParam(value = "montoRecibido", required = false) Double montoRecibido,
            HttpSession session) {

        try {
            Administrador admin = (Administrador) session.getAttribute("adminLogueado");
            if (admin == null) {
                return "redirect:/admin/login";
            }

            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            if (cliente == null) {
                return "redirect:/ventas/nueva?error=Cliente no encontrado";
            }

            // Parsear IDs y cantidades
            String[] idsArray = productosIds.split(",");
            String[] cantArray = cantidades.split(",");

            if (idsArray.length != cantArray.length || idsArray.length == 0) {
                return "redirect:/ventas/nueva?error=Datos de productos inválidos";
            }

            // Crear venta
            Venta venta = new Venta(
                    new Date(),
                    metodoPago,
                    totalFinal,
                    cliente.getIdentificacion(),
                    admin.getIdAdministrador()
            );

            // Calcular vuelto en sesión solo si es Efectivo (no guardar en BD)
            Double vuelto = 0.0;
            if ("Efectivo".equalsIgnoreCase(metodoPago)) {
                // Pago en efectivo: montoRecibido es obligatorio y debe cubrir el total
                if (montoRecibido == null) {
                    return "redirect:/ventas/nueva?error=Debe ingresar el monto recibido para efectivo";
                }
                if (montoRecibido < totalFinal) {
                    return "redirect:/ventas/nueva?error=El monto pagado es inferior al total";
                }
                vuelto = Math.round((montoRecibido - totalFinal) * 100.0) / 100.0;
            }
            // NO guardar vuelto en la entidad: solo en sesión

            // Agregar detalles y actualizar stock
            for (int i = 0; i < idsArray.length; i++) {
                int prodId = Integer.parseInt(idsArray[i]);
                int cantidad = Integer.parseInt(cantArray[i]);

                Producto producto = productoService.obtenerProductoPorId(prodId);
                
                if (producto == null) {
                    return "redirect:/ventas/nueva?error=Producto ID " + prodId + " no encontrado";
                }

                if (producto.getCantidadStock() < cantidad) {
                    return "redirect:/ventas/nueva?error=Stock insuficiente para " + producto.getNombre();
                }

                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                venta.agregarDetalle(detalle);

                // Actualizar stock
                producto.setCantidadStock(producto.getCantidadStock() - cantidad);
                productoService.guardarProducto(producto);
            }

            ventaService.guardarVenta(venta);

            // Guardar ID de venta y vuelto en sesión para generar PDF
            session.setAttribute("ultimaVentaId", venta.getIdVentas());
            session.setAttribute("ultimaVentaVuelto", vuelto);

            return "redirect:/ventas/nueva?mensaje=Venta registrada exitosamente. Total: $" + String.format("%.2f", totalFinal);

        } catch (Exception e) {
            logger.error("ERROR en guardarVenta: {}", e.getMessage(), e);
            return "redirect:/ventas/nueva?error=Error al guardar: " + e.getMessage();
        }
    }

    // Obtener TODOS los productos con imágenes para el modal
@GetMapping("/listar-productos")
@ResponseBody
public ResponseEntity<List<Map<String, Object>>> listarTodosProductos() {
    List<Map<String, Object>> productosResponse = new ArrayList<>();
    
    try {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        if (productos != null) {
            for (Producto p : productos) {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id", p.getId());
                prod.put("nombre", p.getNombre());
                prod.put("tipo", p.getTipo() != null ? p.getTipo() : "N/A");
                prod.put("modelo", p.getModelo() != null ? p.getModelo() : "N/A");
                prod.put("precio", p.getPrecio());
                prod.put("precioConIva", p.getPrecio() * (1 + IVA));
                prod.put("stock", p.getCantidadStock());
                
                // Convertir imagen a Base64 si existe
                if (p.getImagen() != null && p.getImagen().length > 0) {
                    String base64Image = java.util.Base64.getEncoder().encodeToString(p.getImagen());
                    prod.put("imagen", "data:image/jpeg;base64," + base64Image);
                } else {
                    prod.put("imagen", null);
                }
                
                productosResponse.add(prod);
            }
        }
            } catch (Exception e) {
                logger.error("ERROR en listarTodosProductos: {}", e.getMessage(), e);
            }
    
    return ResponseEntity.ok(productosResponse);
}

    // Generar PDF de la última venta
    @GetMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarPDF(HttpSession session) {
        try {
            Integer ventaId = (Integer) session.getAttribute("ultimaVentaId");
            
            if (ventaId == null) {
                System.err.println("ERROR: No hay ID de venta en sesión");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Venta venta = ventaService.obtenerTodas().stream()
                .filter(v -> v.getIdVentas() == ventaId)
                .findFirst()
                .orElse(null);

            if (venta == null) {
                System.err.println("ERROR: Venta no encontrada con ID: " + ventaId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Cliente cliente = clienteService.buscarClientePorId(venta.getClienteIdentificacion());
            
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, baos);

                document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("FACTURA DE VENTA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph(" "));

            // Información de la venta
            document.add(new Paragraph("Factura No: " + venta.getIdVentas()));
            document.add(new Paragraph("Fecha: " + venta.getFecha()));
            document.add(new Paragraph("Cliente: " + cliente.getNombre() + " " + cliente.getApellido()));
            document.add(new Paragraph("ID Cliente: " + cliente.getIdentificacion()));
            document.add(new Paragraph("Metodo de Pago: " + venta.getMetodoPago()));
            
            document.add(new Paragraph(" "));

            // Tabla de productos
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 2, 2});

            // Headers
            addTableHeader(table, "Producto");
            addTableHeader(table, "Cantidad");
            addTableHeader(table, "Precio Unit.");
            addTableHeader(table, "IVA 19%");
            addTableHeader(table, "Subtotal");

            // Datos - Formato colombiano
            java.text.NumberFormat currencyFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-CO"));
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto prod = detalle.getProducto();
                double precioUnit = prod.getPrecio();
                double precioConIva = precioUnit * (1 + IVA);
                double subtotalLinea = precioConIva * detalle.getCantidad();

                table.addCell(prod.getNombre());
                table.addCell(String.valueOf(detalle.getCantidad()));
                table.addCell(currencyFmt.format(precioUnit));
                table.addCell(currencyFmt.format(precioUnit * IVA));
                table.addCell(currencyFmt.format(subtotalLinea));
            }

            document.add(table);
            
            document.add(new Paragraph(" "));
            
            // Total
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            java.text.NumberFormat currencyFmtTotal = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-CO"));
            Paragraph total = new Paragraph("TOTAL A PAGAR: " + currencyFmtTotal.format(venta.getTotal()), boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // Vuelto: obtener del atributo de sesión (ultimaVentaVuelto)
            Object sessionVuelto = session.getAttribute("ultimaVentaVuelto");
            Double vueltoToShow = null;
            if (sessionVuelto instanceof Double) {
                vueltoToShow = (Double) sessionVuelto;
            }

            // Mostrar la línea de Vuelto solo si el método de pago fue Efectivo
            if (venta.getMetodoPago() != null && "Efectivo".equalsIgnoreCase(venta.getMetodoPago())) {
                if (vueltoToShow == null) {
                    vueltoToShow = 0.0;
                }
                Paragraph vueltoParrafo = new Paragraph("Vuelto: " + currencyFmtTotal.format(vueltoToShow), boldFont);
                vueltoParrafo.setAlignment(Element.ALIGN_RIGHT);
                document.add(vueltoParrafo);
            }

                document.close();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "Factura_" + venta.getIdVentas() + ".pdf");

                return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            logger.error("ERROR en generarPDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    

    private void addTableHeader(PdfPTable table, String headerTitle) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell header = new PdfPCell(new Phrase(headerTitle, headerFont));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setBackgroundColor(new Color(200, 200, 200));
        table.addCell(header);
    }
}