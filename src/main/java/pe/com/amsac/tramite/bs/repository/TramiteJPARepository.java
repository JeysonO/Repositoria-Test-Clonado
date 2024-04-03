package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.api.util.BitallJPARepository;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

public interface TramiteJPARepository extends BitallJPARepository<Tramite, String>, CustomTramiteJPARepository {

    @Query("SELECT t FROM Tramite t order by t.numeroTramite desc")
    Tramite obtenerUltimoRegistroMaxNumeroTramite(Pageable pageable);
}
