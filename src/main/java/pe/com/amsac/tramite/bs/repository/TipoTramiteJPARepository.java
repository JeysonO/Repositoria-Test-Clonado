package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.domain.TipoTramite;

import java.util.List;

public interface TipoTramiteJPARepository extends JpaRepository<TipoTramite, String> {

    List<TipoTramite> findByTipoTramite(String tipoTramite);

    List<TipoTramite> findByEstado(String estado);

}
