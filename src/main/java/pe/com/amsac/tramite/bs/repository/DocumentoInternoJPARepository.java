package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.DocumentoInterno;

public interface DocumentoInternoJPARepository extends JpaRepository<DocumentoInterno, String> {
}
