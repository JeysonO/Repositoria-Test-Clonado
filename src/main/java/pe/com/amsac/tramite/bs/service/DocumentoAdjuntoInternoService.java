package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoInternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoInterno;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoInternoMongoRepository;

@Service
public class DocumentoAdjuntoInternoService {

	@Autowired
	private DocumentoAdjuntoInternoMongoRepository documentoAdjuntoInternoMongoRepository;

	@Autowired
	private Mapper mapper;

	public DocumentoAdjuntoInterno registrarDocumentoAdjuntoInterno(DocumentoAdjuntoInternoBodyRequest documentoAdjuntoInternoBodyRequest) throws Exception {

		DocumentoAdjuntoInterno documentoAdjuntoInterno = mapper.map(documentoAdjuntoInternoBodyRequest,DocumentoAdjuntoInterno.class);
		documentoAdjuntoInterno.setEstado("A");
		documentoAdjuntoInternoMongoRepository.save(documentoAdjuntoInterno);
		return documentoAdjuntoInterno;

	}
		
	
}
