package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.FormaRecepcion;

import java.util.List;

public interface FormaRecepcionJPARepository extends JpaRepository<FormaRecepcion, String> {

    List<FormaRecepcion> findByEstado(String estado);

    List<FormaRecepcion> findByFormaRecepcion(String formaRecepcion);

}
