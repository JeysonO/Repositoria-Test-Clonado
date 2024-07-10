package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.amsac.tramite.api.request.body.bean.PersonaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.PersonaResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.service.PersonaService;

import javax.validation.Valid;

@RestController
@RequestMapping("/personas")
public class PersonaController {

	private static final Logger LOGGER = LogManager.getLogger(PersonaController.class);
	
	//@Autowired
	//private PersonaService personaService;

	//@Autowired
	//private Mapper mapper;

	@PostMapping
    public ResponseEntity<CommonResponse> registrarPersona(@Valid @RequestBody PersonaBodyRequest personaBodyRequest) throws Exception{
		return null;
    	/*
		Persona persona = personaService.registrarPersona(personaBodyRequest);

    	PersonaResponse personaResponse = mapper.map(persona,PersonaResponse.class);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(personaResponse).build();

    	return new ResponseEntity<CommonResponse>(commonResponse,HttpStatus.CREATED);
    	*/

    }

}
