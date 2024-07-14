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

    @Query(value = "select ISNULL(MAX(nro_tramite_dependencia),0)+1 from dbo.tramite where id_dependencia_remitente=?1 and nro_tramite_dependencia=?2", nativeQuery = true)
    Long obtenerNumeroTramiteByDependencia(String dependenciaId, Integer anio);

    List<Tramite> findByEstado(String estado);
}
