package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.bean.EventSchedule;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteMigracionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.CustomMultipartFile;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMigracionMongoRepository;
import pe.com.amsac.tramite.bs.repository.UsuarioMongoRepository;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.util.EstadoTramiteConstant;
import pe.com.amsac.tramite.bs.util.TipoAdjuntoConstant;
import pe.com.amsac.tramite.bs.util.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequestScope
public class TramiteService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private TramiteMigracionMongoRepository tramiteMigracionMongoRepository;

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private UsuarioMongoRepository usuarioMongoRepository;

	@Autowired
	private TipoDocumentoMongoRepository tipoDocumentoMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private CalendarioService calendarioService;

	@Autowired
	private ConfiguracionService consiguracionService;

	@Autowired
	private DocumentoAdjuntoService documentoAdjuntoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private FormaRecepcionService formaRecepcionService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private Util util;

	Map<String, Object> filtroParam = new HashMap<>();

	public List<Tramite> buscarTramiteParams(TramiteRequest tramiteRequest) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		List<Criteria> listCriteria =  new ArrayList<>();
		if(parameters.containsKey("soloOriginal")){
			parameters.remove("soloOriginal");
		}
		/*if(parameters.containsKey("fechaDocumentoDesde") && parameters.containsKey("fechaDocumentoHasta"))
			listCriteria.add(Criteria.where("fechaDocumento").gte(parameters.get("fechaDocumentoDesde")).lte(parameters.get("fechaDocumentoHasta")));
		*/
		/*
		if(parameters.containsKey("fechaCreacionDesde")){
			listCriteria.add(Criteria.where("createdDate").gte(parameters.get("fechaCreacionDesde")));
			filtroParam.put("fechaCreacionDesde",formatter.format(parameters.get("fechaCreacionDesde")));
			parameters.remove("fechaCreacionDesde");
		}
		if(parameters.containsKey("fechaCreaciontoHasta")){
			listCriteria.add(Criteria.where("createdDate").lte(parameters.get("fechaCreaciontoHasta")));
			filtroParam.put("fechaCreaciontoHasta",formatter.format(parameters.get("fechaCreaciontoHasta")));
			parameters.remove("fechaCreaciontoHasta");
		}
		*/
		if(tramiteRequest.getFechaCreacionDesde()!=null){
			listCriteria.add(Criteria.where("createdDate").gte(tramiteRequest.getFechaCreacionDesde()));
			parameters.remove("fechaCreacionDesde");
		}


		if(tramiteRequest.getFechaCreaciontoHasta()!=null){
			Date fechaHasta = tramiteRequest.getFechaCreaciontoHasta();
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			//listCriteria.add(Criteria.where("fechaInicio").lte((Date)parameters.get("fechaDerivacionHasta")));
			listCriteria.add(Criteria.where("createdDate").lte(fechaHasta));
			parameters.remove("fechaCreaciontoHasta");
		}

		if(parameters.containsKey("asunto")){
			listCriteria.add(Criteria.where("asunto").regex(".*"+parameters.get("asunto")+".*","i"));
			parameters.remove("asunto");
		}
		if(parameters.containsKey("razonSocial")){
			listCriteria.add(Criteria.where("razonSocial").regex(".*"+parameters.get("razonSocial")+".*","i"));
			parameters.remove("razonSocial");
		}

		if(!StringUtils.isBlank(tramiteRequest.getMisTramite())){
			parameters.remove("misTramite");
			parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
			//listCriteria.add(Criteria.where("createdByUser").regex(".*"+securityHelper.obtenerUserIdSession()+".*"));
			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				parameters.put("dependenciaUsuarioCreacion.id",dependenciaIdUserSession);
			}
		}
		/*
		if(!StringUtils.isBlank(tramiteRequest.getCreatedByUser())){
			parameters.remove("createdByUser");
			//parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
			listCriteria.add(Criteria.where("createdByUser").regex(".*"+tramiteRequest.getCreatedByUser()+".*"));
		}
		*/

		if(!listCriteria.isEmpty())
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));

		if((Integer) parameters.get("numeroTramite")==0) {
			parameters.remove("numeroTramite");
		}

		filtroParam.putAll(parameters);
		//Agregamos la paginacion
		if(tramiteRequest.getPageNumber()>=0 && tramiteRequest.getPageSize()>0){
			Pageable pageable = PageRequest.of(tramiteRequest.getPageNumber(), tramiteRequest.getPageSize());
			andQuery.with(pageable);
		}
		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);

		//Criteria criteria = Criteria.where("createdByUser").regex(".*63326743b5ebc131b21522e1.*");
		//andQuery.addCriteria(criteria);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));


		/*
		if(andExpression.size()>1)
			andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		else
			andQuery.addCriteria(andExpression.get(0));
		*/


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

		if(StringUtils.isBlank(tramiteBodyRequest.getIdTramiteRelacionado()) && tramiteBodyRequest.isValidarTramiteRelacionado()){
			Map<String, Object> mapaRetorno = numeroDocumentoRepetido(tramiteBodyRequest);
			if(mapaRetorno!=null && !((Map)mapaRetorno.get("atributos")).get("idTramiteRelacionado").toString().equals(tramiteBodyRequest.getId()) ){
				throw new ServiceException((List<Mensaje>) mapaRetorno.get("errores"), (Map) mapaRetorno.get("atributos"));
			}
		}

		Date fechaDocumento = null;
		if(tramiteBodyRequest.getFechaDocumento()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteBodyRequest.getFechaDocumento();
			tramiteBodyRequest.setFechaDocumento(null);
			fechaDocumento =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}

		Tramite tramite = mapper.map(tramiteBodyRequest,Tramite.class);

		if(fechaDocumento!=null)
			tramite.setFechaDocumento(fechaDocumento);

		//List<Tramite> tramiteList = obtenerNumeroTramite();
		/*
		Tramite tramiteList = obtenerNumeroTramite();

		int numeroTramite = 1;

		if(!CollectionUtils.isEmpty(tramiteList))
			numeroTramite = obtenerNumeroTramite().get(0).getNumeroTramite()+1;
		tramite.setNumeroTramite(numeroTramite);
		*/
		tramite.setTramiteRelacionado(null);
		if(!StringUtils.isBlank(tramiteBodyRequest.getIdTramiteRelacionado())){
			Tramite tramiteRelacionado = new Tramite();
			tramiteRelacionado.setId(tramiteBodyRequest.getIdTramiteRelacionado());
			tramite.setTramiteRelacionado(tramiteRelacionado);
		}

		//Solo si es registro, genero un numero de tramite
		if(StringUtils.isBlank(tramite.getId()))
			tramite.setNumeroTramite(obtenerNumeroTramite());

		//tramite.setEstado("A");
		tramite.setEstado(EstadoTramiteConstant.REGISTRADO);
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			tramite.setCargoUsuarioCreacion(null);
			//Se setea la forma de recepcion siempre como digital
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));

			//Seteamos la razon social del usuario en el campo razon social del tramite
			try{
				String usuarioId = securityHelper.obtenerUserIdSession();
				UsuarioResponse usuarioResponse = obtenerUsuarioById(usuarioId);
				tramite.setRazonSocial(usuarioResponse.getPersona().getRazonSocialNombre());
			}catch (Exception ex){
				log.error("ERROR AL OBTENER RAZON SOCIAL",ex);
			}
		}else{
			if(tramiteBodyRequest.getOrigen().equals("INTERNO")){
				tramite.setEntidadExterna(null);
				tramite.setFormaRecepcion(null);
			}else{
				tramite.setEntidadInterna(null);
				tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
			}
			tramite.setDependenciaDestino(null);
			//Obtenemos la dependencia que llega en el header para registrar el tramite
			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				Dependencia dependencia = new Dependencia();
				dependencia.setId(dependenciaIdUserSession);
				tramite.setDependenciaUsuarioCreacion(dependencia);
			}
			//Obtenemos el cargo que llega en el header para registrar el tramite
			String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();
			if(!StringUtils.isBlank(cargoIdUserSession)){
				Cargo cargo = new Cargo();
				cargo.setId(cargoIdUserSession);
				tramite.setCargoUsuarioCreacion(cargo);
			}
		}

		//Si es modificacion
		if(!StringUtils.isBlank(tramite.getId())){
			Tramite tramiteTemporal = tramiteMongoRepository.findById(tramite.getId()).get();
			tramite.setCreatedByUser(tramiteTemporal.getCreatedByUser());
			tramite.setCreatedDate(tramiteTemporal.getCreatedDate());
			tramite.setNumeroTramite(tramiteTemporal.getNumeroTramite());
		}

		tramiteMongoRepository.save(tramite);
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			registrarDerivacion(tramite);
			Map param = generarReporteAcuseTramite(tramite);
			DocumentoAdjuntoResponse documentoAdjuntoResponse = registrarAcuseComoDocumentoDelTramite(param);
			param.put("documentoAdjuntoId",documentoAdjuntoResponse.getId());
			enviarAcuseTramite(param);
		}

		return tramite;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void registrarDerivacion(Tramite tramite) throws Exception {
		//Obtener 1er Usuario de Seguridad-UsuarioCargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuario-cargo/cargo/RECEPCION_MESA_PARTES";
		//String uri = env.getProperty("app.url.seguridad") + "/usuario-app-rol/MESA_PARTES";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

		TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest = new TramiteDerivacionBodyRequest();
		tramiteDerivacionBodyRequest.setSecuencia(1);
		tramiteDerivacionBodyRequest.setUsuarioInicio(tramite.getCreatedByUser());
		if(tramite.getDependenciaUsuarioCreacion()!=null)
			tramiteDerivacionBodyRequest.setDependenciaIdUsuarioInicio(tramite.getDependenciaUsuarioCreacion().getId());
		if(tramite.getCargoUsuarioCreacion()!=null)
			tramiteDerivacionBodyRequest.setCargoIdUsuarioInicio(tramite.getCargoUsuarioCreacion().getId());

		tramiteDerivacionBodyRequest.setUsuarioFin(((LinkedHashMap)((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("usuario")).get("id").toString());

		//UsuarioCargoResponse usuarioCargoResponse = mapper.map(response.getBody().getData(),UsuarioCargoResponse.class);
		//UsuarioCargoResponse usuarioCargoResponse = mapper.map(((List)response.getBody().getData()).get(0),UsuarioCargoResponse.class);
		//tramiteDerivacionBodyRequest.setDependenciaIdUsuarioFin(usuarioCargoResponse.getCargo().getDependencia().getId());
		//tramiteDerivacionBodyRequest.setCargoIdUsuarioFin(usuarioCargoResponse.getCargo().getId());

		CargoDTOResponse cargoResponse = mapper.map(((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("cargo"),CargoDTOResponse.class);

		tramiteDerivacionBodyRequest.setDependenciaIdUsuarioFin(cargoResponse.getDependencia().getId());
		tramiteDerivacionBodyRequest.setCargoIdUsuarioFin(cargoResponse.getId());
		tramiteDerivacionBodyRequest.setEstadoInicio("REGISTRADO");
		tramiteDerivacionBodyRequest.setFechaInicio(tramite.getCreatedDate());
		tramiteDerivacionBodyRequest.setTramiteId(tramite.getId());
		tramiteDerivacionBodyRequest.setComentarioInicio("Se inicia registro del Tramite");
		tramiteDerivacionBodyRequest.setForma("ORIGINAL");
		tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyRequest);
	}

	public int obtenerNumeroTramite(){
		Query query = new Query();
		//Criteria criteria = Criteria.where("estado").is("A");
		//query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("numeroTramite")
		));
		//List<Tramite> tramiteList = mongoTemplate.findOne(query, Tramite.class);
		Tramite tramite = mongoTemplate.findOne(query, Tramite.class);
		int numeroTramite = 1;
		if(tramite!=null){
			numeroTramite = tramite.getNumeroTramite()+1; //numeroTramite = obtenerNumeroTramite().get(0).getNumeroTramite()+1;
		}
		return numeroTramite;
	}

	public List<Tramite> buscarTramiteParamsByUsuarioId(String usuarioId, TramiteRequest tramiteRequest) throws Exception {
		/*
		Query query = new Query();
		Criteria criteria = Criteria.where("createdByUser").is(usuarioId);
		query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("createdDate")
		));
		List<Tramite> tramiteList = mongoTemplate.find(query, Tramite.class);
		*/

		tramiteRequest.setCreatedByUser(usuarioId);
		//tramiteRequest.setCreatedByUser("63326743b5ebc131b21522e1");
		//tramiteRequest.setEstado("RECEPCIONADO");
		List<Tramite> tramiteList = buscarTramiteParams(tramiteRequest);

		//Ordenamos por fecha de creacion, los mas recientes primero
		Collections.sort(tramiteList, new Comparator<Tramite>(){
			@Override
			public int compare(Tramite a, Tramite b)
			{
				return Long.compare(a.getCreatedDate().getTime(), b.getCreatedDate().getTime());
			}
		});

		return tramiteList;
	}

	public Tramite findById(String id){
		return tramiteMongoRepository.findById(id).get();
	}

	public Tramite save(Tramite tramite){
		return tramiteMongoRepository.save(tramite);
	}

	public Map numeroDocumentoRepetidoAnterior(TramiteBodyRequest tramiteBodyRequest) throws Exception {
		//Obtener persona del Usuario creador de Tramite
		//String usuarioId = securityHelper.obtenerUserIdSession();
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

		//List<TramiteReporteResponse> tramiteReporteResponseList = buscarTramiteParams(tramiteRequest);
		String soloOriginal = tramiteRequest.getSoloOriginal();
		List<Tramite> tramiteList = buscarTramiteParams(tramiteRequest);
		List<TramiteReporteResponse> tramiteReporteResponseList = new ArrayList<>();

		for(Tramite tramite : tramiteList){
			TramiteReporteResponse tramiteReporteResponse = mapper.map(tramite,TramiteReporteResponse.class);
			//tramiteReporteResponse.setTramiteDerivacion(tramiteDerivacionService.obtenerTramiteByTramiteId(tramite.getId()));
			tramiteReporteResponse.setTramiteDerivacion(obtenerTramiteDerivacionReporteResponse(tramiteDerivacionService.obtenerTramiteByTramiteId(tramite.getId()),soloOriginal));

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
			filtroParam.replace("createdByUser", user.getNombreCompleto());

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

		//Mostrar filtros Aplicados
		if(filtroParam.containsKey("fechaCreacionDesde") && filtroParam.containsKey("fechaCreaciontoHasta")){
			filtroParam.put("fechaCreacion",filtroParam.get("fechaCreacionDesde")+" - "+filtroParam.get("fechaCreaciontoHasta"));
			filtroParam.remove("fechaCreacionDesde");
			filtroParam.remove("fechaCreaciontoHasta");
		}

		for (Map.Entry<String, Object> entry : filtroParam.entrySet()) {
			parameters.put(entry.getKey(),entry.getValue());
		}

		JasperPrint print = JasperFillManager.fillReport(jasperReport,parameters,source);

		return print;
	}

	public Map generarReporteAcuseTramite(Tramite tramite) throws Exception {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream url = classloader.getResourceAsStream("acuseTramiteExterno.jrxml");

		JasperReport jasperReport = JasperCompileManager.compileReport(url);

		DateFormat Formato = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//String fechaGeneracion = Formato.format(tramite.getCreatedDate());
		String fechaGeneracion = Formato.format(determinarFechaGeneracion(tramite.getCreatedDate()));


		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		//Mapear Usuario y Persona
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/" + tramite.getCreatedByUser();
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<CommonResponse>() {
		});

		LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");

		Persona person = mapper.map(persona, Persona.class);
		usuario.replace("persona", person);
		Usuario user = mapper.map(usuario, Usuario.class);

		//Mapear Dependencia Destino
		String uriD = env.getProperty("app.url.seguridad") + "/dependencias/obtener-dependencia-by-id/" + tramite.getDependenciaDestino().getId();
		ResponseEntity<CommonResponse> responseD = restTemplate.exchange(uriD, HttpMethod.GET, entity, new ParameterizedTypeReference<CommonResponse>() {
		});

		LinkedHashMap<Object, Object> dependencia = (LinkedHashMap<Object, Object>) responseD.getBody().getData();

		//Obtener Tipo Documento de Tramite
		TipoDocumento tipoDocumento = tipoDocumentoMongoRepository.findById(tramite.getTipoDocumento().getId()).get();

		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("numeroTramite", tramite.getNumeroTramite());
		parameters.put("tipoDocumento", tipoDocumento.getTipoDocumento().toUpperCase());
		parameters.put("fechaGeneracion", fechaGeneracion);
		parameters.put("fechaHoraIngreso", fechaGeneracion);
		parameters.put("estado", "EN CUSTODIA ELECTRÓNICA POR AMSAC");
		parameters.put("emisorNombreCompleto", user.getNombreCompleto());
		parameters.put("emisorRazonSocial", user.getPersona().getRazonSocialNombre().toUpperCase());
		parameters.put("emisorRuc", user.getPersona().getNumeroDocumento());
		parameters.put("asunto", tramite.getAsunto());
		//TODO: pendiente conocer destino en registro de Trmaite
		parameters.put("destino", dependencia.get("nombre").toString().toUpperCase());

		List<String> lista = null;
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(lista);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters,source);

		//Directorio donde se guardará una copia fisica
		String nombreArchivoAcuse = "acuseRecibo-" + new SimpleDateFormat("ddMMyyyyHHmmssSSS").format(new Date()) + ".pdf";
		//final String reportPdf = env.getProperty("file.base-upload-dir") + File.separator + "acuse" + File.separator + "acuseRecibo.pdf";
		//final String reportPdf = env.getProperty("file.base-upload-dir") + File.separator + "acuse" + File.separator + nombreArchivoAcuse;

		String rutaAcuse = env.getProperty("file.base-upload-dir") + File.separator + "acuse";
		fileStorageService.createDirectory(rutaAcuse);

		final String reportPdf = rutaAcuse + File.separator + nombreArchivoAcuse;

		//Guardamos en el directorio
		JasperExportManager.exportReportToPdfFile(print, reportPdf);

		Map<String, Object> param = new HashMap<>();
		param.put("ruta",reportPdf);
		param.put("numeroTramite",tramite.getNumeroTramite());
		param.put("correo",user.getEmail());
		param.put("tramiteId",tramite.getId());
		param.put("nombreArchivo",nombreArchivoAcuse);

		return param;
	}

	public void enviarAcuseTramite(Map param) throws Exception {

		/*
		//Verificamos si hoy es feriado o si estamos fuera de horario de atención
		Integer fechaHoyEnEntero = Integer.getInteger(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		if(calendarioService.esFeriado(fechaHoyEnEntero) || !consiguracionService.estamosDentroHorarioDeAtencion() ){
			//Enviar acusede forma a futuro
			programarEnvioFuturoDeAcuseDeTramite(param);
			return;
		}
		*/

		enviarAcuseTramiteAhora(param);

		/*
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("to",param.get("correo").toString());
		//bodyMap.add("to","evelyn.flores@bitall.com.pe");
		bodyMap.add("asunto","Acuse de Recibo N° Tramite " + param.get("numeroTramite"));
		bodyMap.add("cuerpo","<h4>Estimado(a).</h4> </br> <p>Usted ha creado un tramite el cual hemos recibido de forma correcta, los detalles del trámite creado los puede ver en el documento adjunto.</p>");
		bodyMap.add("files", new FileSystemResource(param.get("ruta").toString()));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMailAttach";

		restTemplate.postForEntity( uri, requestEntity, null);
		*/

	}

	private void enviarAcuseTramiteAhora(Map param){
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("to",param.get("correo").toString());
		//bodyMap.add("to","evelyn.flores@bitall.com.pe");
		bodyMap.add("asunto","Acuse de Recibo N° Tramite " + param.get("numeroTramite"));
		bodyMap.add("cuerpo","<h4>Estimado(a).</h4> </br> <p>Usted ha creado un tramite el cual hemos recibido de forma correcta, los detalles del trámite creado los puede ver en el documento adjunto.</p>");
		bodyMap.add("files", new FileSystemResource(param.get("ruta").toString()));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMailAttach";

		restTemplate.postForEntity( uri, requestEntity, null);
	}

	private void programarEnvioFuturoDeAcuseDeTramite(Map param) throws Exception {
		//Encontrar el siguiente dia habil
		Date siguienteDiaHabil = calendarioService.obtenerSiguienteDiaHabil();
		String horaEnvioCorreoAcuseTramite = consiguracionService.obtenerConfiguracion().getHoraEnvioCorreoAcuseTramite();

		//Dia y hora de envio de acuse, en formato Date
		String fechaHoraEnvioAcuseTramiteCadena = new SimpleDateFormat("dd/MM/yyyy").format(siguienteDiaHabil).concat(" ").concat(horaEnvioCorreoAcuseTramite);
		Date fechaHoraEnvioAcuseTramiteDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHoraEnvioAcuseTramiteCadena);

		//Armar el cuerpo a enviar al scheduler
		Date startAt = fechaHoraEnvioAcuseTramiteDate;
		Date endAt = util.addMinuteToJavaUtilDate(startAt,5);

		//Creamos el evento
		EventSchedule eventSchedule = EventSchedule.builder()
				.headers(util.buildHeaders())
				.httpMethod(HttpMethod.POST.name())
				.resource(String.format(env.getProperty("app.url.enviarAcuseTramite"),param.get("tramiteId").toString()))
				.build();
		//TODO falta implementar
		log.info("programar a futuro lo siguiente:"+param);
		/*
		//Creamos el objeto para enviar a crear la tarea
		RequestSchedule requestSchedule = RequestSchedule.builder()
				.group("ENVIO_ACUSE_RECIBO_TRAMITE")
				.priority(10)
				.startAt(startAt)
				.endAt(endAt)
				//.cron("0 1/1 * * * ? *")
				.cron(util.createCron(startAt))
				.event(eventSchedule)
				.build();

		ObjectMapper mapper = new ObjectMapper();
		log.info("Schedule To Task -> "+mapper.writeValueAsString(requestSchedule));

		scheduleService.scheduleRegister(requestSchedule, HttpMethod.POST);
		*/


	}

	public List<TramiteResponse> buscarTramiteWithParams(TramiteRequest tramiteRequest) throws Exception {

		//Persona y Tipo Persona de Creador de cada tramite
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		//List<TramiteReporteResponse> tramiteReporteResponseList = buscarTramiteParams(tramiteRequest);
		//String soloOriginal = tramiteRequest.getSoloOriginal();
		List<Tramite> tramiteList = buscarTramiteParams(tramiteRequest);
		List<TramiteResponse> tramiteResponseList = new ArrayList<>();

		for(Tramite tramite : tramiteList){
			TramiteResponse tramiteResponse = mapper.map(tramite,TramiteResponse.class);
			//tramiteResponse.setTramiteDerivacion(tramiteDerivacionService.obtenerTramiteByTramiteId(tramite.getId()));
			//Se comenta esta parte porque no es necesario devolver el listado de derivaciones
			//tramiteResponse.setTramiteDerivacion(obtenerTramiteDerivacionReporteResponse(tramiteDerivacionService.obtenerTramiteByTramiteId(tramite.getId()),soloOriginal));

			log.info("Buscar usuario para usuario id:"+tramite.getCreatedByUser()+", tramite:"+tramiteResponse.getNumeroTramite());
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
			filtroParam.replace("createdByUser", user.getNombreCompleto());

			tramiteResponse.setUsuario(user.getNombreCompleto());
			tramiteResponse.setPersona(persona.getRazonSocialNombre());
			tramiteResponse.setCreatedDate(tramite.getCreatedDate());
			if(StringUtils.isBlank(tramiteResponse.getRazonSocial())){
				tramiteResponse.setRazonSocial(tramite.getEntidadExterna()!=null?tramite.getEntidadExterna().getRazonSocial():persona.getRazonSocialNombre());
			}
			tramiteResponseList.add(tramiteResponse);
		}

		return tramiteResponseList;
	}

	private List<TramiteDerivacionReporteResponse> obtenerTramiteDerivacionReporteResponse(List<TramiteDerivacionReporteResponse> tramiteDerivacionReporteResponseList, String soloOriginal){
		if(StringUtils.isBlank(soloOriginal) || soloOriginal.equals("N")){
			return tramiteDerivacionReporteResponseList;
		}
		return tramiteDerivacionReporteResponseList.stream().filter(x -> x.getForma().equals("ORIGINAL")).collect(Collectors.toList());
	}

	private DocumentoAdjuntoResponse registrarAcuseComoDocumentoDelTramite(Map param) throws Exception {

		Path path = Paths.get(param.get("ruta").toString());
		byte[] archivoAcuseByteArray = Files.readAllBytes(path);
		CustomMultipartFile file = new CustomMultipartFile(archivoAcuseByteArray,param.get("nombreArchivo").toString(),"application/pdf");

		DocumentoAdjuntoBodyRequest documentoAdjuntoRequest = new DocumentoAdjuntoBodyRequest();
		documentoAdjuntoRequest.setTramiteId(param.get("tramiteId").toString());
		documentoAdjuntoRequest.setDescripcion("ACUSE de RECIBO");
		documentoAdjuntoRequest.setFile(file);
		documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.ACUSE_RECIBO_TRAMITE_AMSAC);

		return documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoRequest);
	}

	public void enviarAcusePorTramiteId(String tramiteId) throws Exception {

		//Obtenemos el tramite
		Tramite tramite = findById(tramiteId);
		//El email de usuario
		Usuario usuario =  usuarioService.obtenerUsuarioById(tramite.getCreatedByUser());
		String emailDestino = usuario.getEmail();

		//Obtener el documento adjunto acuse
		DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
		documentoAdjuntoRequest.setTramiteId(tramiteId);
		documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.ACUSE_RECIBO_TRAMITE_AMSAC);
		DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.obtenerDocumentoAdjuntoList(documentoAdjuntoRequest).get(0);

		//Obtenemos el resource del acuse a enviar
		documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
		documentoAdjuntoRequest.setId(documentoAdjuntoResponse.getId());
		Resource resource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("to",emailDestino);
		//bodyMap.add("to","evelyn.flores@bitall.com.pe");
		bodyMap.add("asunto",env.getProperty("app.field.asuntoAcuseRecepcionTramite") + tramite.getNumeroTramite());
		bodyMap.add("cuerpo","<h4>Estimado(a).</h4> </br> <p>Usted ha creado un tramite el cual hemos recibido de forma correcta, los detalles del trámite creado los puede ver en el documento adjunto.</p>");
		bodyMap.add("files", resource); //new FileSystemResource(param.get("ruta").toString()));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMailAttach";

		restTemplate.postForEntity( uri, requestEntity, null);
	}

	private Date determinarFechaGeneracion(Date fechaCreacionTramite) throws Exception {
		Date fechaCreacionTramiteFinal = fechaCreacionTramite;

		Integer fechaHoyEnEntero = Integer.getInteger(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		if(calendarioService.esFeriado(fechaHoyEnEntero) || !consiguracionService.estamosDentroHorarioDeAtencion() ){
			fechaCreacionTramiteFinal = determinarSiguienteDiaYHoraHabilParaRecepcionarTramite();
		}

		return fechaCreacionTramiteFinal;

	}

	private Date determinarSiguienteDiaYHoraHabilParaRecepcionarTramite() throws Exception {
		//Encontrar el siguiente dia habil
		Date siguienteDiaHabil = calendarioService.obtenerSiguienteDiaHabil();
		String horaEnvioCorreoAcuseTramite = consiguracionService.obtenerConfiguracion().getHoraEnvioCorreoAcuseTramite();

		//Dia y hora de envio de acuse, en formato Date
		String fechaHoraEnvioAcuseTramiteCadena = new SimpleDateFormat("dd/MM/yyyy").format(siguienteDiaHabil).concat(" ").concat(horaEnvioCorreoAcuseTramite);
		Date fechaHoraEnvioAcuseTramiteDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHoraEnvioAcuseTramiteCadena);

		return fechaHoraEnvioAcuseTramiteDate;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteMigracion registrarTramiteMigracion(TramiteMigracionBodyRequest tramiteBodyRequest) throws Exception {

		/*
		if(StringUtils.isBlank(tramiteBodyRequest.getIdTramiteRelacionado())){
			Map<String, Object> mapaRetorno = numeroDocumentoRepetido(tramiteBodyRequest);
			if(mapaRetorno!=null){
				throw new ServiceException((List<Mensaje>) mapaRetorno.get("errores"), (Map) mapaRetorno.get("atributos"));
			}
		}
		*/

		Date fechaDocumento = null;
		if(tramiteBodyRequest.getFechaDocumento()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteBodyRequest.getFechaDocumento();
			tramiteBodyRequest.setFechaDocumento(null);
			fechaDocumento =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}

		TramiteMigracion tramite = mapper.map(tramiteBodyRequest,TramiteMigracion.class);

		if(fechaDocumento!=null)
			tramite.setFechaDocumento(fechaDocumento);

		tramite.setTramiteRelacionado(null);
		/*
		List<Tramite> tramiteList = obtenerNumeroTramite();

		int numeroTramite = 1;

		if(!CollectionUtils.isEmpty(tramiteList))
			numeroTramite = obtenerNumeroTramite().get(0).getNumeroTramite()+1;

		tramite.setNumeroTramite(numeroTramite);
		*/
		//tramite.setEstado("A");
		tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			//tramite.setCargoUsuarioCreacion(null); No va porque no registran cargo
			//Se setea la forma de recepcion siempre como digital
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));
		}else{
			if(tramiteBodyRequest.getOrigen().equals("INTERNO")){
				tramite.setEntidadExterna(null);
				tramite.setFormaRecepcion(null);
			}else{
				tramite.setEntidadInterna(null);
			}
			tramite.setDependenciaDestino(null);

		}
		tramiteMigracionMongoRepository.save(tramite);
		/*
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			registrarDerivacion(tramite);
			Map param = generarReporteAcuseTramite(tramite);
			DocumentoAdjuntoResponse documentoAdjuntoResponse = registrarAcuseComoDocumentoDelTramite(param);
			param.put("documentoAdjuntoId",documentoAdjuntoResponse.getId());
			enviarAcuseTramite(param);

		}
		*/

		return tramite;

	}

	public void actualizarEstadoTramite(String tramiteId, String estadoTramite) throws Exception {
		boolean actualizoEstado = true;
		if(estadoTramite.equals(EstadoTramiteConstant.ATENDIDO)){
			//Si voy a colocar el tramite como atendido verifico que no haya ningun ORIGNAL pendiente de atención.
			Predicate<TramiteDerivacion> predicate = x -> x.getForma().equals("ORIGINAL") && x.getEstado().equals("P");
			List<TramiteDerivacion> tramiteDerivacionList = tramiteDerivacionService.obtenerTramiteDerivacionByTramiteId(tramiteId);
			actualizoEstado = tramiteDerivacionList.stream().filter(predicate).collect(Collectors.toList()).size()>0?false:true;
		}
		if(actualizoEstado){
			Tramite tramite = tramiteMongoRepository.findById(tramiteId).get();
			tramite.setEstado(estadoTramite);
			tramiteMongoRepository.save(tramite);
		}
	}

	public int totalRegistros(TramiteRequest tramiteRequest) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		List<Criteria> listCriteria =  new ArrayList<>();
		if(parameters.containsKey("soloOriginal")){
			parameters.remove("soloOriginal");
		}
		/*if(parameters.containsKey("fechaDocumentoDesde") && parameters.containsKey("fechaDocumentoHasta"))
			listCriteria.add(Criteria.where("fechaDocumento").gte(parameters.get("fechaDocumentoDesde")).lte(parameters.get("fechaDocumentoHasta")));
		*/
		if(parameters.containsKey("fechaCreacionDesde")){
			listCriteria.add(Criteria.where("createdDate").gte(parameters.get("fechaCreacionDesde")));
			filtroParam.put("fechaCreacionDesde",formatter.format(parameters.get("fechaCreacionDesde")));
			parameters.remove("fechaCreacionDesde");
		}
		if(parameters.containsKey("fechaCreaciontoHasta")){
			listCriteria.add(Criteria.where("createdDate").lte(parameters.get("fechaCreaciontoHasta")));
			filtroParam.put("fechaCreaciontoHasta",formatter.format(parameters.get("fechaCreaciontoHasta")));
			parameters.remove("fechaCreaciontoHasta");
		}
		if(parameters.containsKey("asunto")){
			listCriteria.add(Criteria.where("asunto").regex(".*"+parameters.get("asunto")+".*"));
			parameters.remove("asunto");
		}

		if(!listCriteria.isEmpty())
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));

		if((Integer) parameters.get("numeroTramite")==0) {
			parameters.remove("numeroTramite");
		}
		if(!StringUtils.isBlank(tramiteRequest.getMisTramite())){
			parameters.remove("misTramite");
			parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				parameters.put("dependenciaUsuarioCreacion.id",dependenciaIdUserSession);
			}
		}
		filtroParam.putAll(parameters);
		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

		long cantidadRegistro = mongoTemplate.count(andQuery, Tramite.class);

		return (int)cantidadRegistro;
	}

	public void actualizarDependenciaUsuarioCreacionTramite(String tramiteId, String dependenciaCreacionTramiteId){
		Tramite tramite = tramiteMongoRepository.findById(tramiteId).get();
		Dependencia dependencia = new Dependencia();
		dependencia.setId(dependenciaCreacionTramiteId);
		tramite.setDependenciaUsuarioCreacion(dependencia);
		tramiteMongoRepository.save(tramite);
	}

	public Map obtenerIndicadoresDashboardByTokenUsuario(){
		String usuarioId = securityHelper.obtenerUserIdSession();
		String dependenciaId = securityHelper.obtenerDependenciaIdUserSession();
		return obtenerIndicadoresDashboardByUsuarioIdAndDependencia(usuarioId, dependenciaId);
	}

	public Map obtenerIndicadoresDashboardByUsuarioIdAndDependencia(String usuarioId, String dependenciaId) {

		Map mapaRespuesta = new HashMap();
		try{
			//Obtenemos los tramites creados por el usuario
			int cantidadTramitesGeneradosPorUsuarioYDependencia = cantidadTramitesGeneradosByUsuarioAndDependencia(usuarioId,dependenciaId);

			//Obtener tramites pendientes por usuario y dependencia
			int cantidadTramitesPendientesPorUsuarioYDependencia = cantidadTramitesPendientesByUsuarioAndDependencia(usuarioId,dependenciaId);

			//Obtener tramites atendidos por usuario y dependencia
			int cantidadTramitesAtendidosPorUsuarioYDependencia = cantidadTramitesAtendidosByUsuarioAndDependencia(usuarioId,dependenciaId);

			mapaRespuesta.put("cantidadTramitesGeneradosPorUsuarioYDependencia",cantidadTramitesGeneradosPorUsuarioYDependencia);
			mapaRespuesta.put("cantidadTramitesPendientesPorUsuarioYDependencia",cantidadTramitesPendientesPorUsuarioYDependencia);
			mapaRespuesta.put("cantidadTramitesAtendidosPorUsuarioYDependencia",cantidadTramitesAtendidosPorUsuarioYDependencia);

		}catch (Exception ex){
			mapaRespuesta.put("cantidadTramitesGeneradosPorUsuarioYDependencia",0);
			mapaRespuesta.put("cantidadTramitesPendientesPorUsuarioYDependencia",0);
			mapaRespuesta.put("cantidadTramitesAtendidosPorUsuarioYDependencia",0);
			log.error("ERROR",ex);
		}

		return mapaRespuesta;
	}

	public int cantidadTramitesGeneradosByUsuarioAndDependencia(String usuarioId, String dependenciaId){

		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = new HashMap<>();
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();

		parameters.put("createdByUser",usuarioId);
		if(!StringUtils.isBlank(dependenciaId)){
			parameters.put("dependenciaUsuarioCreacion.id",dependenciaId);
		}

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

		long cantidadRegistro = mongoTemplate.count(andQuery, Tramite.class);

		return (int)cantidadRegistro;
	}

	public int cantidadTramitesPendientesByUsuarioAndDependencia(String usuarioId, String dependenciaId) throws Exception {

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaId);
		tramiteDerivacionRequest.setUsuarioFin(usuarioId);
		tramiteDerivacionRequest.setEstado("P");

		return tramiteDerivacionService.totalRegistros(tramiteDerivacionRequest);
	}

	public int cantidadTramitesAtendidosByUsuarioAndDependencia(String usuarioId, String dependenciaId) throws Exception {

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaId);
		tramiteDerivacionRequest.setUsuarioFin(usuarioId);
		tramiteDerivacionRequest.setEstado("A");
		//tramiteDerivacionRequest.setNotEstadoFin(EstadoTramiteConstant.RECEPCIONADO);

		return tramiteDerivacionService.totalRegistros(tramiteDerivacionRequest);
	}

	public Map numeroDocumentoRepetido(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		//Buscamos un tramite con el mismo numero de documento
		Map<String, Object> param = new HashMap<>();
		param.put("numeroDocumento",tramiteBodyRequest.getNumeroDocumento());

		List<Tramite> tramiteList = buscarHistorialTramite(param);

		Collections.sort(tramiteList, new Comparator<Tramite>(){
			@Override
			public int compare(Tramite o1, Tramite o2) {
				return o1.getCreatedDate().compareTo(o2.getCreatedDate());
			}
		});

		Map<String, Object> mapRetorno = null;

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

	private UsuarioResponse obtenerUsuarioById(String usuarioId){

		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+usuarioId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET,entity, CommonResponse.class);
		ObjectMapper objectMapper = new ObjectMapper();
		UsuarioResponse usuarioResponse = objectMapper.convertValue(response.getBody().getData(),new TypeReference<UsuarioResponse>() {});

		//ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		return usuarioResponse;
	}

}
