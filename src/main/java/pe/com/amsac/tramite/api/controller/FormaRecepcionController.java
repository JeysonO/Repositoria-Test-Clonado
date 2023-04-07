package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.FormaRecepcionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.FormaRecepcionResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.FormaRecepcion;
import pe.com.amsac.tramite.bs.service.FormaRecepcionService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/formas-recepcion")
public class FormaRecepcionController {

	private static final Logger LOGGER = LogManager.getLogger(FormaRecepcionController.class);

	@Autowired
	private FormaRecepcionService formaRecepcionService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> buscarTipoDocumento(){
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<FormaRecepcion> listaFormaRecepcion = formaRecepcionService.findByAllFormaRecepcion();
			List<FormaRecepcionResponse> obtenerFormaRecepcionList =  new ArrayList<>();
			FormaRecepcionResponse formaRecepcionResponse = null;
			for (FormaRecepcion temp : listaFormaRecepcion) {
				formaRecepcionResponse = mapper.map(temp, FormaRecepcionResponse.class);
				obtenerFormaRecepcionList.add(formaRecepcionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerFormaRecepcionList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


	@PostMapping
	public ResponseEntity<CommonResponse> registrarFormaRecepcion(@Valid @RequestBody FormaRecepcionBodyRequest formaRecepcionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			FormaRecepcion formaRecepcion = formaRecepcionService.registrarFormaRecepcion(formaRecepcionBodyrequest);

			FormaRecepcionResponse formaRecepcionResponse = mapper.map(formaRecepcion, FormaRecepcionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(formaRecepcionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
