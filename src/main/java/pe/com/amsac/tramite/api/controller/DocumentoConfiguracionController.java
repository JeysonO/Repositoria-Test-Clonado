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
import pe.com.amsac.tramite.api.request.body.bean.DocumentoConfiguracionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.DocumentoConfiguracionResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.DocumentoConfiguracion;
import pe.com.amsac.tramite.bs.service.DocumentoConfiguracionService;

import javax.validation.Valid;

@RestController
@RequestMapping("/documentos-configuraciones")
public class DocumentoConfiguracionController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoConfiguracionController.class);

	@Autowired
	private DocumentoConfiguracionService documentoConfiguracionService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarDocumentoConfiguracion(@Valid @RequestBody DocumentoConfiguracionBodyRequest documentoConfiguracionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			DocumentoConfiguracion documentoConfiguracion = documentoConfiguracionService.registrarDocumentoConfiguracion(documentoConfiguracionBodyrequest);

			DocumentoConfiguracionResponse documentoConfiguracionResponse = mapper.map(documentoConfiguracion, DocumentoConfiguracionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(documentoConfiguracionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
