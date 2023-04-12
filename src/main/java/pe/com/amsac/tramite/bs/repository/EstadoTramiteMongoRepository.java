package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.EstadoTramite;

public interface EstadoTramiteMongoRepository extends MongoRepository<EstadoTramite, String> {

}
