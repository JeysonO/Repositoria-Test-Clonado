package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoInternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoInterno;
import pe.com.amsac.tramite.bs.repository.DocumentoInternoMongoRepository;

@Service
public class DocumentoInternoService {

	@Autowired
	private DocumentoInternoMongoRepository documentoInternoMongoRepository;

	@Autowired
	private Mapper mapper;

	public DocumentoInterno registrarDocumentoInterno(DocumentoInternoBodyRequest documentoInternoBodyRequest) throws Exception {

		DocumentoInterno documentoInterno = mapper.map(documentoInternoBodyRequest,DocumentoInterno.class);
		documentoInterno.setEstado("A");
		documentoInternoMongoRepository.save(documentoInterno);
		return documentoInterno;

	}
		
	
}
