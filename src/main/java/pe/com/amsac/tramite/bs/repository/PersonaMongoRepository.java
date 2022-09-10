package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Persona;

public interface PersonaMongoRepository extends MongoRepository<Persona, String> {

}
