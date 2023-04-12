package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoExternoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoExterno;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoExternoMongoRepository;

@Service
public class DocumentoAdjuntoExternoService {

	@Autowired
	private DocumentoAdjuntoExternoMongoRepository documentoAdjuntoExternoMongoRepository;

	@Autowired
	private Mapper mapper;

	public DocumentoAdjuntoExterno registrarDocumentoAdjuntoExterno(DocumentoAdjuntoExternoBodyRequest documentoAdjuntoExternoBodyRequest) throws Exception {

		DocumentoAdjuntoExterno documentoAdjuntoExterno = mapper.map(documentoAdjuntoExternoBodyRequest,DocumentoAdjuntoExterno.class);
		documentoAdjuntoExterno.setEstado("A");
		documentoAdjuntoExternoMongoRepository.save(documentoAdjuntoExterno);
		return documentoAdjuntoExterno;

	}
		
	
}
