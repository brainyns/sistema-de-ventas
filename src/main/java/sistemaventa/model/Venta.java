package sistemaventa.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "Ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVentas")
    private int idVentas;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Fecha", nullable = false)
    @NotNull(message = "La fecha no puede ser nula")
    private Date fecha;

    @Column(name = "Metodo_pago", nullable = false)
    @NotBlank(message = "Debe seleccionar un método de pago")
    private String metodoPago;

    @Column(name = "Total", nullable = false)
    @Positive(message = "El total debe ser mayor que cero")
    private double total;

    @Column(name = "Cliente_Identificacion", nullable = false)
    @Positive(message = "El cliente es obligatorio")
    private int clienteIdentificacion;

    @Column(name = "idAdministrador", nullable = false)
    @Positive(message = "El administrador es obligatorio")
    private int idAdministrador;

    // El vuelto NO se persiste en la base de datos. Se calcula en sesión.
    @Transient
    private Double vuelto; // Vuelto cuando se paga en efectivo

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {}

    public Venta(Date fecha, String metodoPago, double total, int clienteIdentificacion, int idAdministrador) {
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.total = total;
        this.clienteIdentificacion = clienteIdentificacion;
        this.idAdministrador = idAdministrador;
        this.vuelto = null;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    // Getters y Setters
    public int getIdVentas() { return idVentas; }
    public void setIdVentas(int idVentas) { this.idVentas = idVentas; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public int getClienteIdentificacion() { return clienteIdentificacion; }
    public void setClienteIdentificacion(int clienteIdentificacion) { this.clienteIdentificacion = clienteIdentificacion; }
    public int getIdAdministrador() { return idAdministrador; }
    public void setIdAdministrador(int idAdministrador) { this.idAdministrador = idAdministrador; }
    public Double getVuelto() { return vuelto; }
    public void setVuelto(Double vuelto) { this.vuelto = vuelto; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}
