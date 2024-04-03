package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;

import java.util.List;

public interface UsuarioFirmaJPARepository extends JpaRepository<UsuarioFirma, String> {

    //@Query(value="{ 'usuario.id' : ?0,'estado' : 'A'  }")
    @Query(value = "select uf.* from usuario_firma uf \n" +
            "inner join  usuario u on u.id_usuario = uf.id_usuario \n" +
            "where u.id_usuario = ?1 \n" +
            "and uf.estado = 'A'",
            nativeQuery = true )
    List<UsuarioFirma> obtenerUsuarioFirmaByUsuarioId(String usuarioId);

    @Query(value = "select uf from UsuarioFirma uf \n" +
            "left join fetch  usuario u \n" +
            "where upper(u.nombre) like % ?1 % \n" +
            "and uf.estado = 'A'",
            nativeQuery = true )
    List<UsuarioFirma> obtenerUsuarioFirmaByNombreUsuario(String nombre, Pageable pageable);

    List<UsuarioFirma> findByEstado(String estado);
}
