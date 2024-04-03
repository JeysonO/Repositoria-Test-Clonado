package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.AlertaBodyRequest;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.repository.AlertaJPARepository;

@Service
public class AlertaService {

	@Autowired
	private AlertaJPARepository alertaJPARepository;

	@Autowired
	private Mapper mapper;

	public Alerta registrarAlerta(AlertaBodyRequest alertaBodyRequest) throws Exception {

		Alerta alerta = mapper.map(alertaBodyRequest,Alerta.class);
		alerta.setEstado("A");
		alertaJPARepository.save(alerta);
		return alerta;

	}
		
	
}
