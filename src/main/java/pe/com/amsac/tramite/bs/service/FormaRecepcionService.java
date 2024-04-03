package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.FormaRecepcionBodyRequest;
import pe.com.amsac.tramite.bs.domain.FormaRecepcion;
import pe.com.amsac.tramite.bs.repository.FormaRecepcionJPARepository;

import java.util.List;

@Service
public class FormaRecepcionService {

	@Autowired
	private FormaRecepcionJPARepository formaRecepcionJPARepository;

	@Autowired
	private Mapper mapper;

	public FormaRecepcion registrarFormaRecepcion(FormaRecepcionBodyRequest formaRecepcionBodyRequest) throws Exception {

		FormaRecepcion formaRecepcion = mapper.map(formaRecepcionBodyRequest,FormaRecepcion.class);
		formaRecepcion.setEstado("A");
		formaRecepcionJPARepository.save(formaRecepcion);
		return formaRecepcion;

	}

	public List<FormaRecepcion> findByAllFormaRecepcion() throws Exception{
		return formaRecepcionJPARepository.findByEstado("A");
	}

	public List<FormaRecepcion> findByFormaRecepcion(String formaRecepcion) throws Exception{
		return formaRecepcionJPARepository.findByFormaRecepcion(formaRecepcion);
	}
	
}
