package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.EntidadExterna;
import pe.com.amsac.tramite.bs.domain.EntidadInterna;

import java.util.Optional;

public interface TramiteEntidadInternaJPARepository extends JpaRepository<EntidadInterna, String> {

    @Modifying
    @Query(value = "delete from tramite_entidad_interna where tramite_id=?1 ",
            nativeQuery = true )
    void eliminarEntidadInternaByTramiteId(String tramiteId);

    Optional<EntidadInterna> findByTramiteId(String tramiteId);
}
