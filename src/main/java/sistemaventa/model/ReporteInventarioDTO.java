package sistemaventa.model;

import java.util.List;


public record ReporteInventarioDTO(
        List<Producto> productos,
        long totalProductos,
        long bajoStock,
        long stockNormal,
        long stockAlto,
        double valorTotal,
        long tiposDistintos,
        List<String> typeLabels,  
        List<Long> typeData,      
        List<String> stateLabels, 
        List<Long> stateData      
) {
}