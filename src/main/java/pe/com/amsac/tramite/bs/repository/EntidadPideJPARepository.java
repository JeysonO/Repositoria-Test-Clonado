package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.EntidadPide;

import java.util.List;

public interface EntidadPideJPARepository extends JpaRepository<EntidadPide, String> {

    List<EntidadPide> findByEstado(String estado);

}
