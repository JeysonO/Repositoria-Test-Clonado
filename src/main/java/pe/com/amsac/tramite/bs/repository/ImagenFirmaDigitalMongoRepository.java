package pe.com.amsac.tramite.bs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.com.amsac.tramite.bs.domain.FirmaDocumento;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;

import java.util.List;

public interface ImagenFirmaDigitalMongoRepository extends MongoRepository<ImagenFirmaDigital, String> {

    public List<ImagenFirmaDigital> findByEstado(String estado);
}
