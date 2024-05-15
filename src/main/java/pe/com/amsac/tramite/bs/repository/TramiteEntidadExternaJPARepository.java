package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.api.util.BitallJPARepository;
import pe.com.amsac.tramite.bs.domain.EntidadExterna;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.Usuario;

import java.util.Optional;

public interface TramiteEntidadExternaJPARepository extends JpaRepository<EntidadExterna, String> {

    @Modifying
    @Query(value = "delete from tramite_entidad_externa where tramite_id=?1 ",
            nativeQuery = true )
    void eliminarEntidadExternaByTramiteId(String tramiteId);

    Optional<EntidadExterna> findByTramiteId(String tramiteId);
}
