package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.EstadoTramiteBodyRequest;
import pe.com.amsac.tramite.bs.domain.EstadoTramite;
import pe.com.amsac.tramite.bs.repository.EstadoTramiteJPARepository;

@Service
public class EstadoTramiteService {

	@Autowired
	private EstadoTramiteJPARepository estadoTramiteJPARepository;

	@Autowired
	private Mapper mapper;

	public EstadoTramite registrarEstadoTramite(EstadoTramiteBodyRequest estadoTramiteBodyRequest) throws Exception {

		EstadoTramite estadoTramite = mapper.map(estadoTramiteBodyRequest,EstadoTramite.class);
		estadoTramite.setEstado("A");
		estadoTramiteJPARepository.save(estadoTramite);
		return estadoTramite;

	}
		
	
}
