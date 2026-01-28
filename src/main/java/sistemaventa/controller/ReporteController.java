package sistemaventa.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // <-- Importar el DTO
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory; // <-- Mejor excepción
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import sistemaventa.model.Producto;
import sistemaventa.model.ReporteInventarioDTO;
import sistemaventa.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class ReporteController {

    private final ProductoService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);
   
    public ReporteController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/reporte")
    public String verReporteInventario(Model model) {
      
        ReporteInventarioDTO reporte = productoService.generarReporteInventario();

        model.addAttribute("reporte", reporte);
        
     
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm"));
        model.addAttribute("fechaReporte", fecha);

        return "reporte_inventario";
    }

    @GetMapping("/reporte/pdf")
    public void descargarPdf(HttpServletResponse response) {

        ReporteInventarioDTO reporte = productoService.generarReporteInventario();
        List<Producto> productos = reporte.productos(); // Obtenemos la lista desde el DTO

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) { // try-with-resources
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

           
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph(" — Reporte de Inventario", headerFont);
            titulo.setAlignment(Element.ALIGN_LEFT);
            document.add(titulo);

            Font fechaFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph fecha = new Paragraph("Generado: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fechaFont);
            fecha.setSpacingAfter(12f);
            document.add(fecha);

           
            PdfPTable table = new PdfPTable(new float[]{1f, 3f, 2f, 1.5f, 1.2f, 1.2f, 1.2f, 1.2f});
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Tipo");
            table.addCell("Precio");
            table.addCell("Cantidad");
            table.addCell("StockMin");
            table.addCell("StockMax");
            table.addCell("Estado"); 

            for (Producto p : productos) {
                table.addCell(String.valueOf(p.getId()));
                table.addCell(p.getNombre());
                table.addCell(p.getTipo() == null ? "" : p.getTipo()); // Podrías usar el helper
                table.addCell(String.format("$%,.2f", p.getPrecio()));
                table.addCell(String.valueOf(p.getCantidadStock()));
                table.addCell(String.valueOf(p.getStockMinimo()));
                table.addCell(String.valueOf(p.getStockMaximo()));
                String estado = "Normal";
                if (p.getCantidadStock() < p.getStockMinimo()) estado = "Bajo";
                if (p.getCantidadStock() > p.getStockMaximo()) estado = "Alto";
                table.addCell(estado);
            }
            document.add(table);

        
            Paragraph resumen = new Paragraph("\nTotal productos: " + reporte.totalProductos()
                    + "   |   Valor total estimado: $" + String.format("%,.2f", reporte.valorTotal()));
            resumen.setSpacingBefore(12f);
            document.add(resumen);
            
            document.close();

            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_inventario.pdf");
            response.setContentLength(baos.size());

            // Usar try-with-resources para el OutputStream
            try (ServletOutputStream os = response.getOutputStream()) {
                baos.writeTo(os);
                os.flush();
            }

        } catch (IOException | DocumentException e) {
            logger.error("Error al generar PDF de inventario: {}", e.getMessage(), e);
            // Manejo de error
        }
    }
}