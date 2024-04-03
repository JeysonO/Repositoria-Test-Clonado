package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.ConfiguracionUsuario;

import java.util.List;

public interface ConfiguracionUsuarioJPARepository extends JpaRepository<ConfiguracionUsuario, String> {

    //@Query(value="{ 'usuario.id' : ?0,'estado' : 'A'  }")
    @Query(value = "select cu.* from configuracion_usuario cu \n" +
            "inner join  usuario u on u.id_usuario = cu.id_usuario \n" +
            "where u.id_usuario = ?1 \n" +
            "and cu.estado = 'A'",
            nativeQuery = true )
    List<ConfiguracionUsuario> obtenerConfiguracionUsuarioByUsuarioId(String usuarioId);

}
