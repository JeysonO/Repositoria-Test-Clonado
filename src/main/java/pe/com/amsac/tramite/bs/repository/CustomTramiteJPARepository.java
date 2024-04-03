package pe.com.amsac.tramite.bs.repository;

import org.springframework.stereotype.Repository;
import pe.com.amsac.tramite.api.util.CustomRepository;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;


@Repository
public interface CustomTramiteJPARepository extends CustomRepository<Tramite, String> {
	
}