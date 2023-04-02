package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;

import java.util.List;

public interface UsuarioFirmaMongoRepository extends MongoRepository<UsuarioFirma, String> {

    @Query(value="{ 'usuario.id' : ?0,'estado' : 'A'  }")
    List<UsuarioFirma> obtenerUsuarioFirmaByUsuarioId(String usuarioId);

    List<UsuarioFirma> findByEstado(String estado);
}
