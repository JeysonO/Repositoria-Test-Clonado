package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.domain.TextoFirma;

import java.util.List;

public interface TextoFirmaJPARepository extends JpaRepository<TextoFirma, String> {

    //@Query(value="{ 'estado' : 'A' }")
    @Query(value = "select tf.* from texto_firma tf \n" +
            "where tf.estado = 'A'",
            nativeQuery = true )
    List<TextoFirma> obtenerTextoFirmaHabilitado();

}
