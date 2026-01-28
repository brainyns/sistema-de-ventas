package sistemaventa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Codigo_producto")
    private Integer  id;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @Column(name = "Tipo")
    private String tipo;

    @Column(name = "Modelo")
    private String modelo;

    @Column(name = "Precio")
    private double precio;

    @Column(name = "Stock")
    private int cantidadStock;

    @Column(name = "Stock_Minimo")
    private int stockMinimo;

    @Column(name = "Stock_Maximo")
    private int stockMaximo;

        @Lob
    @Column(name = "Imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;

   
    public Producto() {}

    public Producto(Integer id, String nombre, String tipo, String modelo, double precio,
                    int cantidadStock, int stockMinimo, int stockMaximo, byte[] imagen) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.modelo = modelo;
        this.precio = precio;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.imagen =  imagen;
    }

  public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Integer  getId() { return id; }
    public void setId(Integer  id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(int cantidadStock) { this.cantidadStock = cantidadStock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(int stockMaximo) { this.stockMaximo = stockMaximo; }

    
    

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", modelo='" + modelo + '\'' +
                ", precio=" + precio +
                ", cantidadStock=" + cantidadStock +
                ", stockMinimo=" + stockMinimo +
                ", stockMaximo=" + stockMaximo +
                '}';
    }
}
