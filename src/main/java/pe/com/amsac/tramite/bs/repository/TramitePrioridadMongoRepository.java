package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;

import java.util.List;

public interface TramitePrioridadMongoRepository extends MongoRepository<TramitePrioridad, String> {

    List<TramitePrioridad> findByEstado(String estado);
}
