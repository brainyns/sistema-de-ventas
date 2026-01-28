package sistemaventa.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetalleVentaId implements Serializable {
    
    // Nombres que serán mapeados a las columnas mediante @MapsId en DetalleVenta
    private Integer productoCodigoProducto; 
    private Integer ventasIdVentas;         

    public DetalleVentaId() {}

    public DetalleVentaId(Integer productoCodigoProducto, Integer ventasIdVentas) {
        this.productoCodigoProducto = productoCodigoProducto;
        this.ventasIdVentas = ventasIdVentas;
    }
    
    // --- Getters y Setters ---
    public Integer getProductoCodigoProducto() {
        return productoCodigoProducto;
    }

    public void setProductoCodigoProducto(Integer productoCodigoProducto) {
        this.productoCodigoProducto = productoCodigoProducto;
    }

    public Integer getVentasIdVentas() {
        return ventasIdVentas;
    }

    public void setVentasIdVentas(Integer ventasIdVentas) {
        this.ventasIdVentas = ventasIdVentas;
    }
    
    // --- equals y hashCode (IMPRESCINDIBLE) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVentaId that = (DetalleVentaId) o;
        return Objects.equals(productoCodigoProducto, that.productoCodigoProducto) &&
               Objects.equals(ventasIdVentas, that.ventasIdVentas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoCodigoProducto, ventasIdVentas);
    }
}