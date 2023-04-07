package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.TipoDocumentoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.TipoDocumentoResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TipoDocumento;
import pe.com.amsac.tramite.bs.service.TipoDocumentoService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tipos-documentos")
public class TipoDocumentoController {
	private static final Logger LOGGER = LogManager.getLogger(TipoDocumentoController.class);

	@Autowired
	private TipoDocumentoService tipoDocumentoService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarTipoDocumento(@Valid @RequestBody TipoDocumentoBodyRequest tipoDocumentoBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			TipoDocumento tipoDocumento = tipoDocumentoService.registrarTipoDocumento(tipoDocumentoBodyrequest);

			TipoDocumentoResponse tipoDocumentoResponse = mapper.map(tipoDocumento, TipoDocumentoResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tipoDocumentoResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerTipoDocumento() throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {
			List<TipoDocumento> listaTipoDocumento = tipoDocumentoService.findByAllTipoDocumento();
			List<TipoDocumentoResponse> obtenerTipoDocumentoList =  new ArrayList<>();
			TipoDocumentoResponse tipoDocumentoResponse = null;
			for (TipoDocumento temp : listaTipoDocumento) {
				tipoDocumentoResponse = mapper.map(temp, TipoDocumentoResponse.class);
				obtenerTipoDocumentoList.add(tipoDocumentoResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTipoDocumentoList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
