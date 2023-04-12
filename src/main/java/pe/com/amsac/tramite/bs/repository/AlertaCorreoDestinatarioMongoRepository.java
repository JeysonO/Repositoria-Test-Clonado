package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.AlertaCorreoDestinatario;

public interface AlertaCorreoDestinatarioMongoRepository extends MongoRepository<AlertaCorreoDestinatario, String> {

}
