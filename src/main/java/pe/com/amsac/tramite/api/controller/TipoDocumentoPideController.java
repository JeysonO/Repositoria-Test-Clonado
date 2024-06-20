package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TipoDocumentoPideResponse;
import pe.com.amsac.tramite.api.response.bean.TipoDocumentoResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TipoDocumento;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoPide;
import pe.com.amsac.tramite.bs.service.TipoDocumentoPideService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tipos-documentos-pide")
public class TipoDocumentoPideController {
	private static final Logger LOGGER = LogManager.getLogger(TipoDocumentoPideController.class);

	@Autowired
	private TipoDocumentoPideService tipoDocumentoPideService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerTipoDocumento() throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {
			List<TipoDocumentoPide> tipoDocumentoPideList = tipoDocumentoPideService.obtenerTipoDocumentoPideByEstado("A");//tipoDocumentoService.findByAllTipoDocumento();
			List<TipoDocumentoPideResponse> tipoDocumentoPideResponseList =  new ArrayList<>();
			TipoDocumentoPideResponse tipoDocumentoPideResponse = null;
			for (TipoDocumentoPide temp : tipoDocumentoPideList) {
				tipoDocumentoPideResponse = mapper.map(temp, TipoDocumentoPideResponse.class);
				tipoDocumentoPideResponseList.add(tipoDocumentoPideResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tipoDocumentoPideResponseList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
