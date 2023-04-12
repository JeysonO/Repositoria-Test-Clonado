package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoConfiguracion;

public interface DocumentoConfiguracionMongoRepository extends MongoRepository<DocumentoConfiguracion, String> {

}
