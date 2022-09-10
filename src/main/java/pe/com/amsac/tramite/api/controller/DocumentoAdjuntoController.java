package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoInternoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoExternoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.DocumentoAdjuntoInternoResponse;
import pe.com.amsac.tramite.api.response.bean.DocumentoAdjuntoExternoResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoInterno;
import pe.com.amsac.tramite.bs.service.DocumentoAdjuntoInternoService;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjuntoExterno;
import pe.com.amsac.tramite.bs.service.DocumentoAdjuntoExternoService;

import javax.validation.Valid;

@RestController
@RequestMapping("/documentos-adjuntos")
public class DocumentoAdjuntoController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoAdjuntoController.class);

	@Autowired
	private DocumentoAdjuntoInternoService documentoAdjuntoInternoService;

	@Autowired
	private DocumentoAdjuntoExternoService documentoAdjuntoExternoService;

	@Autowired
	private Mapper mapper;

	@PostMapping("/internos")
	public ResponseEntity<CommonResponse> registrarDocumentoAdjuntoInterno(@Valid @RequestBody DocumentoAdjuntoInternoBodyRequest documentoAdjuntoInternoBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			DocumentoAdjuntoInterno documentoAdjuntoInterno = documentoAdjuntoInternoService.registrarDocumentoAdjuntoInterno(documentoAdjuntoInternoBodyrequest);

			DocumentoAdjuntoInternoResponse documentoAdjuntoInternoResponse = mapper.map(documentoAdjuntoInterno, DocumentoAdjuntoInternoResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(documentoAdjuntoInternoResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/externos")
	public ResponseEntity<CommonResponse> registrarDocumentoAdjuntoExterno(@Valid @RequestBody DocumentoAdjuntoExternoBodyRequest documentoAdjuntoExternoBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			DocumentoAdjuntoExterno documentoAdjuntoExterno = documentoAdjuntoExternoService.registrarDocumentoAdjuntoExterno(documentoAdjuntoExternoBodyrequest);

			DocumentoAdjuntoExternoResponse documentoAdjuntoExternoResponse = mapper.map(documentoAdjuntoExterno, DocumentoAdjuntoExternoResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(documentoAdjuntoExternoResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
