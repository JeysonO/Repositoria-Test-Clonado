package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;

import java.util.List;

public interface TramitePrioridadJPARepository extends JpaRepository<TramitePrioridad, String> {

    List<TramitePrioridad> findByEstado(String estado);
}
