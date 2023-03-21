package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;

import java.util.List;

public interface UsuarioFirmaMongoRepository extends MongoRepository<UsuarioFirma, String> {

    List<UsuarioFirma> findByUsuario(String usuario);
}
