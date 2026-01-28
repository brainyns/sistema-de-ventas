package sistemaventa.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCompra")
    private Integer idCompra;

    @Column(name = "Fecha")
    private Date fecha;

    @Column(name = "Cantidad_Comprada")
    private Integer cantidadComprada;

    @Column(name = "Precio_Unitario_Compra")
    private Double precioUnitarioCompra;
    
    // Relación ManyToOne con Producto
    @ManyToOne
    @JoinColumn(name = "Producto_id")
    private Producto producto;

    // Relación ManyToOne con Proveedor
    @ManyToOne
    @JoinColumn(name = "Proveedor_id")
    private Proveedor proveedor;
    
    // Relación ManyToOne con Administrador
    @ManyToOne
    @JoinColumn(name = "idAdministrador")
    private Administrador administrador;

    // Constructores, Getters y Setters...
    public Compra() {
    }
    
    // ... (Añadir constructor con campos si lo necesitas)

    // Getters y Setters (solo algunos ejemplos, debes crear todos)
    public Integer getIdCompra() {
        return idCompra;
    }
    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getCantidadComprada() {
        return cantidadComprada;
    }
    public void setCantidadComprada(Integer cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public Double getPrecioUnitarioCompra() {
        return precioUnitarioCompra;
    }
    public void setPrecioUnitarioCompra(Double precioUnitarioCompra) {
        this.precioUnitarioCompra = precioUnitarioCompra;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Administrador getAdministrador() {
        return administrador;
    }
    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }
}