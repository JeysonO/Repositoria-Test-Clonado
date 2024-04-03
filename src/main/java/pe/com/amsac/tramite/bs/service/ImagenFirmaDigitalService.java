package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;
import pe.com.amsac.tramite.bs.repository.ImagenFirmaDigitalJPARepository;

import java.util.List;

@Service
public class ImagenFirmaDigitalService {

	@Autowired
	private ImagenFirmaDigitalJPARepository imagenFirmaDigitalJPARepository;

	public ImagenFirmaDigital obtenerImagenFirmaDigitalById(String imagenFirmaDigitalId) throws Exception {

		return imagenFirmaDigitalJPARepository.findById(imagenFirmaDigitalId).get();

	}

	public List<ImagenFirmaDigital> obtenerImagenFirmaDigitalActivos() throws Exception {

		return imagenFirmaDigitalJPARepository.findByEstado("A");

	}


}
