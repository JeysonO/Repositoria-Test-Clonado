package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.FormaRecepcion;

import java.util.List;

public interface FormaRecepcionMongoRepository extends MongoRepository<FormaRecepcion, String> {

    List<FormaRecepcion> findByEstado(String estado);
}
