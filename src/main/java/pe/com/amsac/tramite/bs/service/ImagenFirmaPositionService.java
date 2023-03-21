package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaPosition;
import pe.com.amsac.tramite.bs.repository.ImagenFirmaPositionMongoRepository;

import java.util.List;

@Service
public class ImagenFirmaPositionService {

	@Autowired
	private ImagenFirmaPositionMongoRepository imagenFirmaPositionMongoRepository;

	public ImagenFirmaPosition obtenerImagenFirmaPositionById(String imagenFirmaPositionId) throws Exception {

		return imagenFirmaPositionMongoRepository.findById(imagenFirmaPositionId).get();

	}

	public List<ImagenFirmaPosition> obtenerImagenFirmaPositionActivos() throws Exception {

		return imagenFirmaPositionMongoRepository.findByEstado("A");

	}


}
