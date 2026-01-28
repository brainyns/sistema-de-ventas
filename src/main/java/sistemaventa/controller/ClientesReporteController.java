package sistemaventa.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
import com.lowagie.text.pdf.PdfPTable;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import sistemaventa.model.Cliente;
import sistemaventa.model.Venta;
import sistemaventa.service.ClienteService;
import sistemaventa.service.VentaService;

@Controller
@RequestMapping("/clientes")
public class ClientesReporteController {

    private final VentaService ventaService;
    private final ClienteService clienteService;

    public ClientesReporteController(VentaService ventaService, ClienteService clienteService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientesReporteController.class);

    @GetMapping("/reporte")
    public String verReporteClientes(Model model) {
        List<Venta> ventas = ventaService.obtenerTodas();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        Map<Integer, Double> totals = new HashMap<>();
        Set<Integer> clientesHoyIds = new HashSet<>();

        for (Venta v : ventas) {
            if (v.getFecha() == null) continue;
            LocalDate fecha = v.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.equals(today)) {
                clientesHoyIds.add(v.getClienteIdentificacion());
                totals.put(v.getClienteIdentificacion(), totals.getOrDefault(v.getClienteIdentificacion(), 0.0) + v.getTotal());
            }
        }

        List<Map<String,Object>> clientesHoyList = new ArrayList<>();
        java.text.NumberFormat currencyFmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-CO"));
        for (Integer id : clientesHoyIds) {
            try {
                Cliente c = clienteService.buscarClientePorId(id);
                Map<String,Object> row = new HashMap<>();
                row.put("cliente", c);
                double rawTotal = Math.round(totals.getOrDefault(id,0.0)*100.0)/100.0;
                row.put("total", rawTotal);
                row.put("formattedTotal", currencyFmt.format(rawTotal));
                clientesHoyList.add(row);
            } catch (NoSuchElementException e) {
                // ignorar si no existe el cliente en la tabla
            }
        }

        Integer topId = totals.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        Cliente topCliente = null;
        Double topTotal = 0.0;
        if (topId != null) {
            try {
                topCliente = clienteService.buscarClientePorId(topId);
                topTotal = totals.getOrDefault(topId, 0.0);
            } catch (NoSuchElementException e) {
                // ignore
            }
        }

        model.addAttribute("clientesHoy", clientesHoyList);
        model.addAttribute("topCliente", topCliente);
        double roundedTop = Math.round(topTotal*100.0)/100.0;
        model.addAttribute("topTotal", roundedTop);
        model.addAttribute("topTotalFormatted", currencyFmt.format(roundedTop));
        model.addAttribute("fechaReporte", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return "reporte_clientes";
    }

    

    @GetMapping("/reporte/pdf")
    public void descargarPdf(HttpServletResponse response) {
        List<Venta> ventas = ventaService.obtenerTodas();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        Map<Integer, Double> totals = new HashMap<>();
        Set<Integer> clientesHoyIds = new HashSet<>();

        for (Venta v : ventas) {
            if (v.getFecha() == null) continue;
            LocalDate fecha = v.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.equals(today)) {
                clientesHoyIds.add(v.getClienteIdentificacion());
                totals.put(v.getClienteIdentificacion(), totals.getOrDefault(v.getClienteIdentificacion(), 0.0) + v.getTotal());
            }
        }

        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph titulo = new Paragraph("Reporte de Clientes - " + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), headerFont);
            titulo.setAlignment(Element.ALIGN_LEFT);
            document.add(titulo);

            Paragraph espacio = new Paragraph("\n");
            document.add(espacio);

            PdfPTable table = new PdfPTable(new float[]{2f,3f,3f,2f});
            table.setWidthPercentage(100);
            table.addCell("Identificación");
            table.addCell("Nombre");
            table.addCell("Apellido");
            table.addCell("Total Comprado Hoy");

            java.text.NumberFormat currencyFmtPdf = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-CO"));
            int limit = Math.min(200, sorted.size());
            for (int i=0;i<limit;i++){
                Map.Entry<Integer, Double> e = sorted.get(i);
                Integer id = e.getKey();
                Double total = Math.round(e.getValue()*100.0)/100.0;
                try {
                    Cliente c = clienteService.buscarClientePorId(id);
                    table.addCell(String.valueOf(id));
                    table.addCell(c.getNombre() == null ? "-" : c.getNombre());
                    table.addCell(c.getApellido() == null ? "-" : c.getApellido());
                    table.addCell(currencyFmtPdf.format(total));
                } catch (NoSuchElementException ex) {
                    // si no existe cliente, mostrar id y total
                    table.addCell(String.valueOf(id));
                    table.addCell("-");
                    table.addCell("-");
                    table.addCell(currencyFmtPdf.format(total));
                }
            }

            document.add(table);

            // Top comprador
            if (!sorted.isEmpty()) {
                Map.Entry<Integer, Double> top = sorted.get(0);
                try {
                    Cliente tc = clienteService.buscarClientePorId(top.getKey());
                    Paragraph topPar = new Paragraph("\nTop comprador del día: " + (tc.getNombre()==null?"-":tc.getNombre()) + " " + (tc.getApellido()==null?"":tc.getApellido()) + " - Total: " + currencyFmtPdf.format(Math.round(top.getValue()*100.0)/100.0));
                    topPar.setSpacingBefore(12f);
                    document.add(topPar);
                } catch (NoSuchElementException ex) {
                    Paragraph topPar = new Paragraph("\nTop comprador del día: ID " + top.getKey() + " - Total: " + currencyFmtPdf.format(Math.round(top.getValue()*100.0)/100.0));
                    topPar.setSpacingBefore(12f);
                    document.add(topPar);
                }
            }

            document.close();

            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_clientes_"+today+".pdf");
            response.setContentLength(baos.size());
            try (ServletOutputStream os = response.getOutputStream()) {
                baos.writeTo(os);
                os.flush();
            }

        } catch (IOException | DocumentException e) {
            logger.error("Error al generar PDF reporte clientes: {}", e.getMessage(), e);
        }
    }
}
