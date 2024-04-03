package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoConfiguracionBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoConfiguracion;
import pe.com.amsac.tramite.bs.repository.DocumentoConfiguracionJPARepository;

@Service
public class DocumentoConfiguracionService {

	@Autowired
	private DocumentoConfiguracionJPARepository documentoConfiguracionJPARepository;

	@Autowired
	private Mapper mapper;

	public DocumentoConfiguracion registrarDocumentoConfiguracion(DocumentoConfiguracionBodyRequest documentoConfiguracionBodyRequest) throws Exception {

		DocumentoConfiguracion documentoConfiguracion = mapper.map(documentoConfiguracionBodyRequest,DocumentoConfiguracion.class);
		documentoConfiguracion.setEstado("A");
		documentoConfiguracionJPARepository.save(documentoConfiguracion);
		return documentoConfiguracion;

	}
		
	
}
