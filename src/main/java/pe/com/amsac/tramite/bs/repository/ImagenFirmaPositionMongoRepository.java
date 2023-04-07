package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaPosition;

import java.util.List;

public interface ImagenFirmaPositionMongoRepository extends MongoRepository<ImagenFirmaPosition, String> {

    public List<ImagenFirmaPosition> findByEstado(String estado);
    public List<ImagenFirmaPosition> findByOrientacionAndEstado(String orientacion, String estado);
}
