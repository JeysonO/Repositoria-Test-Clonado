package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;

public interface TramitePrioridadMongoRepository extends MongoRepository<TramitePrioridad, String> {

}
