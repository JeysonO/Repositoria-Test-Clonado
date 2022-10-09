package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioBodyRequest;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.UsuarioMongoRepository;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TramiteService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private UsuarioMongoRepository usuarioMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

	@Autowired
	private SecurityHelper securityHelper;

	public List<Tramite> buscarTramiteParams(TramiteRequest tramiteRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
			Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		Criteria expression = new Criteria();
		parameters.values().removeIf(Objects::isNull);
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
		return tramiteList;
	}

	public List<Tramite> buscarHistorialTramite(Map<String, Object> param) throws Exception {
		//Buscar Historico y ordenar por fecha mas receinte
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Criteria expression = new Criteria();
		param.values().removeIf(Objects::isNull);
		param.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		andQuery.with(Sort.by(
				Sort.Order.desc("createdDate")
		));
		List<Tramite> tramite = mongoTemplate.find(andQuery, Tramite.class);
		return tramite;
	}

	public Tramite registrarTramite(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		Map<String, Object> mapaRetorno = numeroDocumentoRepetido(tramiteBodyRequest);
		if(mapaRetorno!=null){
			throw new ServiceException((List<Mensaje>) mapaRetorno.get("errores"), (Map) mapaRetorno.get("atributos"));
		}


		Tramite tramite = mapper.map(tramiteBodyRequest,Tramite.class);
		int numeroTramite = obtenerNumeroTramite().get(0).getNumeroTramite()+1;
		tramite.setNumeroTramite(numeroTramite);
		tramite.setEstado("A");
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
		}
		tramiteMongoRepository.save(tramite);
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO"))
			registrarDerivacion(tramite);
		return tramite;

	}

	public void registrarDerivacion(Tramite tramite) throws Exception {
		//Obtener 1er Usuario de Seguridad-UsuarioCargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuario-cargo/recepcion_mesa_partes";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

		TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest = new TramiteDerivacionBodyRequest();
		tramiteDerivacionBodyRequest.setSecuencia(1);
		tramiteDerivacionBodyRequest.setUsuarioInicio(tramite.getCreatedByUser());
		tramiteDerivacionBodyRequest.setUsuarioFin(((LinkedHashMap)((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("usuario")).get("id").toString());
		tramiteDerivacionBodyRequest.setEstadoInicio("REGISTRADO");
		tramiteDerivacionBodyRequest.setFechaInicio(tramite.getCreatedDate());
		tramiteDerivacionBodyRequest.setTramiteId(tramite.getId());
		tramiteDerivacionBodyRequest.setComentarioInicio("Se inicia registro del Tramite");
		tramiteDerivacionBodyRequest.setForma("ORIGINAL");
		tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyRequest);
	}

	public List<Tramite> obtenerNumeroTramite(){
		Query query = new Query();
		Criteria criteria = Criteria.where("estado").is("A");
		query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("numeroTramite")
		));
		List<Tramite> tramiteList = mongoTemplate.find(query, Tramite.class);
		return tramiteList;
	}

	public List<Tramite> buscarTramiteParamsByUsuarioId(String usuarioId){
		Query query = new Query();
		Criteria criteria = Criteria.where("createdByUser").is(usuarioId);
		query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("createdDate")
		));
		List<Tramite> tramiteList = mongoTemplate.find(query, Tramite.class);
		return tramiteList;
	}

	public Tramite findById(String id){
		return tramiteMongoRepository.findById(id).get();
	}

	public Tramite save(Tramite tramite){
		return tramiteMongoRepository.save(tramite);
	}

	public Map numeroDocumentoRepetido(TramiteBodyRequest tramiteBodyRequest) throws Exception {
		//Obtener persona del Usuario creador de Tramite
		//Obtener todos los usuarios relacionados a Persona encontrada
		//Pasar la lista de Usuarios para buscarHistorial
		Map<String, Object> mapRetorno = null;

		//Validar historial de Tramite
		Map<String, Object> param = new HashMap<>();
		param.put("numeroDocumento",tramiteBodyRequest.getNumeroDocumento());
		if(!StringUtils.isBlank(tramiteBodyRequest.getSiglas()))
			param.put("siglas",tramiteBodyRequest.getSiglas());

		if(!CollectionUtils.isEmpty(buscarHistorialTramite(param))){
			Tramite tramiteRelacionado = buscarHistorialTramite(param).get(0);

			DateFormat Formato = new SimpleDateFormat("dd/mm/yyyy");
			String fechaRegistro = Formato.format(tramiteRelacionado.getCreatedDate());

			List<Mensaje> mensajes = new ArrayList<>();

			if(tramiteRelacionado!=null ){
				mensajes.add(new Mensaje("E001","ERROR","Ya existe un tramite con el mismo número con fecha de registro "+fechaRegistro+", desea relacionar los 2 tramites?"));
				Map<String, Object> atributoMap = new HashMap<>();
				atributoMap.put("idTramiteRelacionado",tramiteRelacionado.getId());
				mapRetorno = new HashMap<>();
				mapRetorno.put("errores",mensajes);
				mapRetorno.put("atributos",atributoMap);
			}
		}

		/*
		//Obtener Persona de historialTramite
		String persona;
		if(tramiteRelacionado.getEntidadExterna().getRazonSocial()!=null)
			persona = tramiteRelacionado.getEntidadExterna().getRazonSocial();
		else
			persona = tramiteRelacionado.getEntidadInterna().getUsuario().getPersona().getRazonSocialNombre();

		//Obtener Persona de Tramite por registrar
		String nuevaPersona;
		if(tramiteBodyRequest.getRazonSocial()!=null)
			nuevaPersona = tramiteBodyRequest.getRazonSocial();
		else{
			Optional<Usuario> usuario = usuarioMongoRepository.findById(tramiteBodyRequest.getUsuarioId());
			nuevaPersona = usuario.get().getPersona().getRazonSocialNombre();
		}
		*/



		return mapRetorno;

	}
	
}
