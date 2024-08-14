package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.api.util.BitallJPARepository;
import pe.com.amsac.tramite.bs.domain.CategoriaEntidad;
import pe.com.amsac.tramite.bs.domain.TipoTramite;

import java.util.List;


public interface CategoriaEntidadJPARepository extends JpaRepository<CategoriaEntidad, String> {

    List<CategoriaEntidad> findByEstado(String estado);

}
