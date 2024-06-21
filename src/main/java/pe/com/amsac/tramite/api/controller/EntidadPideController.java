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
import pe.com.amsac.tramite.api.response.bean.EntidadPideResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.EntidadPide;
import pe.com.amsac.tramite.bs.service.EntidadPideService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/entidades-pide")
public class EntidadPideController {
	private static final Logger LOGGER = LogManager.getLogger(EntidadPideController.class);

	@Autowired
	private EntidadPideService tipoDocumentoPideService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerEntidadPide() throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {
			List<EntidadPide> entidadPideList = tipoDocumentoPideService.obtenerEntidadPideByEstado("A");//tipoDocumentoService.findByAllTipoDocumento();
			List<EntidadPideResponse> entidadPideResponseList =  new ArrayList<>();
			EntidadPideResponse entidadPideResponse = null;
			for (EntidadPide temp : entidadPideList) {
				entidadPideResponse = mapper.map(temp, EntidadPideResponse.class);
				entidadPideResponseList.add(entidadPideResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(entidadPideResponseList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
