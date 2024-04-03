package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.Persona;

public interface PersonaJPARepository extends JpaRepository<Persona, String> {

}
