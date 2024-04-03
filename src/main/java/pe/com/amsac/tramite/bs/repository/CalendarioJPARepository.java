package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.Calendario;

import java.util.List;

public interface CalendarioJPARepository extends JpaRepository<Calendario, String> {

    List<Calendario> findByFechaNumber(Integer fechaNumer);

}
