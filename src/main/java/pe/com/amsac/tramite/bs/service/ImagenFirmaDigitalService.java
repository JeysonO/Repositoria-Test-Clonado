package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.ImagenFirmaDigital;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.repository.ImagenFirmaDigitalMongoRepository;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaMongoRepository;

import java.util.List;

@Service
public class ImagenFirmaDigitalService {

	@Autowired
	private ImagenFirmaDigitalMongoRepository imagenFirmaDigitalMongoRepository;

	public ImagenFirmaDigital obtenerImagenFirmaDigitalById(String imagenFirmaDigitalId) throws Exception {

		return imagenFirmaDigitalMongoRepository.findById(imagenFirmaDigitalId).get();

	}

	public List<ImagenFirmaDigital> obtenerImagenFirmaDigitalActivos() throws Exception {

		return imagenFirmaDigitalMongoRepository.findByEstado("A");

	}


}
