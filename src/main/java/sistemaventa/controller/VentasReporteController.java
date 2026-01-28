package sistemaventa.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import sistemaventa.model.DetalleVenta;
import sistemaventa.model.Producto;
import sistemaventa.model.Venta;
import sistemaventa.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentasReporteController {

    private final VentaService ventaService;
    private static final Logger logger = LoggerFactory.getLogger(VentasReporteController.class);

    public VentasReporteController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/reporte")
    public String verReporteVentas(Model model) {
        List<Venta> ventas = ventaService.obtenerTodas();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        double totalHoy = 0.0;
        int ventasHoy = 0;

        Map<String, Long> productCount = new HashMap<>();

        for (Venta v : ventas) {
            if (v.getFecha() == null) continue;
            LocalDate fecha = v.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.equals(today)) {
                totalHoy += v.getTotal();
                ventasHoy++;
            }

            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    Producto p = d.getProducto();
                    if (p == null) continue;
                    String name = p.getNombre() == null ? "Sin nombre" : p.getNombre();
                    long qty = d.getCantidad() == null ? 0L : d.getCantidad().longValue();
                    productCount.put(name, productCount.getOrDefault(name, 0L) + qty);
                }
            }
        }

        // Ordenar productos por cantidad vendida descendente
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(productCount.entrySet());
        sorted.sort((a,b) -> Long.compare(b.getValue(), a.getValue()));

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        List<Map<String, Object>> topList = new ArrayList<>();
        int limit = Math.min(10, sorted.size());
        for (int i=0;i<limit;i++) {
            Map.Entry<String,Long> e = sorted.get(i);
            labels.add(e.getKey());
            data.add(e.getValue());
            Map<String,Object> row = new HashMap<>();
            row.put("nombre", e.getKey());
            row.put("cantidad", e.getValue());
            topList.add(row);
        }

    String fechaReporte = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    // Formateo de números según locale Colombia (ej: $661.640,00)
    NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    model.addAttribute("fechaReporte", fechaReporte);
    model.addAttribute("totalHoy", Math.round(totalHoy * 100.0) / 100.0);
    model.addAttribute("formattedTotalHoy", currencyFmt.format(Math.round(totalHoy * 100.0) / 100.0));
        model.addAttribute("ventasHoy", ventasHoy);
        model.addAttribute("topProductos", topList);
        model.addAttribute("salesLabels", labels);
        model.addAttribute("salesData", data);

        return "reporte_ventas";
    }

    

    @GetMapping("/reporte/pdf")
    public void descargarPdf(HttpServletResponse response) {
        List<Venta> ventas = ventaService.obtenerTodas();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        double totalHoy = 0.0;
        Map<String, Long> productCount = new HashMap<>(); 

        for (Venta v : ventas) {
            if (v.getFecha() == null) continue;
            LocalDate fecha = v.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.equals(today)) {
                totalHoy += v.getTotal();
            }
            if (v.getDetalles() != null) {
                for (DetalleVenta d : v.getDetalles()) {
                    Producto p = d.getProducto();
                    if (p == null) continue;
                    String name = p.getNombre() == null ? "Sin nombre" : p.getNombre();
                    long qty = d.getCantidad() == null ? 0L : d.getCantidad().longValue();
                    productCount.put(name, productCount.getOrDefault(name, 0L) + qty);
                }
            }
        }

        List<Map.Entry<String, Long>> sorted = new ArrayList<>(productCount.entrySet());
        sorted.sort((a,b) -> Long.compare(b.getValue(), a.getValue()));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("Reporte de Ventas - " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), headerFont);
            titulo.setAlignment(Element.ALIGN_LEFT);
            document.add(titulo);

            // Formateo de números para PDF (Colombia)
            NumberFormat currencyFmtLocal = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
            NumberFormat intFmtLocal = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));

            Paragraph resumen = new Paragraph("Total recaudado hoy: " + currencyFmtLocal.format(Math.round(totalHoy * 100.0) / 100.0));
            resumen.setSpacingAfter(12f);
            document.add(resumen);

            PdfPTable table = new PdfPTable(new float[]{4f, 2f});
            table.setWidthPercentage(60);

            // Encabezados con color
            Font headerFontSmall = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            PdfPCell h1 = new PdfPCell(new com.lowagie.text.Phrase("Producto", headerFontSmall));
            h1.setBackgroundColor(new Color(54,162,235));
            h1.setPadding(6);
            table.addCell(h1);

            PdfPCell h2 = new PdfPCell(new com.lowagie.text.Phrase("Cantidad vendida", headerFontSmall));
            h2.setBackgroundColor(new Color(54,162,235));
            h2.setPadding(6);
            table.addCell(h2);

            int limit = Math.min(20, sorted.size());
            for (int i=0;i<limit;i++){
                Map.Entry<String,Long> e = sorted.get(i);
                table.addCell(e.getKey());
                table.addCell(intFmtLocal.format(e.getValue()));
            }
            document.add(table);

            document.close();

            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas_"+today+".pdf");
            response.setContentLength(baos.size());
            try (ServletOutputStream os = response.getOutputStream()) {
                baos.writeTo(os);
                os.flush();
            }

        } catch (IOException | DocumentException e) {
            logger.error("Error al generar PDF reporte ventas: {}", e.getMessage(), e);
        }
    }

}
