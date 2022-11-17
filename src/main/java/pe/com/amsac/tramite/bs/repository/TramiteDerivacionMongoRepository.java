package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

import java.util.List;

public interface TramiteDerivacionMongoRepository extends MongoRepository<TramiteDerivacion, String> {
    @Query(value="{ 'tramite.id' : ?0 }")
    List<TramiteDerivacion> findByTramiteId(String tramiteId);
}
