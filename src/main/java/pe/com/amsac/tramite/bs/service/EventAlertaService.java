package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.EventAlertaBodyRequest;
import pe.com.amsac.tramite.bs.domain.EventAlerta;
import pe.com.amsac.tramite.bs.repository.EventAlertaMongoRepository;

@Service
public class EventAlertaService {

	@Autowired
	private EventAlertaMongoRepository eventAlertaMongoRepository;

	@Autowired
	private Mapper mapper;

	public EventAlerta registrarEventAlerta(EventAlertaBodyRequest eventAlertaBodyRequest) throws Exception {

		EventAlerta eventAlerta = mapper.map(eventAlertaBodyRequest,EventAlerta.class);
		eventAlerta.setEstado("A");
		eventAlertaMongoRepository.save(eventAlerta);
		return eventAlerta;

	}
		
	
}
