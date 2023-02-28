package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteMigracion;

public interface TramiteMigracionMongoRepository extends MongoRepository<TramiteMigracion, String> {
}
