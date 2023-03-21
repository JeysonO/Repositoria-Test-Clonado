package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.FirmaDocumento;
import pe.com.amsac.tramite.bs.domain.Tramite;

public interface FirmaDocumentoRepository extends MongoRepository<FirmaDocumento, String> {
}
