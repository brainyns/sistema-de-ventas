package sistemaventa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Cliente")
public class Cliente {

    @Id
    @NotNull(message = "La identificación es obligatoria")
    @Min(value = 1, message = "La identificación debe ser un número positivo")
    @Column(name = "Identificacion")
    private Integer identificacion;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 100, message = "El apellido no debe superar los 100 caracteres")
    @Column(name = "Apellido", length = 100)
    private String apellido;

    @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos")
    @Column(name = "Telefono", length = 20)
    private String telefono;

    @Size(max = 150, message = "La dirección no debe superar los 150 caracteres")
    @Column(name = "Direccion", length = 150)
    private String direccion;

    @Email(message = "Debe ingresar un correo electrónico válido")
    @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
    @Column(name = "Email", length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "Administrador_idAdministrador", referencedColumnName = "idAdministrador")
    private Administrador administrador;


    public Cliente() {}

    public Cliente(Integer identificacion, String nombre, String apellido,
                   String telefono, String direccion, String email,
                   Administrador administrador) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.administrador = administrador;
    }

    
    public Integer getIdentificacion() { return identificacion; }
    public void setIdentificacion(Integer identificacion) { this.identificacion = identificacion; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
}
