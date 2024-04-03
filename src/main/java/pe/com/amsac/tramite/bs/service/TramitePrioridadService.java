package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.TramitePrioridadBodyRequest;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;
import pe.com.amsac.tramite.bs.repository.TramitePrioridadJPARepository;

import java.util.List;

@Service
public class TramitePrioridadService {

	@Autowired
	private TramitePrioridadJPARepository tramitePrioridadJPARepository;

	@Autowired
	private Mapper mapper;

	public TramitePrioridad registrarTramitePrioridad(TramitePrioridadBodyRequest tramitePrioridadBodyRequest) throws Exception {

		TramitePrioridad tramitePrioridad = mapper.map(tramitePrioridadBodyRequest,TramitePrioridad.class);
		tramitePrioridad.setEstado("A");
		tramitePrioridadJPARepository.save(tramitePrioridad);
		return tramitePrioridad;

	}

	public List<TramitePrioridad> findByAllTramitePrioridad() throws Exception{
		return tramitePrioridadJPARepository.findByEstado("A");
	}
	
}
