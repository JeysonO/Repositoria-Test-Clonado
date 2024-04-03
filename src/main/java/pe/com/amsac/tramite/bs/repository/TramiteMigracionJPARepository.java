package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.TramiteMigracion;

public interface TramiteMigracionJPARepository extends JpaRepository<TramiteMigracion, String> {
}
