package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.Alerta;

public interface AlertaJPARepository extends JpaRepository<Alerta, String> {

}
