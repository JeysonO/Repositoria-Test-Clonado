package pe.com.amsac.tramite.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.UsuarioCreateResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TextoFirma;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.service.TextoFirmaService;
import pe.com.amsac.tramite.bs.service.UsuarioService;

import javax.validation.Valid;
import java.util.List;
//import pe.com.amsac.security.api.response.bean.UsuarioCreateResponse;

@Slf4j
@RestController
@RequestMapping("/textos-firma")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class TextosFirmaController { //extends CustomAPIController<UsuarioResponse, Long> {

	@Autowired
	private TextoFirmaService textoFirmaService;

	@Autowired
	private Mapper mapper;

	@GetMapping
    public ResponseEntity<CommonResponse> obtenerTextosFirma() throws Exception {

		log.info("Obtener Textos Firma");

		HttpStatus httpStatus = HttpStatus.OK;

		List<TextoFirma> textosFirma = textoFirmaService.obtenerTextosFirma();

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(textosFirma).build();

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

    }

}
