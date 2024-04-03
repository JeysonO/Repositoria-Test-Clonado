package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoExternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoExterno;
import pe.com.amsac.tramite.bs.repository.DocumentoExternoJPARepository;

@Service
public class DocumentoExternoService {

	@Autowired
	private DocumentoExternoJPARepository documentoExternoJPARepository;

	@Autowired
	private Mapper mapper;

	public DocumentoExterno registrarDocumentoExterno(DocumentoExternoBodyRequest documentoExternoBodyRequest) throws Exception {

		DocumentoExterno documentoExterno = mapper.map(documentoExternoBodyRequest,DocumentoExterno.class);
		documentoExterno.setEstado("A");
		documentoExternoJPARepository.save(documentoExterno);
		return documentoExterno;

	}
		
	
}
