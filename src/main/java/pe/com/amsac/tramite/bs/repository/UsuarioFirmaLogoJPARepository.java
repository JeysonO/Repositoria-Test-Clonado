package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;

import java.util.List;

public interface UsuarioFirmaLogoJPARepository extends JpaRepository<UsuarioFirmaLogo, String> {

    //@Query(value="{ 'usuarioFirma.id' : ?0,'estado' : 'A'  }")
    @Query(value = "select ufl.* from usuario_firma_logo ufl \n" +
            "inner join  usuario_firma uf on uf.id_usuario_firma = ufl.id_usuario_firma \n" +
            "where uf.id_usuario_firma = ?1 \n" +
            "and ufl.estado = 'A'",
            nativeQuery = true )
    List<UsuarioFirmaLogo> obtenerUsuarioFirmaLogoByUsuarioFirmaId(String usuarioFirmaId);

}
