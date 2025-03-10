package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaPosition;

import java.util.List;

public interface ImagenFirmaPositionJPARepository extends JpaRepository<ImagenFirmaPosition, String> {

    public List<ImagenFirmaPosition> findByEstado(String estado);
    public List<ImagenFirmaPosition> findByOrientacionAndEstado(String orientacion, String estado);
    public List<ImagenFirmaPosition> findByPositionAndOrientacionAndEstado(String position, String orientacion, String estado);

}
