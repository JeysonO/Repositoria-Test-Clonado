package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoInternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoInterno;

@Service
public class DocumentoInternoService {

	/*
	@Autowired
	private DocumentoInternoJPARepository documentoInternoJPARepository;
	*/

	@Autowired
	private Mapper mapper;

	public DocumentoInterno registrarDocumentoInterno(DocumentoInternoBodyRequest documentoInternoBodyRequest) throws Exception {

		DocumentoInterno documentoInterno = mapper.map(documentoInternoBodyRequest,DocumentoInterno.class);
		documentoInterno.setEstado("A");
		//documentoInternoJPARepository.save(documentoInterno);
		return documentoInterno;

	}
		
	
}
