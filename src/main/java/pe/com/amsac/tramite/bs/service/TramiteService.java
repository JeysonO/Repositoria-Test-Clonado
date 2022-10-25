package pe.com.amsac.tramite.bs.service;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.response.bean.TramiteDerivacionReporteResponse;
import pe.com.amsac.tramite.api.response.bean.TramiteReporteResponse;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.UsuarioMongoRepository;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.io.InputStream;
import java.net.UnknownHostException;
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
		parameters.values().removeIf(Objects::isNull);
		List<Criteria> listCriteria =  new ArrayList<>();
		if(parameters.containsKey("fechaDocumentoDesde") && parameters.containsKey("fechaDocumentoHasta"))
			listCriteria.add(Criteria.where("fechaDocumento").gte(parameters.get("fechaDocumentoDesde")).lte(parameters.get("fechaDocumentoHasta")));
		if(parameters.containsKey("fechaCreacionDesde") && parameters.containsKey("fechaCreaciontoHasta"))
			listCriteria.add(Criteria.where("createdDate").gte(parameters.get("fechaCreacionDesde")).lte(parameters.get("fechaCreaciontoHasta")));
		if(!listCriteria.isEmpty())
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		if((Integer) parameters.get("numeroTramite")==0) {
			parameters.remove("numeroTramite");
		}
		if(!StringUtils.isBlank(tramiteRequest.getMisTramite())){
			parameters.remove("misTramites");
			parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
		}
		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
		return tramiteList;
	}

	public List<Tramite> buscarHistorialTramite(Map<String, Object> param) throws Exception {

		//Buscar Historico y ordenar por fecha mas reciente
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Criteria expression = new Criteria();
		param.values().removeIf(Objects::isNull);
		param.forEach((key, value) -> expression.and(key).is(value));
		/*for (Map.Entry<String,Object> paramTMP : param.entrySet()) {
			if((paramTMP.getValue() instanceof List))
				//Agregar el criterio con in
				expression.and(paramTMP.getKey()).in(paramTMP.getValue());
			else
				//Agregar el criterio con is
				expression.and(paramTMP.getKey()).is(paramTMP.getValue());
		}*/
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		andQuery.with(Sort.by(
				Sort.Order.desc("createdDate")
		));
		List<Tramite> tramite = mongoTemplate.find(andQuery, Tramite.class);
		return tramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Tramite registrarTramite(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		if(StringUtils.isBlank(tramiteBodyRequest.getIdTramiteRelacionado())){
			Map<String, Object> mapaRetorno = numeroDocumentoRepetido(tramiteBodyRequest);
			if(mapaRetorno!=null){
				throw new ServiceException((List<Mensaje>) mapaRetorno.get("errores"), (Map) mapaRetorno.get("atributos"));
			}
		}

		Tramite tramite = mapper.map(tramiteBodyRequest,Tramite.class);

		List<Tramite> tramiteList = obtenerNumeroTramite();

		int numeroTramite = 1;

		if(!CollectionUtils.isEmpty(tramiteList))
			numeroTramite = obtenerNumeroTramite().get(0).getNumeroTramite()+1;

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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
		String usuarioId = securityHelper.obtenerUserIdSession();
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-externo-by-id";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

		//Mapear Persona de Usuario Creador Tramite
		LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		LinkedHashMap<String, String> personaL = (LinkedHashMap<String, String>) usuario.get("persona");

		//Obtener todos los usuarios relacionados a Persona encontrada
		String uri1 = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-persona-id/"+personaL.get("id");
		ResponseEntity<CommonResponse> response1 = restTemplate.exchange(uri1,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

		ArrayList listaUsuario = (ArrayList) response1.getBody().getData();

		List<String> idsUsuariosByPersona = new ArrayList<>();

		for (Object cadena : listaUsuario) {
			LinkedHashMap<Object, Object> usuarios = (LinkedHashMap<Object, Object>) cadena;
			idsUsuariosByPersona.add((String) usuarios.get("id"));
		}

		//Validar historial de Tramite
		Map<String, Object> param = new HashMap<>();
		//param.put("createdByUser",idsUsuariosByPersona);
		param.put("numeroDocumento",tramiteBodyRequest.getNumeroDocumento());
		if(!StringUtils.isBlank(tramiteBodyRequest.getSiglas()))
			param.put("siglas",tramiteBodyRequest.getSiglas());

		List<Tramite> primeraLista = buscarHistorialTramite(param);
		List<Tramite> tramiteList = new ArrayList<>();

		for(String usuarioTmp : idsUsuariosByPersona){
			for(Tramite tramiteTmp : primeraLista){
				if(tramiteTmp.getCreatedByUser().equals(usuarioTmp))
					tramiteList.add(tramiteTmp);
			}
		}
		Collections.sort(tramiteList, new Comparator<Tramite>(){
			@Override
			public int compare(Tramite o1, Tramite o2) {
				return o1.getCreatedDate().compareTo(o2.getCreatedDate());
			}
		});

		Map<String, Object> mapRetorno = null;

		//if(!CollectionUtils.isEmpty(buscarHistorialTramite(param))){
			//Tramite tramiteRelacionado = buscarHistorialTramite(param).get(0);
		if(!CollectionUtils.isEmpty(tramiteList)){
			Tramite tramiteRelacionado = tramiteList.get(0);

			DateFormat Formato = new SimpleDateFormat("dd/MM/yyyy");
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

		return mapRetorno;

	}

	public List<TramiteReporteResponse> generarReporteTramiteSeguimiento(TramiteRequest tramiteRequest) throws Exception{
		//Persona y Tipo Persona de Creador de cada tramite
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		List<Tramite> tramiteList = buscarTramiteParams(tramiteRequest);
		List<TramiteReporteResponse> tramiteReporteResponseList = new ArrayList<>();

		for(Tramite tramite : tramiteList){
			TramiteReporteResponse tramiteReporteResponse = mapper.map(tramite,TramiteReporteResponse.class);
			tramiteReporteResponse.setTramiteDerivacion(tramiteDerivacionService.obtenerTramiteByTramiteId(tramite.getId()));

			String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/" + tramite.getCreatedByUser();
			ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			//Mapear Persona de Usuario Inicio
			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> personaL = (LinkedHashMap<String, String>) usuario.get("persona");
			personaL.remove("createdDate");
			personaL.remove("lastModifiedDate");
			personaL.remove("tipoDocumento");
			personaL.remove("entityId");
			Persona persona = mapper.map(personaL,Persona.class);
			usuario.replace("persona",persona);
			Usuario user = mapper.map(usuario,Usuario.class);

			tramiteReporteResponse.setUsuario(user.getNombreCompleto());
			tramiteReporteResponse.setPersona(persona.getRazonSocialNombre());
			tramiteReporteResponse.setCreatedDate(tramite.getCreatedDate());
			tramiteReporteResponseList.add(tramiteReporteResponse);
		}

		return tramiteReporteResponseList;

	}

	public JasperPrint exportPdfFile(TramiteRequest tramiteRequest) throws Exception {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream url1 = classloader.getResourceAsStream("reporteTramite.jrxml");
		InputStream url2 = classloader.getResourceAsStream("subReporteTramiteDerivacio.jrxml");

		JasperReport jasperReport = JasperCompileManager.compileReport(url1);
		JasperReport jasperReport1 = JasperCompileManager.compileReport(url2);

		List<TramiteReporteResponse> tramiteReporteResponseList = generarReporteTramiteSeguimiento(tramiteRequest);

		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(tramiteReporteResponseList);

		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("DataSurse", source );
		parameters.put("fechaReporte", new Date() );
		parameters.put("subReporteUrl", jasperReport1 );
		//parameters.put("title", "Reporte Tramite Derivacion");

		JasperPrint print = JasperFillManager.fillReport(jasperReport,parameters,source);

		return print;
	}
}
