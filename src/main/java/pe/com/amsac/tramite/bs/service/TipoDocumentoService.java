package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.TipoDocumentoBodyRequest;
import pe.com.amsac.tramite.bs.domain.TipoDocumento;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoMongoRepository;

@Service
public class TipoDocumentoService {

	@Autowired
	private TipoDocumentoMongoRepository tipoDocumentoMongoRepository;

	@Autowired
	private Mapper mapper;

	public TipoDocumento registrarTipoDocumento(TipoDocumentoBodyRequest tipoDocumentoBodyRequest) throws Exception {

		TipoDocumento tipoDocumento = mapper.map(tipoDocumentoBodyRequest,TipoDocumento.class);
		tipoDocumento.setEstado("A");
		tipoDocumentoMongoRepository.save(tipoDocumento);
		return tipoDocumento;

	}
		
	
}
