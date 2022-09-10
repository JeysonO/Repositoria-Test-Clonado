package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.FormaRecepcion;

public interface FormaRecepcionMongoRepository extends MongoRepository<FormaRecepcion, String> {

}
