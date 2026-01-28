package sistemaventa.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sistemaventa.model.Producto;
@Repository
// ⭐ CAMBIO AQUÍ: Reemplazar 'Long' por 'Integer'
public interface ProductoRepository extends JpaRepository<Producto, Integer> { 
}