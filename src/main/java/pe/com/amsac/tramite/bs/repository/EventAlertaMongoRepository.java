package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.EventAlerta;

public interface EventAlertaMongoRepository extends MongoRepository<EventAlerta, String> {

}
