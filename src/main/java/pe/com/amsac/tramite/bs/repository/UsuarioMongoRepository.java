package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.Usuario;

import java.util.List;

public interface UsuarioMongoRepository extends MongoRepository<Usuario, String> {

    List<Usuario> findByEmail(String email);

    List<Usuario> findByUsername(String username);
}
