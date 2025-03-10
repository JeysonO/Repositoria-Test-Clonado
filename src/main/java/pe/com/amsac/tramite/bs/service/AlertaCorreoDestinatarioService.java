package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.AlertaCorreoDestinatarioBodyRequest;
import pe.com.amsac.tramite.bs.domain.AlertaCorreoDestinatario;
import pe.com.amsac.tramite.bs.repository.AlertaCorreoDestinatarioJPARepository;

@Service
public class AlertaCorreoDestinatarioService {

	@Autowired
	private AlertaCorreoDestinatarioJPARepository alertaCorreoDestinatarioJPARepository;

	@Autowired
	private Mapper mapper;

	public AlertaCorreoDestinatario registrarAlertaCorreoDestinatario(AlertaCorreoDestinatarioBodyRequest alertaCorreoDestinatarioBodyRequest) throws Exception {

		AlertaCorreoDestinatario alertaCorreoDestinatario = mapper.map(alertaCorreoDestinatarioBodyRequest,AlertaCorreoDestinatario.class);
		alertaCorreoDestinatario.setEstado("A");
		alertaCorreoDestinatarioJPARepository.save(alertaCorreoDestinatario);
		return alertaCorreoDestinatario;

	}
		
	
}
