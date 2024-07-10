package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoTramite;

import java.util.List;

public interface TipoDocumentoJPARepository extends JpaRepository<TipoDocumentoTramite, String> {

    List<TipoDocumentoTramite> findByEstado(String estado);

    @Query(value = "select td.* \n" +
            "from tipo_documento td \n" +
            "where td.tipo_ambito in (?1) \n" +
            "and td.estado='A'",
            nativeQuery = true )
    List<TipoDocumentoTramite> obtenerTipoDocumentoByAmbito(List<String> ambitos);

}
