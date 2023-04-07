package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoMigracionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.WrapperDocumentoAdjuntoMigracionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.service.DocumentoAdjuntoService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/documentos-adjuntos")
public class DocumentoAdjuntoController {
	private static final Logger LOGGER = LogManager.getLogger(DocumentoAdjuntoController.class);

	@Autowired
	private DocumentoAdjuntoService documentoAdjuntoService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerDocumentoAdjunto(@Valid DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<DocumentoAdjuntoResponse> documentoAdjuntoList =  documentoAdjuntoService.obtenerDocumentoAdjuntoList(documentoAdjuntoRequest);
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(documentoAdjuntoList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

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

	@GetMapping("/downloadFile/{documentoAdjuntoId}")
	public ResponseEntity<Resource> downloadFileEscala(@PathVariable String documentoAdjuntoId, HttpServletRequest request)
			throws Exception {
		try {
			DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
			documentoAdjuntoRequest.setId(documentoAdjuntoId);

			Resource resource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);

			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
				LOGGER.info("No se puede determinar el MimeType.");
			}

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);

		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping("/migracion")
	public ResponseEntity<CommonResponse> registrarDocumentoAdjuntoMigracion(@Valid @RequestBody WrapperDocumentoAdjuntoMigracionBodyRequest wrapperDocumentoAdjuntoMigracionBodyRequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		List<Map> listaRespuesta = new ArrayList<>();
		for(DocumentoAdjuntoMigracionBodyRequest documentoAdjuntoMigracionBodyRequest : wrapperDocumentoAdjuntoMigracionBodyRequest.getDocumentosAdjuntos()){
			try{
				documentoAdjuntoService.registrarDocumentoAdjuntoMigracion(documentoAdjuntoMigracionBodyRequest);
				listaRespuesta.add(new HashMap(){{
					put(documentoAdjuntoMigracionBodyRequest.getNombreArchivo(), "OK");
				}});
			}catch(Exception ex){
				listaRespuesta.add(new HashMap(){{
					put(documentoAdjuntoMigracionBodyRequest.getNombreArchivo(), "ERROR");
				}});
			}
		}

		if(listaRespuesta.stream().filter(x -> x.containsValue("ERROR")).count()>0)
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, null)).data(listaRespuesta).build();
		else
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(listaRespuesta).build();


		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
