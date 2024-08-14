package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.CategoriaEntidad;
import pe.com.amsac.tramite.bs.repository.CategoriaEntidadJPARepository;

import java.util.List;

@Service
public class CategoriaEntidadService {

	@Autowired
	private CategoriaEntidadJPARepository categoriaEntidadJPARepository;

	public List<CategoriaEntidad> obtenerCategoriaEntidadActivo() throws Exception {

		return categoriaEntidadJPARepository.findByEstado("A");
	}
		
	
}
