package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.UsuarioFirmaCreateResponse;
import pe.com.amsac.tramite.api.response.bean.UsuarioFirmaLogoCreateResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;
import pe.com.amsac.tramite.bs.service.UsuarioFirmaLogoService;

import java.util.ArrayList;
import java.util.List;
//import pe.com.amsac.security.api.response.bean.UsuarioCreateResponse;

@Slf4j
@RestController
@RequestMapping("/usuarios-firma-logo")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class UsuarioFirmaLogoController { //extends CustomAPIController<UsuarioResponse, Long> {

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	@Autowired
	private Mapper mapper;

	@PostMapping
    public ResponseEntity<CommonResponse> registrarUsuarioFirmaLogo(
			@RequestParam(value = "usuarioFirmaId", required = true) String usuarioFirmaId,
			@RequestParam(value = "descripcion", required = false) String descripcion,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try{
			UsuarioFirmaLogo usuarioFirmaLogo = usuarioFirmaLogoService.registrarUsuarioFirmaLogo(usuarioFirmaId, descripcion, file);

			UsuarioFirmaLogoCreateResponse usuarioFirmaLogoResponse = mapper.map(usuarioFirmaLogo, UsuarioFirmaLogoCreateResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaLogoResponse).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

    }

	@GetMapping("/usuario-firma/{usuarioFirmaId}")
	public ResponseEntity<CommonResponse> obtenerUsuarioFirmaLogoByUsuarioFirmaId(@PathVariable String usuarioFirmaId) throws Exception {

		log.info("obtenerUsuarioFirmaLogoByUsuarioFirmaId: "+usuarioFirmaId);

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			List<UsuarioFirmaLogo> usuarioFirmaLogoList = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaId);
			List<UsuarioFirmaLogoCreateResponse> usuarioFirmaLogoResponseList = new ArrayList<>();

			if(!CollectionUtils.isEmpty(usuarioFirmaLogoList)){
				for(UsuarioFirmaLogo usuarioFirmaLogoTmp:usuarioFirmaLogoList){
					usuarioFirmaLogoResponseList.add(mapper.map(usuarioFirmaLogoTmp, UsuarioFirmaLogoCreateResponse.class));
				}
			}

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioFirmaLogoResponseList).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@DeleteMapping("/{usuarioFirmaLogoId}")
	public ResponseEntity<CommonResponse> eliminarUsuarioFirmaLogo(@PathVariable String usuarioFirmaLogoId) throws Exception {

		log.info("eliminarUsuarioFirmaLogo: "+usuarioFirmaLogoId);

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{
			usuarioFirmaLogoService.eliminarUsuarioFirmaLogoById(usuarioFirmaLogoId);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		}catch(Exception se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,null)).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

}
