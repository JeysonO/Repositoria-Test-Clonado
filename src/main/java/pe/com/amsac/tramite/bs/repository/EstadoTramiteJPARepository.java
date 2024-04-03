package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.EstadoTramite;

public interface EstadoTramiteJPARepository extends JpaRepository<EstadoTramite, String> {

}
