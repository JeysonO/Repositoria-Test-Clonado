package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

@Service
public class TramiteService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private Mapper mapper;

	public Tramite registrarTramite(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		Tramite tramite = mapper.map(tramiteBodyRequest,Tramite.class);
		tramite.setEstado("A");
		tramiteMongoRepository.save(tramite);
		return tramite;

	}
		
	
}
