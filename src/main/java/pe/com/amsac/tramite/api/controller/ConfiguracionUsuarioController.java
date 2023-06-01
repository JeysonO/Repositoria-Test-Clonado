package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
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
import pe.com.amsac.tramite.api.request.body.bean.ConfiguracionUsuarioBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteExternoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.ConfiguracionUsuarioResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.ConfiguracionUsuario;
import pe.com.amsac.tramite.bs.service.ConfiguracionUsuarioService;
import pe.com.amsac.tramite.bs.service.FirmaDocumentoService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RestController
@RequestMapping("/configuracion_usuario")
public class ConfiguracionUsuarioController {

	@Autowired
	private ConfiguracionUsuarioService configuracionUsuarioService;

	@Autowired
	private Mapper mapper;

    @PostMapping
    public ResponseEntity<CommonResponse> registrarConfiguracionUsuario(@Valid @RequestBody ConfiguracionUsuarioBodyRequest configuracionUsuarioBodyRequest) throws Exception {

		log.info(">> registrarConfiguracionUsuario");


		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			ConfiguracionUsuario configuracionUsuario = configuracionUsuarioService.registrarConfiguracionUsuario(configuracionUsuarioBodyRequest);

			ConfiguracionUsuarioResponse configuracionUsuarioResponse = mapper.map(configuracionUsuario,ConfiguracionUsuarioResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(configuracionUsuarioResponse).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}
		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

    }

	@PutMapping
	public ResponseEntity<CommonResponse> actualizarConfiguracionUsuario(@Valid @RequestBody ConfiguracionUsuarioBodyRequest configuracionUsuarioBodyRequest) throws Exception {

		log.info(">> actualizarConfiguracionUsuario");


		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			configuracionUsuarioService.registrarConfiguracionUsuario(configuracionUsuarioBodyRequest);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}
		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<CommonResponse> actualizarConfiguracionUsuario(@PathVariable String usuarioId) throws Exception {

		log.info(">> actualizarConfiguracionUsuario");


		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			ConfiguracionUsuario configuracionUsuario = configuracionUsuarioService.obtenerConfiguracionUsuario(usuarioId);

			ConfiguracionUsuarioResponse configuracionUsuarioResponse = null;

			if(configuracionUsuario!=null)
				configuracionUsuarioResponse = mapper.map(configuracionUsuario,ConfiguracionUsuarioResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(configuracionUsuarioResponse).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}
		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

}
