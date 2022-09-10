package pe.com.amsac.tramite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.UsuarioCreateResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.service.UsuarioService;

import javax.validation.Valid;
//import pe.com.amsac.security.api.response.bean.UsuarioCreateResponse;

@Slf4j
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class UsuarioController { //extends CustomAPIController<UsuarioResponse, Long> {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private Mapper mapper;

	@PostMapping
    public ResponseEntity<CommonResponse> registrarUsuario(@Valid @RequestBody UsuarioBodyRequest usuarioBodyrequest) throws Exception {

		log.info("Datos usuario para registrar: "+new ObjectMapper().writeValueAsString(usuarioBodyrequest));

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try{
			Usuario usuario = usuarioService.registrarUsuario(usuarioBodyrequest);

			UsuarioCreateResponse usuarioResponse = mapper.map(usuario, UsuarioCreateResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(usuarioResponse).build();


		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

    }

    /*
	@PutMapping
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@Valid @RequestBody UsuarioBodyRequest usuarioBodyrequest) throws Exception{
    	try {
    		
    		UsuarioResponse usuarioResponse = usuarioFacade.actualizarUsuario(usuarioBodyrequest);
        	
        	return new ResponseEntity<UsuarioResponse>(usuarioResponse,HttpStatus.CREATED);
    	}catch (Exception e) {
    		log.error("Error en actualizarUsuario", e);
			throw e;
		}
    }

	@PostMapping("/crear-usuario-rol-aplicacion")
	public ResponseEntity<UsuarioCreateResponse> registrarUsuarioAndRolAplicacion(@Valid @RequestBody UsuarioBodyRequest usuarioBodyrequest) throws Exception{

		UsuarioCreateResponse usuarioResponse = usuarioFacade.registrarUsuarioAndRolAplicacion(usuarioBodyrequest);

		return new ResponseEntity<UsuarioCreateResponse>(usuarioResponse,HttpStatus.CREATED);

	}

	@PostMapping("/reestablecer-password")
	public ResponseEntity<UsuarioCreateResponse> reestablecerContraseña(@Valid @RequestBody ReestablecerPasswordBodyRequest usuarioBodyrequest) throws Exception {

		UsuarioCreateResponse usuarioResponse = usuarioFacade.reestablecerPassword(usuarioBodyrequest);

		return new ResponseEntity<UsuarioCreateResponse>(usuarioResponse,HttpStatus.OK);

	}
	*/
}
