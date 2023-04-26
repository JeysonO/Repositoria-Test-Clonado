package pe.com.amsac.tramite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.UsuarioFirmaRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.UsuarioFirmaCreateResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.service.UsuarioFirmaService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import pe.com.amsac.security.api.response.bean.UsuarioCreateResponse;

@Slf4j
@RestController
@RequestMapping("/usuarios-firma")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class UsuarioFirmaController { //extends CustomAPIController<UsuarioResponse, Long> {

	@Autowired
	private UsuarioFirmaService usuarioFirmaService;

	@Autowired
	private Mapper mapper;

	@PostMapping
    public ResponseEntity<CommonResponse> registrarUsuarioFirma(@Valid @RequestBody UsuarioFirmaBodyRequest usuarioFirmaBodyrequest) throws Exception {

		log.info("registrarUsuarioFirma: "+new ObjectMapper().writeValueAsString(usuarioFirmaBodyrequest));

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try{
			UsuarioFirma usuarioFirma = usuarioFirmaService.registrarUsuarioFirma(usuarioFirmaBodyrequest);

			UsuarioFirmaCreateResponse usuarioFirmaResponse = mapper.map(usuarioFirma, UsuarioFirmaCreateResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaResponse).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

    }

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<CommonResponse> obtenerUsuarioFirmaByUsuarioId(@PathVariable String usuarioId) throws Exception {

		log.info("obtenerUsuarioFirmaByUsuarioId: "+usuarioId);

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			UsuarioFirma usuarioFirma = usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);

			UsuarioFirmaCreateResponse usuarioFirmaResponse = mapper.map(usuarioFirma, UsuarioFirmaCreateResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaResponse).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerUsuarioFirma(@Valid UsuarioFirmaRequest usuarioFirmaRequest ) throws Exception {

		log.info("obtenerUsuarioFirmaByEstado");

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			List<UsuarioFirma> usuarioFirmaList = usuarioFirmaService.obtenerUsuarioFirma(usuarioFirmaRequest);
			List<UsuarioFirmaCreateResponse> usuarioFirmaResponseList = new ArrayList<>();

			if(!CollectionUtils.isEmpty(usuarioFirmaList)){
				for(UsuarioFirma usuarioFirmaTmp:usuarioFirmaList){
					usuarioFirmaResponseList.add(mapper.map(usuarioFirmaTmp, UsuarioFirmaCreateResponse.class));
				}
			}

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaResponseList).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@DeleteMapping("/{usuarioFirmaId}")
	public ResponseEntity<CommonResponse> eliminarUsuarioFirma(@PathVariable String usuarioFirmaId) throws Exception {

		log.info("eliminarUsuarioFirmaLogo: "+usuarioFirmaId);

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			usuarioFirmaService.eliminarUsuarioFirmaById(usuarioFirmaId);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@GetMapping("/usuario")
	public ResponseEntity<CommonResponse> obtenerUsuarioFirmaByUsuario() throws Exception {

		log.info("obtenerUsuarioFirmaByEstado");

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			UsuarioFirma usuarioFirma = usuarioFirmaService.obtenerUsuarioFirmaByUsuario();

			UsuarioFirmaCreateResponse usuarioFirmaResponse = mapper.map(usuarioFirma, UsuarioFirmaCreateResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaResponse).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			//httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@GetMapping("/record-count")
	public ResponseEntity<CommonResponse> recordCountObtenerUsuarioFirma(@Valid UsuarioFirmaRequest usuarioFirmaRequest ) throws Exception {

		log.info("recordCountObtenerUsuarioFirma");

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			int cantidadRegistros = usuarioFirmaService.totalRegistros(usuarioFirmaRequest);

			Map<String, Object> param = new HashMap<>();
			param.put("cantidadRegistros", cantidadRegistros);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

}
