package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.api.util.BitallJPARepository;
import pe.com.amsac.tramite.api.util.CustomJPARepository;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.domain.Usuario;

import java.util.List;

public interface TramiteDerivacionJPARepository extends BitallJPARepository<TramiteDerivacion, String>, CustomTramiteDerivacionJPARepository {
    //@Query(value="{ 'tramite.id' : ?0 }")
    @Query(value = "select td.* from tramite_derivacion td \n" +
            "inner join  tramite t on t.id_tramite = td.id_tramite \n" +
            "where t.id_tramite = ?1 \n",
            nativeQuery = true )
    List<TramiteDerivacion> obtenerTramiteDerivacionByTramiteId(String tramiteId);
}
