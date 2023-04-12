package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoInterno;

public interface DocumentoAdjuntoInternoMongoRepository extends MongoRepository<DocumentoAdjuntoInterno, String> {

}
