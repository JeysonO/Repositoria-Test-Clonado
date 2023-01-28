package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Calendario;
import pe.com.amsac.tramite.bs.domain.Configuracion;

import java.util.List;

public interface ConfiguracionMongoRepository extends MongoRepository<Configuracion, String> {

}
