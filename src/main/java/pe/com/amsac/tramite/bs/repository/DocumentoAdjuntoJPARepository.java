package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;

import java.util.Optional;

public interface DocumentoAdjuntoJPARepository extends JpaRepository<DocumentoAdjunto, String> {

    @Query(value = "select da from DocumentoAdjunto da left join fetch da.tramite t where da.id = ?1")
    Optional<DocumentoAdjunto> obtenerDocumentoAdjuntoById (String documentoAdjuntoId);

}
