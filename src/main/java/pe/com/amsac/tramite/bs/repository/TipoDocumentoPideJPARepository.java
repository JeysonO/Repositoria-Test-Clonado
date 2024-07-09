package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.TipoDocumento;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoPide;

import java.util.List;

public interface TipoDocumentoPideJPARepository extends JpaRepository<TipoDocumentoPide, String> {

    List<TipoDocumentoPide> findByTipoDocumentoPide(String tipoDocumentoPide);

}
