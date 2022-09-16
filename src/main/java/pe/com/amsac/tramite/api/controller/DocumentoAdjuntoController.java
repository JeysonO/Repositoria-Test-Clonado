package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.service.DocumentoAdjuntoService;

@RestController
@RequestMapping("/documentos-adjuntos")
public class DocumentoAdjuntoController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoAdjuntoController.class);

	@Autowired
	private DocumentoAdjuntoService documentoAdjuntoService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarDocumentoAdjunto(
			@RequestParam(value = "tramiteId", required = true) String tramiteId,
			@RequestParam(value = "descripcion", required = false) String descripcion,
			@RequestParam(value = "tipoAdjunto", required = false) String tipoAdjunto,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			DocumentoAdjuntoBodyRequest documentoAdjuntoRequest = new DocumentoAdjuntoBodyRequest();
			documentoAdjuntoRequest.setTramiteId(tramiteId);
			documentoAdjuntoRequest.setDescripcion(descripcion);
			documentoAdjuntoRequest.setFile(file);
			documentoAdjuntoRequest.setTipoAdjunto(tipoAdjunto);

			DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoRequest);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(documentoAdjuntoResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
