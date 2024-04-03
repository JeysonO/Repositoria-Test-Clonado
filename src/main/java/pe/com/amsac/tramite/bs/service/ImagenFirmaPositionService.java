package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaPosition;
import pe.com.amsac.tramite.bs.repository.ImagenFirmaPositionJPARepository;

import java.util.List;

@Service
public class ImagenFirmaPositionService {

	@Autowired
	private ImagenFirmaPositionJPARepository imagenFirmaPositionJPARepository;

	public ImagenFirmaPosition obtenerImagenFirmaPositionById(String imagenFirmaPositionId) throws Exception {

		return imagenFirmaPositionJPARepository.findById(imagenFirmaPositionId).get();
	}

	public List<ImagenFirmaPosition> obtenerImagenFirmaPositionActivos() throws Exception {

		return imagenFirmaPositionJPARepository.findByEstado("A");

	}

	public List<ImagenFirmaPosition> obtenerImagenFirmaPositionActivosAndOrientacion(String orientacion) throws Exception {

		return imagenFirmaPositionJPARepository.findByOrientacionAndEstado(orientacion, "A");

	}

	public List<ImagenFirmaPosition> obtenerImagenFirmaPositionByPositionAndOrientacion(String position, String orientacion) throws Exception {

		return imagenFirmaPositionJPARepository.findByPositionAndOrientacionAndEstado(position, orientacion, "A");

	}

}
