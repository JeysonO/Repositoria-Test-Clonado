package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;

public interface DocumentoAdjuntoMongoRepository extends MongoRepository<DocumentoAdjunto, String> {

}
