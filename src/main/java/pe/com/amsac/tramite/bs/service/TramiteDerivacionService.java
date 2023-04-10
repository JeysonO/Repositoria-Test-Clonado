package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.util.EstadoTramiteConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Slf4j
public class TramiteDerivacionService {

	@Autowired
	private TramiteDerivacionMongoRepository tramiteDerivacionMongoRepository;

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private Mapper mapper;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private TramiteService tramiteService;

	@Autowired
	private Environment env;

	public List<TramiteDerivacion> obtenerTramiteDerivacionByTramiteId(String tramiteId) throws Exception {
		//Obtener Usuario
		/*
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		DatosToken datosToken = (DatosToken)authentication.getPrincipal();

		String idUser =  datosToken.getIdUser();
		*/
		//String idUser =  securityHelper.obtenerUserIdSession();

		Query query = new Query();
		Criteria criteria = Criteria.where("tramite.id").is(tramiteId);
		query.addCriteria(criteria);
		List<TramiteDerivacion> tramitePendienteList = mongoTemplate.find(query, TramiteDerivacion.class);

		//Por cada usuario origen y fin, obtener la dependencia y cargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = null;
		String uriBusqueda;
		String nombreCompleto;
		for(TramiteDerivacion tramiteDerivacion : tramitePendienteList){
			//Se completan datos de usuario inicio
			uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
			Persona personaDto = mapper.map(persona,Persona.class);

			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());

			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);

			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			persona = (LinkedHashMap<String, String>) usuario.get("persona");
			personaDto = mapper.map(persona,Persona.class);

			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);

		}

		return tramitePendienteList;
	}

	public List<TramiteDerivacion> obtenerTramiteDerivacionPendientes(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		/*
		String idUser =  securityHelper.obtenerUserIdSession();

		Query query = new Query();
		Criteria criteria = Criteria.where("usuarioFin.id").is(idUser).and("estado").is("P");
		query.addCriteria(criteria);
		List<TramiteDerivacion> tramitePendienteList = mongoTemplate.find(query, TramiteDerivacion.class);
		*/


		String idUser =  securityHelper.obtenerUserIdSession();
		tramiteDerivacionRequest.setUsuarioFin(idUser);

		List<TramiteDerivacion> tramitePendienteList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);

		if(CollectionUtils.isEmpty(tramitePendienteList))
			return tramitePendienteList;

		//Ordenamos por numero de tramite
		Collections.sort(tramitePendienteList, new Comparator<TramiteDerivacion>(){
			@Override
			public int compare(TramiteDerivacion a, TramiteDerivacion b)
			{
				return b.getTramite().getNumeroTramite() - a.getTramite().getNumeroTramite();
			}
		});

		/*
		//Por cada usuario origen y fin, obtener la dependencia y cargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = null;
		String uriBusqueda;
		String nombreCompleto;
		for(TramiteDerivacion tramiteDerivacion : tramitePendienteList){
			//Se completan datos de usuario inicio
			uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);

			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);

		}
		*/

		return tramitePendienteList;
	}

	public TramiteDerivacion obtenerTramiteDerivacionById(String id) throws Exception {
		TramiteDerivacion obtenerTramiteDerivacionById = tramiteDerivacionMongoRepository.findById(id).get();
		return obtenerTramiteDerivacionById;
	}

	public List<TramiteDerivacion> buscarTramiteDerivacionParams(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		Criteria orCriteria = new Criteria();
		Criteria criteriaOr = null;
		Criteria criteriaGlobal = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		List<Criteria> orExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		if(parameters.get("numeroTramite").equals(0)){
			parameters.remove("numeroTramite");
		}
		List<Criteria> listCriteria =  new ArrayList<>();
		List<Criteria> listOrCriteria =  new ArrayList<>();

		List<String> tramiteIds = obtenerTramitesId(tramiteDerivacionRequest);
		if(!CollectionUtils.isEmpty(tramiteIds)){
			listOrCriteria.add(Criteria.where("tramite.id").in(tramiteIds.toArray()));
			parameters.remove("tramiteId");
			parameters.remove("numeroTramite");
			parameters.remove("asunto");

			for (String tramiteId : tramiteIds) {
				Criteria expression = new Criteria();
				expression.and("tramite.id").is(tramiteId);
				orExpression.add(expression);
			}
			//orQuery.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}

		if(parameters.containsKey("fechaDerivacionDesde") && parameters.containsKey("fechaDerivacionHasta")){
			listCriteria.add(Criteria.where("fechaInicio").gte(parameters.get("fechaDerivacionDesde")).lte(parameters.get("fechaDerivacionHasta")));
			parameters.remove("fechaDerivacionDesde");
		}
		if(parameters.containsKey("usuarioInicio")){
			listCriteria.add(Criteria.where("usuarioInicio.id").is(parameters.get("usuarioInicio")));
			parameters.remove("fechaDerivacionDesde");
		}
		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}
		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}

		//Agregamos la paginacion
		if(tramiteDerivacionRequest.getPageNumber()>=0 && tramiteDerivacionRequest.getPageSize()>0){
			Pageable pageable = PageRequest.of(tramiteDerivacionRequest.getPageNumber(), tramiteDerivacionRequest.getPageSize());
			andQuery.with(pageable);
		}

		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);

		//andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

		if(!CollectionUtils.isEmpty(listOrCriteria))
			criteriaOr = orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()]));

		Criteria criteriaAnd = andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()]));

		if(criteriaOr!=null)
			criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd,criteriaOr);
		else
			criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd);

		andQuery.addCriteria(criteriaGlobal);

		List<TramiteDerivacion> tramiteList = mongoTemplate.find(andQuery, TramiteDerivacion.class);

		//Por cada usuario origen y fin, obtener la dependencia y cargo
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = null;
		String uriBusqueda;
		String nombreCompleto;

		//Se obtiene datos del tramite
		String usuarioCreacion = null;
		String dependenciaEmpresa = null;
		if(!CollectionUtils.isEmpty(tramiteList)){
			uriBusqueda = uri + tramiteList.get(0).getTramite().getCreatedByUser();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			usuarioCreacion = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");

			if(tramiteList.get(0).getTramite().getOrigenDocumento().equals("EXTERNO")){
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);
				dependenciaEmpresa = personaDto.getRazonSocialNombre();
			}else{
				dependenciaEmpresa = ((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString();
			}
		}


		for(TramiteDerivacion tramiteDerivacion : tramiteList){
			tramiteDerivacion.setUsuarioCreacion(usuarioCreacion);
			tramiteDerivacion.setDependenciaEmpresa(dependenciaEmpresa);

			//Se completan datos de usuario inicio
			uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
			Persona personaDto = mapper.map(persona,Persona.class);

			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());

			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);

			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			persona = (LinkedHashMap<String, String>) usuario.get("persona");
			personaDto = mapper.map(persona,Persona.class);

			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);

		}

		return tramiteList;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrar(TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		/*
		//Obtener Usuario Inicio
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioInicio();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
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
		Usuario userInicio = mapper.map(usuario,Usuario.class);

		//Mapear Usuario Fin
		uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioFin();
		response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		personaL = (LinkedHashMap<String, String>) usuario.get("persona");
		personaL.remove("createdDate");
		personaL.remove("lastModifiedDate");
		personaL.remove("tipoDocumento");
		personaL.remove("entityId");
		persona = mapper.map(personaL,Persona.class);
		usuario.replace("persona",persona);
		Usuario userFin = mapper.map(usuario,Usuario.class);

		Date fechaMaxima = null;
		if(tramiteDerivacionBodyRequest.getFechaMaximaAtencion()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteDerivacionBodyRequest.getFechaMaximaAtencion();
			tramiteDerivacionBodyRequest.setFechaMaximaAtencion(null);
			fechaMaxima =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}

		TramiteDerivacion registroTramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		if(fechaMaxima!=null)
			registroTramiteDerivacion.setFechaMaximaAtencion(fechaMaxima);
		registroTramiteDerivacion.setUsuarioInicio(userInicio);
		registroTramiteDerivacion.setUsuarioFin(userFin);
		registroTramiteDerivacion.setFechaInicio(new Date());
		registroTramiteDerivacion.setTramite(tramiteMongoRepository.findById(tramiteDerivacionBodyRequest.getTramiteId()).get());
		registroTramiteDerivacion.setEstado("P");

		int sec = obtenerSecuencia(tramiteDerivacionBodyRequest.getTramiteId());
		registroTramiteDerivacion.setSecuencia(sec);

		tramiteDerivacionMongoRepository.save(registroTramiteDerivacion);
		*/
		//El primer estado inicio hacia otro usuario es DERIVADO
		if(StringUtils.isBlank(tramiteDerivacionBodyRequest.getEstadoInicio())){
			tramiteDerivacionBodyRequest.setEstadoInicio(EstadoTramiteConstant.DERIVADO);
		}
		TramiteDerivacion registroTramiteDerivacion = registrarTramiteDerivacion(tramiteDerivacionBodyRequest);

		tramiteService.actualizarEstadoTramite(registroTramiteDerivacion.getTramite().getId(),registroTramiteDerivacion.getEstadoInicio());

		//Invocar a servicio para envio de correo
		//Solo se envia si es diferente de estadoInicio = SUBSANACION
		//if(registroTramiteDerivacion.getEstadoInicio()!=null && !registroTramiteDerivacion.getEstadoInicio().equals("SUBSANACION"))
		envioCorreoDerivacion(registroTramiteDerivacion);

		return registroTramiteDerivacion;

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion subsanarTramiteDerivacion(SubsanacionTramiteDerivacionBodyRequest subsanartramiteDerivacionBodyrequest) throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();

		TramiteDerivacion subsanarTramiteActual = tramiteDerivacionMongoRepository.findById(subsanartramiteDerivacionBodyrequest.getId()).get();
		subsanarTramiteActual.setComentarioFin(subsanartramiteDerivacionBodyrequest.getComentarioInicial());
		subsanarTramiteActual.setEstadoFin(EstadoTramiteConstant.SUBSANACION);
		subsanarTramiteActual.setFechaFin(new Date());
		subsanarTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(subsanarTramiteActual);

		int sec = obtenerSecuencia(subsanarTramiteActual.getTramite().getId());

		LocalDate fechaMaxima = null;
		if(subsanarTramiteActual.getFechaMaximaAtencion()!=null){
			fechaMaxima = subsanarTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			subsanarTramiteActual.setFechaMaximaAtencion(null);
		}
		TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = mapper.map(subsanarTramiteActual, TramiteDerivacionBodyRequest.class);
		subsanarTramiteBodyRequest.setSecuencia(sec);
		subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
		subsanarTramiteBodyRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioInicio().getId());
		subsanarTramiteBodyRequest.setComentarioInicio(subsanarTramiteActual.getComentarioFin());
		subsanarTramiteBodyRequest.setEstadoInicio(subsanarTramiteActual.getEstadoFin());
		subsanarTramiteBodyRequest.setFechaInicio(new Date());
		subsanarTramiteBodyRequest.setForma("ORIGINAL");
		if(fechaMaxima!=null)
			subsanarTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);
		subsanarTramiteBodyRequest.setId(null);
		subsanarTramiteBodyRequest.setEstadoFin(null);
		subsanarTramiteBodyRequest.setFechaFin(null);
		subsanarTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(subsanarTramiteBodyRequest);

		//Enviar correo para subsanacion
		envioCorreoSubsanacion(nuevoDerivacionTramite);

		//Actualizamos el estado a nivel de tramite
		tramiteService.actualizarEstadoTramite(subsanarTramiteActual.getTramite().getId(),EstadoTramiteConstant.SUBSANACION);

		return nuevoDerivacionTramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarDerivacionTramite(DerivarTramiteBodyRequest derivartramiteBodyrequest) throws Exception {

		TramiteDerivacion derivacionTramiteActual = null;
		if(derivartramiteBodyrequest.getEnConocimientoAtendido()!=null && derivartramiteBodyrequest.getEnConocimientoAtendido().equals("S")){
			AtencionTramiteDerivacionBodyRequest atencionTramiteDerivacionBodyRequest = new AtencionTramiteDerivacionBodyRequest();
			atencionTramiteDerivacionBodyRequest.setId(derivartramiteBodyrequest.getId());
			atencionTramiteDerivacionBodyRequest.setEstadoFin(EstadoTramiteConstant.ATENDIDO);
			atencionTramiteDerivacionBodyRequest.setComentarioFin("CONOCIMIENTO ATENDIDO");
			derivacionTramiteActual = registrarAtencionTramiteDerivacion(atencionTramiteDerivacionBodyRequest);
			derivacionTramiteActual.setForma("COPIA");
		}else{
			derivacionTramiteActual = tramiteDerivacionMongoRepository.findById(derivartramiteBodyrequest.getId()).get();
			ZoneId defaultZoneId = ZoneId.systemDefault();
			derivacionTramiteActual.setEstadoFin(EstadoTramiteConstant.DERIVADO);
			derivacionTramiteActual.setFechaFin(new Date());
			derivacionTramiteActual.setProveidoAtencion(derivartramiteBodyrequest.getProveidoAtencion());
			derivacionTramiteActual.setComentarioFin(derivartramiteBodyrequest.getComentarioFin());
			derivacionTramiteActual.setFechaMaximaAtencion(Date.from(derivartramiteBodyrequest.getFechaMaximaAtencion().atStartOfDay(defaultZoneId).toInstant()));
			derivacionTramiteActual.setEstado("A");
			tramiteDerivacionMongoRepository.save(derivacionTramiteActual);

			//Actualizamos el estado a nivel de tramite
			tramiteService.actualizarEstadoTramite(derivacionTramiteActual.getTramite().getId(),EstadoTramiteConstant.DERIVADO);
		}

		//Asignar valores manualmente segun condiciones
		int sec = obtenerSecuencia(derivacionTramiteActual.getTramite().getId());
		String usuarioId = securityHelper.obtenerUserIdSession();
		LocalDate fechaMaxima = null;
		if(derivacionTramiteActual.getFechaMaximaAtencion()!=null){
			fechaMaxima = derivacionTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			derivacionTramiteActual.setFechaMaximaAtencion(null);
		}

		//Crear nuevo tramite
		TramiteDerivacionBodyRequest derivacionTramiteBodyRequest = mapper.map(derivacionTramiteActual, TramiteDerivacionBodyRequest.class);
		derivacionTramiteBodyRequest.setSecuencia(sec);
		derivacionTramiteBodyRequest.setUsuarioInicio(usuarioId);
		derivacionTramiteBodyRequest.setUsuarioFin(derivartramiteBodyrequest.getUsuarioFin());
		derivacionTramiteBodyRequest.setEstadoInicio(EstadoTramiteConstant.DERIVADO);
		derivacionTramiteBodyRequest.setFechaInicio(new Date());
		if(fechaMaxima!=null)
			derivacionTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);
		//TODO: PENDIENTE DATO COMENTARIO INICIO
		derivacionTramiteBodyRequest.setComentarioInicio(derivacionTramiteActual.getComentarioFin());
		derivacionTramiteBodyRequest.setId(null);
		derivacionTramiteBodyRequest.setEstadoFin(null);
		derivacionTramiteBodyRequest.setFechaFin(null);
		derivacionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(derivacionTramiteBodyRequest);

		envioCorreoDerivacion(nuevoDerivacionTramite);

		return nuevoDerivacionTramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarRecepcionTramiteDerivacion(String id) throws Exception {

		TramiteDerivacion recepcionTramiteActual = tramiteDerivacionMongoRepository.findById(id).get();
		recepcionTramiteActual.setEstadoFin(EstadoTramiteConstant.RECEPCIONADO);
		recepcionTramiteActual.setFechaFin(new Date());
		recepcionTramiteActual.setComentarioFin("Se recepciona tramite para verificacion");
		recepcionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(recepcionTramiteActual);

		int sec = obtenerSecuencia(recepcionTramiteActual.getTramite().getId());

		LocalDate fechaMaxima = null;
		if(recepcionTramiteActual.getFechaMaximaAtencion()!=null){
			fechaMaxima = recepcionTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			recepcionTramiteActual.setFechaMaximaAtencion(null);
		}

		TramiteDerivacionBodyRequest recepcionTramiteBodyRequest = mapper.map(recepcionTramiteActual, TramiteDerivacionBodyRequest.class);
		recepcionTramiteBodyRequest.setSecuencia(sec);
		recepcionTramiteBodyRequest.setUsuarioInicio(recepcionTramiteActual.getUsuarioFin().getId());
		recepcionTramiteBodyRequest.setUsuarioFin(recepcionTramiteActual.getUsuarioFin().getId());
		recepcionTramiteBodyRequest.setEstadoInicio(recepcionTramiteActual.getEstadoFin());
		recepcionTramiteBodyRequest.setFechaInicio(new Date());
		recepcionTramiteBodyRequest.setComentarioInicio(recepcionTramiteActual.getComentarioFin());
		if(fechaMaxima!=null)
			recepcionTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);
		recepcionTramiteBodyRequest.setId(null);
		recepcionTramiteBodyRequest.setEstadoFin(null);
		recepcionTramiteBodyRequest.setFechaFin(null);
		recepcionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoRecepcionTramite = registrarTramiteDerivacion(recepcionTramiteBodyRequest);

		tramiteService.actualizarEstadoTramite(recepcionTramiteActual.getTramite().getId(),recepcionTramiteActual.getEstadoFin());

		return nuevoRecepcionTramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarAtencionTramiteDerivacion(AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();

		TramiteDerivacion atenderTramiteDerivacion = tramiteDerivacionMongoRepository.findById(atenciontramiteDerivacionBodyrequest.getId()).get();
		atenderTramiteDerivacion.setEstadoFin(atenciontramiteDerivacionBodyrequest.getEstadoFin());
		atenderTramiteDerivacion.setFechaFin(new Date());
		atenderTramiteDerivacion.setComentarioFin(atenciontramiteDerivacionBodyrequest.getComentarioFin());
		atenderTramiteDerivacion.setEstado("A");
		tramiteDerivacionMongoRepository.save(atenderTramiteDerivacion);

		String idTramite = atenderTramiteDerivacion.getTramite().getId();

		LocalDate fechaMaxima = null;
		if(atenderTramiteDerivacion.getFechaMaximaAtencion()!=null){
			fechaMaxima = atenderTramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			atenderTramiteDerivacion.setFechaMaximaAtencion(null);
		}

		if(atenciontramiteDerivacionBodyrequest.getEstadoFin().equals(EstadoTramiteConstant.SUBSANADO)){
			int sec = obtenerSecuencia(atenderTramiteDerivacion.getTramite().getId());

			TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = mapper.map(atenderTramiteDerivacion, TramiteDerivacionBodyRequest.class);
			subsanarTramiteBodyRequest.setSecuencia(sec);
			subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
			subsanarTramiteBodyRequest.setUsuarioFin(atenderTramiteDerivacion.getUsuarioInicio().getId());
			subsanarTramiteBodyRequest.setComentarioInicio(atenciontramiteDerivacionBodyrequest.getComentarioFin());
			subsanarTramiteBodyRequest.setEstadoInicio(atenderTramiteDerivacion.getEstadoFin());
			subsanarTramiteBodyRequest.setFechaInicio(new Date());
			subsanarTramiteBodyRequest.setForma("ORIGINAL");
			if(fechaMaxima!=null)
				subsanarTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);
			subsanarTramiteBodyRequest.setId(null);
			subsanarTramiteBodyRequest.setEstadoFin(null);
			subsanarTramiteBodyRequest.setFechaFin(null);
			subsanarTramiteBodyRequest.setComentarioFin(null);

			atenderTramiteDerivacion = registrarTramiteDerivacion(subsanarTramiteBodyRequest);

			//TODO enviar correo de que el tramite ha sido atendido
		}

		//Ahora se actualiza el estado del tramite
		tramiteService.actualizarEstadoTramite(idTramite, atenciontramiteDerivacionBodyrequest.getEstadoFin());
		/*
		Tramite tramite = tramiteService.findById(idTramite);
		tramite.setEstado(atenciontramiteDerivacionBodyrequest.getEstadoFin());
		tramiteService.save(tramite);
		*/

		return atenderTramiteDerivacion;
	}

	public int obtenerSecuencia(String id){
		int secuencia = 1;
		Query query = new Query();
		Criteria criteria = Criteria.where("tramite.id").is(id);
		query.addCriteria(criteria);
		query.with(Sort.by(
				Sort.Order.desc("secuencia")
		));
		List<TramiteDerivacion> tramiteList = mongoTemplate.find(query, TramiteDerivacion.class);

		if(!CollectionUtils.isEmpty(tramiteList))
			secuencia = tramiteList.get(0).getSecuencia() + 1;

		return secuencia;
	}

	public void envioCorreoSubsanacion(TramiteDerivacion subsanartramiteDerivacion) throws IOException {
		//TODO Armar mensaje del cuerpo de correo, obteniendo la plantillaSubsanar.html
		//Si la forma es ORIGINAL, se envia como pendientes, pero si es COPIA entonces que el mensaje indique que le ha llegago tramite como copia
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("plantillaSubsanacion.html");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String strLine;
		StringBuffer msjHTML = new StringBuffer();
		while ((strLine = bufferedReader.readLine()) != null) {
			msjHTML.append(strLine);
		}
		//Asunto: Segun Forma
		String forma;
		if(subsanartramiteDerivacion.getForma().equals("ORIGINAL"))
			forma = env.getProperty("app.field.asuntoDerivacion");//"STD AMSAC - Pendiente de atención";
		else
			forma = env.getProperty("app.field.asuntoDerivacionCopia"); //"STD AMSAC - Para su conocimiento";

		//Correo: Destinatario
		String correoDestinatario = subsanartramiteDerivacion.getUsuarioFin().getEmail();

		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat Formato = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat fechaa = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		//Armar el body del Email
		/*
		String numTramite = String.valueOf(subsanartramiteDerivacion.getTramite().getNumeroTramite());
		String fecha = fechaa.format(subsanartramiteDerivacion.getCreatedDate());
		String asunto = subsanartramiteDerivacion.getTramite().getAsunto();
		String razonSocialEmisor = subsanartramiteDerivacion.getUsuarioInicio().getPersona().getRazonSocialNombre();
		String correoEmisor = subsanartramiteDerivacion.getUsuarioInicio().getEmail();
		String proveido = subsanartramiteDerivacion.getProveidoAtencion();
		String plazoMaximo = fechaa.format(subsanartramiteDerivacion.getFechaMaximaAtencion());
		String horaRecepcion = hourFormat.format(subsanartramiteDerivacion.getCreatedDate());
		String avisoConfidencialidad = subsanartramiteDerivacion.getTramite().getAvisoConfidencial();
		String codigoEtica = subsanartramiteDerivacion.getTramite().getCodigoEtica();
		String desde = Formato.format(subsanartramiteDerivacion.getCreatedDate());
		String hasta = Formato.format(subsanartramiteDerivacion.getFechaMaximaAtencion());

		String bodyHtmlFinal = String.format(msjHTML.toString(), numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, avisoConfidencialidad, codigoEtica, desde, hasta);

		*/

		String plazoMaximo = "-";
		String hasta = "-";
		String avisoConfidencialidad = "-";
		String codigoEtica = "-";
		String proveido = "-";

		String urlTramite = env.getProperty("app.url.linkTramite");
		String numTramite = String.valueOf(subsanartramiteDerivacion.getTramite().getNumeroTramite());
		String fecha = fechaa.format(subsanartramiteDerivacion.getCreatedDate());
		String asunto = subsanartramiteDerivacion.getTramite().getAsunto();
		String razonSocialEmisor = subsanartramiteDerivacion.getUsuarioInicio().getPersona().getRazonSocialNombre();
		String correoEmisor = subsanartramiteDerivacion.getUsuarioInicio().getEmail();

		if(subsanartramiteDerivacion.getProveidoAtencion()!=null)
			proveido = subsanartramiteDerivacion.getProveidoAtencion();

		if(subsanartramiteDerivacion.getFechaMaximaAtencion()!=null)
			plazoMaximo = fechaa.format(subsanartramiteDerivacion.getFechaMaximaAtencion());

		String horaRecepcion = hourFormat.format(subsanartramiteDerivacion.getCreatedDate());

		if(subsanartramiteDerivacion.getTramite().getAvisoConfidencial()!=null)
			avisoConfidencialidad = subsanartramiteDerivacion.getTramite().getAvisoConfidencial();

		if(subsanartramiteDerivacion.getTramite().getCodigoEtica()!=null)
			codigoEtica = subsanartramiteDerivacion.getTramite().getCodigoEtica();

		String desde = Formato.format(subsanartramiteDerivacion.getCreatedDate());
		if(subsanartramiteDerivacion.getFechaMaximaAtencion()!=null)
			hasta = Formato.format(subsanartramiteDerivacion.getFechaMaximaAtencion());

		String bodyHtmlFinal = String.format(msjHTML.toString(), urlTramite, numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, avisoConfidencialidad, codigoEtica, desde, hasta);

		Map<String, String> params = new HashMap<String, String>();
		params.put("to", correoDestinatario);
		params.put("subject", forma);
		params.put("text", bodyHtmlFinal);

		/*
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		restTemplate.postForEntity( uri, params, null);
		*/
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity request = new HttpEntity<>(params, headers);
		restTemplate.exchange(uri,HttpMethod.POST,request,String.class);

	}

	public void envioCorreoDerivacion(TramiteDerivacion registrotramiteDerivacion) throws IOException {
		//TODO Armar mensaje del cuerpo de correo, obteniendo la plantillaDerivacion.html
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("plantillaDerivacion.html");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String strLine;
		StringBuffer msjHTML = new StringBuffer();
		while ((strLine = bufferedReader.readLine()) != null) {
			msjHTML.append(strLine);
		}
		//Si la forma es ORIGINAL, se envia como pendientes, pero si es COPIA entonces que el mensaje indique que le ha .
		// llegago tramite como copia
		//Asunto: Segun Forma
		String forma;
		if(registrotramiteDerivacion.getForma().equals("ORIGINAL"))
			forma = env.getProperty("app.field.asuntoDerivacion"); //"STD AMSAC - Pendiente de atención";
		else
			forma = env.getProperty("app.field.asuntoDerivacionCopia"); //"STD AMSAC - Para su conocimiento";

		//Correo: Destinatario
		String correoDestinatario = registrotramiteDerivacion.getUsuarioFin().getEmail();

		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat Formato = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat fechaa = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		//Armar el body del Email
		String plazoMaximo = "-";
		String hasta = "-";
		String avisoConfidencialidad = "-";
		String codigoEtica = "-";
		String proveido = "-";

		String urlTramite = env.getProperty("app.url.linkTramite");
		String numTramite = String.valueOf(registrotramiteDerivacion.getTramite().getNumeroTramite());
		String fecha = fechaa.format(registrotramiteDerivacion.getCreatedDate());
		String asunto = registrotramiteDerivacion.getTramite().getAsunto();
		String razonSocialEmisor = registrotramiteDerivacion.getUsuarioInicio().getPersona().getRazonSocialNombre();
		String correoEmisor = registrotramiteDerivacion.getUsuarioInicio().getEmail();

		if(registrotramiteDerivacion.getProveidoAtencion()!=null)
			proveido = registrotramiteDerivacion.getProveidoAtencion();

		if(registrotramiteDerivacion.getFechaMaximaAtencion()!=null)
			plazoMaximo = Formato.format(registrotramiteDerivacion.getFechaMaximaAtencion());

		String horaRecepcion = hourFormat.format(registrotramiteDerivacion.getCreatedDate());

		if(registrotramiteDerivacion.getTramite().getAvisoConfidencial()!=null)
			avisoConfidencialidad = registrotramiteDerivacion.getTramite().getAvisoConfidencial();

		if(registrotramiteDerivacion.getTramite().getCodigoEtica()!=null)
			codigoEtica = registrotramiteDerivacion.getTramite().getCodigoEtica();

		String desde = Formato.format(registrotramiteDerivacion.getCreatedDate());
		if(registrotramiteDerivacion.getFechaMaximaAtencion()!=null)
			hasta = Formato.format(registrotramiteDerivacion.getFechaMaximaAtencion());

		String bodyHtmlFinal = String.format(msjHTML.toString(), urlTramite, numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, avisoConfidencialidad, codigoEtica, desde, hasta);

		Map<String, String> params = new HashMap<String, String>();
		//params.put("to", "evelyn.flores@bitall.com.pe");
		params.put("to", correoDestinatario);
		params.put("subject", forma);
		params.put("text", bodyHtmlFinal);

		/*
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		restTemplate.postForEntity( uri, params, null);
		*/

		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity request = new HttpEntity<>(params, headers);
		restTemplate.exchange(uri,HttpMethod.POST,request,String.class);

	}

	//TODO: Crear un metodo que obtenga aquellas derivaciones que esten en estado "P" y cuya fecha maxima de atencion >= fecha de hoy
	// Con esa lista, obtener usuario fin y su correo. Enviar alerta
	// plantilla: asunto-> Tramite pendiente de atencion - N° Tramite[Mayu],
	// cuerpo (tabla) -> Estimado(a) Usted tiene un tramite pendiente de atencion con el siguiente detalle:
	// N° Tramite, fecha derivacion, fecha maxima de atencion, dias de atraso.
	// Para dar atencion al tramite, ingrese al siguiente link: link. Firma AMSAC.

	public void alertaTramiteFueraPlazoAtencion () throws IOException {
		//Obtener Lista de Tramites Derivacion, condiciones: estado->P y fechaMaxima>=Hoy
		Date todaysDate = new Date();
		Query query = new Query();
		Criteria criteria = Criteria.where("fechaMaximaAtencion").gte(todaysDate).and("estado").is("P");
		query.addCriteria(criteria);
		List<TramiteDerivacion> tramitePendienteList = mongoTemplate.find(query, TramiteDerivacion.class);
		//Obtener correo de cada usuarioFin de la lista de Tramite Derivacion Pendiente
		for(TramiteDerivacion usuarioTmp : tramitePendienteList){
			String correoUsuarioFin = usuarioTmp.getUsuarioFin().getEmail();
			//Enviar correo de alerta a cada usuarioFin
			enviarCorreoAlerta(correoUsuarioFin,usuarioTmp);
		}
	}

	public void enviarCorreoAlerta(String correoDestino,TramiteDerivacion tramiteDerivacion) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("plantillaAlerta.html");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String strLine;
		StringBuffer msjHTML = new StringBuffer();
		while ((strLine = bufferedReader.readLine()) != null) {
			msjHTML.append(strLine);
		}

		DateFormat Formato = new SimpleDateFormat("dd/MM/yyyy");

		//Armar el body del Email
		String numTramite = String.valueOf(tramiteDerivacion.getTramite().getNumeroTramite());
		String fechaDerivacion = Formato.format(tramiteDerivacion.getCreatedDate());
		String fechaMaximaAtencion = "";
		String diasAtraso = "";
		if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
			fechaMaximaAtencion = Formato.format(tramiteDerivacion.getFechaMaximaAtencion());
			LocalDate fechahoy = LocalDate.now();
			LocalDate fechaMaxima = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long dias = DAYS.between(fechaMaxima, fechahoy);
			diasAtraso = String.valueOf(dias).replace("-","");
		}

		String urlTramite = env.getProperty("app.url.linkTramite");

		String bodyHtmlFinal = String.format(msjHTML.toString(),numTramite,fechaDerivacion,fechaMaximaAtencion,diasAtraso,urlTramite);

		Map<String, String> params = new HashMap<String, String>();
		//params.put("to", "evelyn.flores@bitall.com.pe");
		params.put("to", correoDestino);
		params.put("subject", env.getProperty("app.field.asuntoAtencionFueraPlazo")+numTramite);
		params.put("text", bodyHtmlFinal);

		/*
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		restTemplate.postForEntity( uri, params, null);
		*/
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity request = new HttpEntity<>(params, headers);
		restTemplate.exchange(uri,HttpMethod.POST,request,String.class);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion rechazarTramiteDerivacion(RechazarTramiteDerivacionBodyRequest rechazarTramiteDerivacionBodyRequest) throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();

		TramiteDerivacion subsanarTramiteActual = tramiteDerivacionMongoRepository.findById(rechazarTramiteDerivacionBodyRequest.getId()).get();
		subsanarTramiteActual.setComentarioFin(rechazarTramiteDerivacionBodyRequest.getComentarioInicial());
		subsanarTramiteActual.setEstadoFin(EstadoTramiteConstant.RECHAZADO);
		subsanarTramiteActual.setFechaFin(new Date());
		subsanarTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(subsanarTramiteActual);

		int sec = obtenerSecuencia(subsanarTramiteActual.getTramite().getId());

		TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = mapper.map(subsanarTramiteActual, TramiteDerivacionBodyRequest.class);
		subsanarTramiteBodyRequest.setSecuencia(sec);
		subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
		subsanarTramiteBodyRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioInicio().getId());
		subsanarTramiteBodyRequest.setComentarioInicio(subsanarTramiteActual.getComentarioFin());
		subsanarTramiteBodyRequest.setEstadoInicio(subsanarTramiteActual.getEstadoFin());
		subsanarTramiteBodyRequest.setFechaInicio(new Date());
		subsanarTramiteBodyRequest.setForma("ORIGINAL");
		subsanarTramiteBodyRequest.setId(null);
		subsanarTramiteBodyRequest.setEstadoFin(null);
		subsanarTramiteBodyRequest.setFechaFin(null);
		subsanarTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(subsanarTramiteBodyRequest);

		tramiteService.actualizarEstadoTramite(subsanarTramiteActual.getTramite().getId(),subsanarTramiteActual.getEstadoFin());

		//Enviar correo para subsanacion
		//envioCorreoSubsanacion(nuevoDerivacionTramite);

		return nuevoDerivacionTramite;
	}

	public List<TramiteDerivacionReporteResponse> obtenerTramiteByTramiteId(String tramiteId){
		List<TramiteDerivacion> tramiteDerivacion = tramiteDerivacionMongoRepository.findByTramiteId(tramiteId);
		List<TramiteDerivacionReporteResponse> tramiteReporteResponseList = new ArrayList<>();
		LocalDate fechaMaxima = null;
		for(TramiteDerivacion temp : tramiteDerivacion){
			if(temp.getFechaMaximaAtencion()!=null){
				fechaMaxima = temp.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				temp.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionReporteResponse tramiteDerivacionReporteResponse = mapper.map(temp,TramiteDerivacionReporteResponse.class);
			if(fechaMaxima!=null)
				tramiteDerivacionReporteResponse.setFechaMaximaAtencion(fechaMaxima);
			tramiteReporteResponseList.add(tramiteDerivacionReporteResponse);
		}
		return tramiteReporteResponseList;
	}

	public TramiteDerivacion registrarTramiteDerivacion(TramiteDerivacionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		//Obtener Usuario Inicio
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioInicio();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
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
		Usuario userInicio = mapper.map(usuario,Usuario.class);

		//Mapear Usuario Fin
		uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioFin();
		response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		personaL = (LinkedHashMap<String, String>) usuario.get("persona");
		personaL.remove("createdDate");
		personaL.remove("lastModifiedDate");
		personaL.remove("tipoDocumento");
		personaL.remove("entityId");
		persona = mapper.map(personaL,Persona.class);
		usuario.replace("persona",persona);
		Usuario userFin = mapper.map(usuario,Usuario.class);

		Date fechaMaxima = null;
		if(tramiteDerivacionBodyRequest.getFechaMaximaAtencion()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteDerivacionBodyRequest.getFechaMaximaAtencion();
			tramiteDerivacionBodyRequest.setFechaMaximaAtencion(null);
			fechaMaxima =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}

		TramiteDerivacion registroTramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		if(fechaMaxima!=null)
			registroTramiteDerivacion.setFechaMaximaAtencion(fechaMaxima);
		registroTramiteDerivacion.setUsuarioInicio(userInicio);
		registroTramiteDerivacion.setUsuarioFin(userFin);
		registroTramiteDerivacion.setFechaInicio(new Date());
		registroTramiteDerivacion.setTramite(tramiteMongoRepository.findById(tramiteDerivacionBodyRequest.getTramiteId()).get());
		registroTramiteDerivacion.setEstado("P");

		int sec = obtenerSecuencia(tramiteDerivacionBodyRequest.getTramiteId());
		registroTramiteDerivacion.setSecuencia(sec);

		tramiteDerivacionMongoRepository.save(registroTramiteDerivacion);

		try{
			ejecutarAccionesDeAcuerdoAConfiguracion(registroTramiteDerivacion);
		}catch (Exception ex){
			log.error("ERROR",ex);
		}

		return registroTramiteDerivacion;

	}

	private void ejecutarAccionesDeAcuerdoAConfiguracion(TramiteDerivacion registroTramiteDerivacion) throws Exception {
		//SOLO EJECUTAMOS ESTA PARTE SI ES ORIGINAL
		if(registroTramiteDerivacion.getForma().equals("ORIGINAL")){
			//Obtenemos el usuario cargo fin
			UsuarioBuscarResponse usuarioBuscarResponse = mapper.map(obtenerUsuarioById(registroTramiteDerivacion.getUsuarioFin().getId()),UsuarioBuscarResponse.class);

			//Consultamos si ese cargo fin tiene cargo con derivacion automatica
			if(usuarioBuscarResponse!=null && !StringUtils.isBlank(usuarioBuscarResponse.getCargoId())){
				List<ConfiguracionDerivacionResponse> configuracionDerivacionResponseList = obtenerConfiguracionDerivacionResponseByCargoOrigenId(usuarioBuscarResponse.getCargoId());
				for(ConfiguracionDerivacionResponse configuracionDerivacionResponse : configuracionDerivacionResponseList){
					//Obtenemos los usuario que tengan el cargo destino id.
					List<UsuarioCargoResponse> usuarioCargoResponseList = obtenerUsuarioByCargo(configuracionDerivacionResponse.getCargoDestino().getCargo());
					for (UsuarioCargoResponse usuarioCargoResponse: usuarioCargoResponseList) {
						//Registrar derivaciones de copia para cada elemento, primero validamos si ya se registro una copia.
						if(!existeDerivacionCopiaParaUsuarioInicioFinMismoTramite(registroTramiteDerivacion,usuarioCargoResponse)){
							TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = new TramiteDerivacionBodyRequest();
							subsanarTramiteBodyRequest.setTramiteId(registroTramiteDerivacion.getTramite().getId());
							subsanarTramiteBodyRequest.setUsuarioInicio(registroTramiteDerivacion.getUsuarioInicio().getId());
							subsanarTramiteBodyRequest.setUsuarioFin(usuarioCargoResponse.getUsuario().getId());
							subsanarTramiteBodyRequest.setComentarioInicio(registroTramiteDerivacion.getComentarioInicio());
							subsanarTramiteBodyRequest.setEstadoInicio(registroTramiteDerivacion.getEstadoInicio());
							subsanarTramiteBodyRequest.setFechaInicio(new Date());
							subsanarTramiteBodyRequest.setForma("COPIA");
							subsanarTramiteBodyRequest.setId(null);
							subsanarTramiteBodyRequest.setEstadoFin(null);
							subsanarTramiteBodyRequest.setFechaFin(null);
							subsanarTramiteBodyRequest.setComentarioFin(null);
							registrarTramiteDerivacion(subsanarTramiteBodyRequest);
						}
					}
				}
			}
		}
	}

	private LinkedHashMap<Object, Object> obtenerUsuarioById(String usuarioId){
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+usuarioId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		return response.getBody().getData()!=null?(LinkedHashMap<Object, Object>) response.getBody().getData():null;
	}

	private List<ConfiguracionDerivacionResponse> obtenerConfiguracionDerivacionResponseByCargoOrigenId(String cargoOrigenId){
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/configuraciones-derivacion";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cargoOrigenId", cargoOrigenId);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			builder.queryParam(entry.getKey(), entry.getValue());
		}

		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(builder.toUriString(),HttpMethod.GET,entity, CommonResponse.class);

		return response.getBody().getData()!=null?(List<ConfiguracionDerivacionResponse>)response.getBody().getData():new ArrayList<>();

	}

	private List<UsuarioCargoResponse> obtenerUsuarioByCargo(String cargo){
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuario-cargo/"+cargo;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));

		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, CommonResponse.class);
		return response.getBody().getData()!=null?(List<UsuarioCargoResponse>)response.getBody().getData():new ArrayList<>();

	}

	private boolean existeDerivacionCopiaParaUsuarioInicioFinMismoTramite(TramiteDerivacion registroTramiteDerivacion, UsuarioCargoResponse usuarioCargoResponse) throws Exception {
		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setForma("COPIA");
		tramiteDerivacionRequest.setTramiteId(registroTramiteDerivacion.getTramite().getId());
		tramiteDerivacionRequest.setUsuarioInicio(registroTramiteDerivacion.getUsuarioInicio().getId());
		tramiteDerivacionRequest.setUsuarioFin(usuarioCargoResponse.getUsuario().getId());
		List<TramiteDerivacion> tramiteDerivacionList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);

		return CollectionUtils.isEmpty(tramiteDerivacionList)?false:true;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarTramiteDerivacionMigracion(TramiteDerivacionMigracionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		/*
		//Obtener Usuario Inicio
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioInicio();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
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
		Usuario userInicio = mapper.map(usuario,Usuario.class);

		//Mapear Usuario Fin
		uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+tramiteDerivacionBodyRequest.getUsuarioFin();
		response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
		personaL = (LinkedHashMap<String, String>) usuario.get("persona");
		personaL.remove("createdDate");
		personaL.remove("lastModifiedDate");
		personaL.remove("tipoDocumento");
		personaL.remove("entityId");
		persona = mapper.map(personaL,Persona.class);
		usuario.replace("persona",persona);
		Usuario userFin = mapper.map(usuario,Usuario.class);

		Date fechaMaxima = null;
		if(tramiteDerivacionBodyRequest.getFechaMaximaAtencion()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteDerivacionBodyRequest.getFechaMaximaAtencion();
			tramiteDerivacionBodyRequest.setFechaMaximaAtencion(null);
			fechaMaxima =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}
		*/

		TramiteDerivacion registroTramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		/*
		if(fechaMaxima!=null)
			registroTramiteDerivacion.setFechaMaximaAtencion(fechaMaxima);
		*/
		//registroTramiteDerivacion.setUsuarioInicio(userInicio);
		//registroTramiteDerivacion.setUsuarioFin(userFin);
		//registroTramiteDerivacion.setFechaInicio(new Date());
		if(registroTramiteDerivacion.getEstadoInicio().equals("NOTIFICACION")){
			registroTramiteDerivacion.setUsuarioFin(null);
		}
		registroTramiteDerivacion.setTramite(tramiteMongoRepository.findById(tramiteDerivacionBodyRequest.getTramiteId()).get());
		//registroTramiteDerivacion.setEstado("A");

		int sec = obtenerSecuencia(tramiteDerivacionBodyRequest.getTramiteId());
		registroTramiteDerivacion.setSecuencia(sec);

		tramiteDerivacionMongoRepository.save(registroTramiteDerivacion);


		/*
		try{
			ejecutarAccionesDeAcuerdoAConfiguracion(registroTramiteDerivacion);
		}catch (Exception ex){
			log.error("ERROR",ex);
		}
 		*/

		return registroTramiteDerivacion;

	}

	public int totalRegistros(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		if(parameters.get("numeroTramite").equals(0)){
			parameters.remove("numeroTramite");
		}
		List<Criteria> listCriteria =  new ArrayList<>();
		//TODO: Verificar busqueda por parametro tramite.numeroTramite
		if(parameters.containsKey("tramiteId")){
			listCriteria.add(Criteria.where("tramite.id").is(parameters.get("tramiteId")));
			parameters.remove("tramiteId");
		}
		if(parameters.containsKey("numeroTramite")){
			listCriteria.add(Criteria.where("tramite.numeroTramite").is(parameters.get("numeroTramite")));
			parameters.remove("numeroTramite");
		}
		if(parameters.containsKey("asunto")){
			//listCriteria.add(Criteria.where("tramite.asunto").is(parameters.get("numeroTramite")));
			listCriteria.add(Criteria.where("tramite.asunto").regex(".*"+parameters.get("asunto")+".*"));
			parameters.remove("numeroTramite");
		}
		if(parameters.containsKey("fechaDerivacionDesde") && parameters.containsKey("fechaDerivacionHasta")){
			listCriteria.add(Criteria.where("fechaInicio").gte(parameters.get("fechaDerivacionDesde")).lte(parameters.get("fechaDerivacionHasta")));
			parameters.remove("fechaDerivacionDesde");
		}
		if(parameters.containsKey("usuarioInicio")){
			listCriteria.add(Criteria.where("usuarioInicio.id").is(parameters.get("usuarioInicio")));
			parameters.remove("fechaDerivacionDesde");
		}
		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}
		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}

		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

		long cantidadRegistro = mongoTemplate.count(andQuery, TramiteDerivacion.class);

		return (int)cantidadRegistro;
	}

	private List<String> obtenerTramitesId(TramiteDerivacionRequest tramiteDerivacionRequest){

		List<String> idTramites = new ArrayList<>();
		List<Criteria> listCriteria =  new ArrayList<>();
		List<Criteria> andExpression =  new ArrayList<>();
		Criteria andCriteria = new Criteria();
		Query andQuery = new Query();

		if(!StringUtils.isBlank(tramiteDerivacionRequest.getTramiteId())){
			listCriteria.add(Criteria.where("id").is(tramiteDerivacionRequest.getTramiteId()));
		}
		if(tramiteDerivacionRequest.getNumeroTramite()>0){
			listCriteria.add(Criteria.where("numeroTramite").is(tramiteDerivacionRequest.getNumeroTramite()));
		}
		if(!StringUtils.isBlank(tramiteDerivacionRequest.getAsunto())){
			listCriteria.add(Criteria.where("asunto").regex(".*"+tramiteDerivacionRequest.getAsunto()+".*","i"));
		}
		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));

			andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

			List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
			if(!CollectionUtils.isEmpty(tramiteList)){
				tramiteList.stream().forEach(x -> idTramites.add(x.getId()));
			}
		}

		return idTramites;
	}


}
