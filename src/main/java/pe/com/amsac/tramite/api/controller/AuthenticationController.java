package pe.com.amsac.tramite.api.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.KeycloakProperties;
import pe.com.amsac.tramite.api.request.bean.LoginRequest;
import pe.com.amsac.tramite.api.response.bean.AuthenticationKeycloakResponse;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RestController
@RequestMapping("/signin")
public class AuthenticationController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KeycloakProperties keycloakProperties;

    @PostMapping
    public ResponseEntity<?> autenticacionUsuario(@Valid @RequestBody LoginRequest loginRequest) throws Exception {

		log.info(">> authenticateUser keycloak: "+loginRequest.getUsername());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", loginRequest.getUsername());
		map.add("password", loginRequest.getPassword());
		map.add("client_id", loginRequest.getClientId());
		map.add("client_secret", loginRequest.getClientSecret());
		map.add("grant_type", keycloakProperties.getGrantType());

		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
		ResponseEntity<AuthenticationKeycloakResponse> authenticationKeycloakResponse = restTemplate.postForEntity(keycloakProperties.getUrl(), httpEntity, AuthenticationKeycloakResponse.class);

		//Creamos la respuesta
		Map mapaRespuesta = new HashMap<>();
		mapaRespuesta.put("token",authenticationKeycloakResponse.getBody().getAccess_token());
		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).data(mapaRespuesta).build();

		return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

    }

}
