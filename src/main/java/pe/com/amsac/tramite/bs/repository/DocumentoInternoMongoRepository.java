package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoInterno;

public interface DocumentoInternoMongoRepository extends MongoRepository<DocumentoInterno, String> {
}
