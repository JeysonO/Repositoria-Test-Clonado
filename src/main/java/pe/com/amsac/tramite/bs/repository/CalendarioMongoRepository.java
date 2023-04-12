package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.domain.Calendario;

import java.util.List;

public interface CalendarioMongoRepository extends MongoRepository<Calendario, String> {

    List<Calendario> findByFechaNumber(Integer fechaNumer);

}
