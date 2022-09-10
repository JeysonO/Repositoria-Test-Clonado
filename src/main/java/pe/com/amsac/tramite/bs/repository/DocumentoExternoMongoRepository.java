package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoExterno;

public interface DocumentoExternoMongoRepository extends MongoRepository<DocumentoExterno, String> {

}
