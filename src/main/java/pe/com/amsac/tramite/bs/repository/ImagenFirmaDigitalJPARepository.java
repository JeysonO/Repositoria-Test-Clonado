package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;

import java.util.List;

public interface ImagenFirmaDigitalJPARepository extends JpaRepository<ImagenFirmaDigital, String> {

    public List<ImagenFirmaDigital> findByEstado(String estado);
}
