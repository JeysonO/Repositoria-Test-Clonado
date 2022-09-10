package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoExterno;

public interface DocumentoAdjuntoExternoMongoRepository extends MongoRepository<DocumentoAdjuntoExterno, String> {

}
