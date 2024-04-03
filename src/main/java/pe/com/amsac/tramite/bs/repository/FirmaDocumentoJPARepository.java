package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.FirmaDocumento;

public interface FirmaDocumentoJPARepository extends JpaRepository<FirmaDocumento, String> {
}
