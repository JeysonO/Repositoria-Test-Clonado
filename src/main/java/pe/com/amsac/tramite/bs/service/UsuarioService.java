package pe.com.amsac.tramite.bs.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.KeycloakProperties;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.UsuarioJPARepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class UsuarioService {

	@Autowired
	private UsuarioJPARepository usuarioJPARepository;

	@Autowired
	private Mapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KeycloakProperties keycloakProperties;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private Environment env;

	/*
	public Usuario registrarUsuario(UsuarioBodyRequest usuarioCreateBodyRequest) throws Exception{

		//Se hacen validaciones de los datos del usuario a registrar
		List<Mensaje> mensajesError = validarDatosUsuario(usuarioCreateBodyRequest);
		if(!CollectionUtils.isEmpty(mensajesError)){
			throw new ServiceException(mensajesError);
		}

		//Generarle un usuario que no exista
		usuarioCreateBodyRequest.setUsuario(generateUsername(usuarioCreateBodyRequest));

		//Enviamos a crear el usuario a Keycloak
		createUserOnKeycloak(usuarioCreateBodyRequest);

		Usuario usuario = mapper.map(usuarioCreateBodyRequest,Usuario.class);
		usuarioJPARepository.save(usuario);
		return usuario;

	}

	public List<Mensaje> validarDatosUsuario(UsuarioBodyRequest usuarioCreateBodyRequest){

		Validar:
		- Que no exista otro usuario con el mismo email en la base de datos
		- Que no exista otro usuario con el mismo username en la base de datos

		List<Mensaje> mensajes = new ArrayList<>();
		if(!CollectionUtils.isEmpty(usuarioJPARepository.findByEmail(usuarioCreateBodyRequest.getEmail()))){
			mensajes.add(new Mensaje("E001","ERROR","Ya existe otro usuario con el mismo email"));
		}

		if(!StringUtils.isBlank(usuarioCreateBodyRequest.getUsuario())
			&& !CollectionUtils.isEmpty(usuarioJPARepository.findByUsuario(usuarioCreateBodyRequest.getUsuario()))){
			mensajes.add(new Mensaje("E001","ERROR","Ya existe otro usuario con el mismo username"));
		}

		return mensajes;

	}

	public String generateUsername(UsuarioBodyRequest usuarioBodyRequest) throws ServiceException {

		if(!StringUtils.isBlank(usuarioBodyRequest.getUsuario()))
			return usuarioBodyRequest.getUsuario();

		if(StringUtils.isBlank(usuarioBodyRequest.getNombre())
				|| StringUtils.isBlank(usuarioBodyRequest.getApePaterno())
				|| StringUtils.isBlank(usuarioBodyRequest.getApeMaterno()))
			throw new ServiceException(Arrays.asList(new Mensaje[]{new Mensaje("E001","ERROR","Debe enviar apellido paterno y materno para generar el username")}));

		String username = generateUsername(usuarioBodyRequest.getNombre(), usuarioBodyRequest.getApePaterno(), usuarioBodyRequest.getApeMaterno(),0);

		return username;

	}

	public String generateUsername(String nombre, String apellidoPaterno, String apellidoMaterno, int contadorComodin){
		String username = nombre.substring(0,1).concat(apellidoPaterno).concat(StringUtils.isBlank(apellidoMaterno)?"":apellidoMaterno.substring(0,1)).concat(contadorComodin==0?"":String.valueOf(contadorComodin)).toLowerCase();
		if(usuarioJPARepository.findByUsuario(username)!=null)
			username = generateUsername(nombre,apellidoPaterno,apellidoMaterno,++contadorComodin);
		return username;
	}

	private void createUserOnKeycloak(UsuarioBodyRequest usuarioCreateBodyRequest) throws Exception{
		//Primero nos autenticamos
		String token = obtenerTokenAutenticacion();
		//Ahora creamos el usuario

	}

	private String obtenerTokenAutenticacion(){
		String token = "";
		//ResponseEntity<NumeracionCEResponse> numeracionCEResponseResponseEntity =
		//		restTemplate.postForEntity("http://62.171.156.26:8180/sunatwsapi/api/v1/relacion-carga-embarcar",relacionCargaEmbarcarBodyRequest, NumeracionCEResponse.class);

		return token;
	}
	*/

	public Usuario obtenerUsuarioById(String usuarioId){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		//Mapear Usuario y Persona
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/" + usuarioId;
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<CommonResponse>() {
		});

		LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");

		Persona person = mapper.map(persona, Persona.class);
		usuario.replace("persona", person);
		Usuario usuarioEntidad = mapper.map(usuario, Usuario.class);

		return usuarioEntidad;

	}


	
}
