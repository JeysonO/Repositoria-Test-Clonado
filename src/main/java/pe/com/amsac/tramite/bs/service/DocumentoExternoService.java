package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoExternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoExterno;
import pe.com.amsac.tramite.bs.repository.DocumentoExternoMongoRepository;

@Service
public class DocumentoExternoService {

	@Autowired
	private DocumentoExternoMongoRepository documentoExternoMongoRepository;

	@Autowired
	private Mapper mapper;

	public DocumentoExterno registrarDocumentoExterno(DocumentoExternoBodyRequest documentoExternoBodyRequest) throws Exception {

		DocumentoExterno documentoExterno = mapper.map(documentoExternoBodyRequest,DocumentoExterno.class);
		documentoExterno.setEstado("A");
		documentoExternoMongoRepository.save(documentoExterno);
		return documentoExterno;

	}
		
	
}
