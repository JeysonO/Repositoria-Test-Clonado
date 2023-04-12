package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Tramite;

public interface TramiteMongoRepository extends MongoRepository<Tramite, String> {
}
