package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.TipoDocumento;

public interface TipoDocumentoMongoRepository extends MongoRepository<TipoDocumento, String> {

}
