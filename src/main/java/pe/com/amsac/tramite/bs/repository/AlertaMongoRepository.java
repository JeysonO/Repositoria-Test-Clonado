package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Alerta;

public interface AlertaMongoRepository extends MongoRepository<Alerta, String> {

}
