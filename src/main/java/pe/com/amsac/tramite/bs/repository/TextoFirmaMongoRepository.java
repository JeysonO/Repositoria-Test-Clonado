package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.domain.TextoFirma;

import java.util.List;

public interface TextoFirmaMongoRepository extends MongoRepository<TextoFirma, String> {

    @Query(value="{ 'estado' : 'A' }")
    List<TextoFirma> obtenerTextoFirmaHabilitado();

}
