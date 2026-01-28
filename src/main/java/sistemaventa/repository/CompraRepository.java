package sistemaventa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sistemaventa.model.Compra;

public interface CompraRepository extends JpaRepository<Compra, Integer> {
}