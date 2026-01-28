package sistemaventa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistemaventa.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> { 
    // Integer, porque idVentas es int
}
