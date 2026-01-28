package sistemaventa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DetalleVenta")
public class DetalleVenta {

    @EmbeddedId
    private DetalleVentaId id; // Clave compuesta

    // Relación con Venta: @MapsId usa la parte 'ventasIdVentas' de la clave compuesta
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ventasIdVentas") 
    @JoinColumn(name = "Ventas_idVentas")
    private Venta venta; 

    // Relación con Producto: @MapsId usa la parte 'productoCodigoProducto' de la clave compuesta
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoCodigoProducto") 
    @JoinColumn(name = "Productos_Codigo_producto")
    private Producto producto; 
    
    @Column(name = "Cantidad")
    private Integer cantidad;

    // --- Constructores ---
    public DetalleVenta() {
        this.id = new DetalleVentaId(); // CRÍTICO: Inicializar el ID.
    }
    
    // --- Getters y Setters ---
    public DetalleVentaId getId() {
        return id;
    }

    public void setId(DetalleVentaId id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
        // Si la venta ya tiene ID, lo seteamos en el ID compuesto
        if (venta != null) {
            this.id.setVentasIdVentas(venta.getIdVentas());
        }
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        // Si el producto ya tiene ID, lo seteamos en el ID compuesto
        if (producto != null) {
            this.id.setProductoCodigoProducto(producto.getId());
        }
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}