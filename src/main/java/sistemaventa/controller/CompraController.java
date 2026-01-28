package sistemaventa.controller;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

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

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpSession;
import sistemaventa.model.Administrador;
import sistemaventa.model.Compra;
import sistemaventa.model.Producto;
import sistemaventa.model.Proveedor;
import sistemaventa.service.CompraService;
import sistemaventa.service.ProductoService;
import sistemaventa.service.ProveedorService;

@Controller
@RequestMapping("/compras")
public class CompraController {

    private static final Logger logger = LoggerFactory.getLogger(CompraController.class);

    @Autowired private CompraService compraService;
    @Autowired private ProductoService productoService;
    @Autowired private ProveedorService proveedorService;

    //  Mostrar formulario de compra
    @GetMapping("/nueva")
    public String mostrarFormularioCompra(Model model, HttpSession session) {
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("proveedores", proveedorService.listarProveedores());
        model.addAttribute("compra", new Compra());

        return "registrar_compra"; // Tu vista HTML
    }

    //  Procesar compra
    @PostMapping("/guardar")

public String guardarCompra(
        @RequestParam("productoId") Integer productoId,
        @RequestParam("proveedorId") Integer proveedorId,
        @RequestParam("cantidad") int cantidadComprada,
        @RequestParam("precioUnitario") double precioUnitario,
        HttpSession session) {

    Administrador admin = (Administrador) session.getAttribute("adminLogueado");
    if (admin == null) {
        return "redirect:/admin/login";
    }

    Producto producto = productoService.obtenerProductoPorId(productoId);
    Proveedor proveedor = proveedorService.buscarProveedorPorId(proveedorId);

    // Validaciones básicas
    if (producto == null || proveedor == null) {
        return "redirect:/compras/nueva?error=Producto o proveedor no encontrados";
    }
    if (cantidadComprada <= 0) {
        return "redirect:/compras/nueva?error=Cantidad inválida";
    }

    // Validación de stock máximo
    int stockTotalTrasCompra = producto.getCantidadStock() + cantidadComprada;
    if (stockTotalTrasCompra > producto.getStockMaximo()) {
        return "redirect:/compras/nueva?error=Excede stock máximo permitido (" 
               + producto.getStockMaximo() + ")";
    }

    Compra compra = new Compra();
    compra.setFecha(new Date());
    compra.setCantidadComprada(cantidadComprada);
    compra.setPrecioUnitarioCompra(precioUnitario);
    compra.setProducto(producto);
    compra.setProveedor(proveedor);
    compra.setAdministrador(admin);

    compra = compraService.registrarCompra(compra, producto, cantidadComprada);
    
    // Guardar ID de la compra en sesión para permitir descargar factura
    logger.info("Compra guardada con ID: " + compra.getIdCompra());
    session.setAttribute("ultimaCompraId", compra.getIdCompra());
    session.setAttribute("ultimaCompraTotal", compra.getCantidadComprada() * compra.getPrecioUnitarioCompra());

    return "redirect:/compras/nueva?exito=true";
}

    // Generar PDF de la última compra
    @GetMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarPdfCompra(HttpSession session) {
        try {
            Integer compraId = (Integer) session.getAttribute("ultimaCompraId");
            logger.info("Generando PDF para compraId: " + compraId);
            
            if (compraId == null || compraId <= 0) {
                logger.warn("compraId inválido: " + compraId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: No hay compra registrada".getBytes());
            }

            Compra compra = compraService.obtenerPorId(compraId);
            logger.info("Compra obtenida: " + (compra != null ? "OK" : "NULL"));
            
            if (compra == null) {
                logger.warn("No se encontró compra con ID: " + compraId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Compra no encontrada".getBytes());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, java.awt.Color.BLUE);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("FACTURA DE COMPRA", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Compra No: " + compra.getIdCompra(), headerFont));
            document.add(new Paragraph("Fecha: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(compra.getFecha())));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DATOS DEL PROVEEDOR", headerFont));
            document.add(new Paragraph("Proveedor: " + (compra.getProveedor() != null ? compra.getProveedor().getNombre() : "N/A")));
            document.add(new Paragraph("Contacto: " + (compra.getProveedor() != null ? compra.getProveedor().getTelefono() : "N/A")));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("DETALLES DE LA COMPRA", headerFont));
            document.add(new Paragraph("Producto: " + (compra.getProducto() != null ? compra.getProducto().getNombre() : "N/A")));
            document.add(new Paragraph("Tipo: " + (compra.getProducto() != null ? compra.getProducto().getTipo() : "N/A")));
            
            NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
            document.add(new Paragraph("Cantidad: " + compra.getCantidadComprada() + " unidades"));
            document.add(new Paragraph("Precio Unitario: " + currencyFmt.format(compra.getPrecioUnitarioCompra())));
            
            double total = compra.getCantidadComprada() * (compra.getPrecioUnitarioCompra() == null ? 0.0 : compra.getPrecioUnitarioCompra());
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TOTAL: " + currencyFmt.format(total), headerFont));

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "FacturaCompra_" + compra.getIdCompra() + ".pdf");
            
            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error generando PDF de compra", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error: " + e.getMessage()).getBytes());
        }
    }
}