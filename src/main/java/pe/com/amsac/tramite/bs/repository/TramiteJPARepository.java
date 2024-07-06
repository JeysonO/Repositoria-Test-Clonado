package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.api.util.BitallJPARepository;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

import java.util.List;

public interface TramiteJPARepository extends BitallJPARepository<Tramite, String>, CustomTramiteJPARepository {

    @Query(value = "SELECT t FROM Tramite t")
    Page<Tramite> obtenerUltimoRegistroMaxNumeroTramite(Pageable pageable);

    List<Tramite> findByEstado(String estado);
}
