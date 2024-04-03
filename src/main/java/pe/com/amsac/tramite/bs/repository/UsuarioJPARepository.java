package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.Usuario;

import java.util.List;

public interface UsuarioJPARepository extends JpaRepository<Usuario, String> {

    List<Usuario> findByEmail(String email);

    List<Usuario> findByUsuario(String usuario);
}
