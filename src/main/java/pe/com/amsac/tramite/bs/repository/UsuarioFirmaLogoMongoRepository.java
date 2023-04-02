package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;

import java.util.List;

public interface UsuarioFirmaLogoMongoRepository extends MongoRepository<UsuarioFirmaLogo, String> {

    @Query(value="{ 'usuarioFirma.id' : ?0,'estado' : 'A'  }")
    List<UsuarioFirmaLogo> obtenerUsuarioFirmaLogoByUsuarioFirmaId(String usuarioFirmaId);

}
