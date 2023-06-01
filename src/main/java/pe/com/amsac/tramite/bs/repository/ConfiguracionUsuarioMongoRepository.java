package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.com.amsac.tramite.bs.domain.ConfiguracionUsuario;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;

import java.util.List;

public interface ConfiguracionUsuarioMongoRepository extends MongoRepository<ConfiguracionUsuario, String> {

    @Query(value="{ 'usuario.id' : ?0,'estado' : 'A'  }")
    List<ConfiguracionUsuario> obtenerConfiguracionUsuarioByUsuarioId(String usuarioId);

}
