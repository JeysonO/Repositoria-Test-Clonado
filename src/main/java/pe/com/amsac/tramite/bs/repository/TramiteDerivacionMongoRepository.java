package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

public interface TramiteDerivacionMongoRepository extends MongoRepository<TramiteDerivacion, String> {

}
