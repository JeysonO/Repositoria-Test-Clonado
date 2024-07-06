package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.Configuracion;
import pe.com.amsac.tramite.bs.domain.TramiteEnvioPide;

public interface TramiteEnvioPideJPARepository extends JpaRepository<TramiteEnvioPide, String> {

    //@Query(value = "select COUNT(*) from TramiteEnvioPide e left join fetch e.tramite t where t.id=?1")
    @Query(value = "select COUNT(*)+1 from tramite_envio_pide e where e.id_tramite=?1", nativeQuery = true)
    Long obtenerSecuencia(String tramiteId);

}
