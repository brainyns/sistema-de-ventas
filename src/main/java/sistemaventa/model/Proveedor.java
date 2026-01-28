package sistemaventa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProveedor")
    private Integer idProveedor;

    private String nombre;
    private String telefono;
    private String direccion;

    // Constructores, Getters y Setters...
    public Proveedor() {
    }
    
    // ... (Añadir constructor con campos si lo necesitas)

    // Getters
    public Integer getIdProveedor() {
        return idProveedor;
    }
    public String getNombre() {
        return nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getDireccion() {
        return direccion;
    }

    // Setters
    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}