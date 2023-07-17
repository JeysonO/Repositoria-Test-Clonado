package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.util.UriComponentsBuilder;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.AtenderDerivacionLoteRequest;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.CustomMultipartFile;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.util.EstadoTramiteConstant;
import pe.com.amsac.tramite.bs.util.EstadoTramiteDerivacionConstant;
import pe.com.amsac.tramite.bs.util.TipoAdjuntoConstant;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	private DocumentoAdjuntoService documentoAdjuntoService;

	@Autowired
	private ConfiguracionUsuarioService configuracionUsuarioService;

	@Autowired
	private UsuarioService usuarioService;

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
		Tramite tramite = null;
		if(!CollectionUtils.isEmpty(tramitePendienteList)){
			tramite = tramitePendienteList.get(0).getTramite();
		}
		boolean actualizarTramiteDerivacion = false;
		for(TramiteDerivacion tramiteDerivacion : tramitePendienteList){
			actualizarTramiteDerivacion = false;
			if(StringUtils.isBlank(tramiteDerivacion.getDependenciaNombreUsuarioInicio())){
				uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
				response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

				LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);

				if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
					tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
				}else{
					tramiteDerivacion.setDependenciaNombreUsuarioInicio(tramiteDerivacion.getDependenciaUsuarioInicio().getNombre());
				}
				nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
				tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);
				actualizarTramiteDerivacion = true;
			}

			/*
			//Se completan datos de usuario inicio
			uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
			Persona personaDto = mapper.map(persona,Persona.class);

			if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
			}else{
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(tramiteDerivacion.getDependenciaUsuarioInicio().getNombre());
			}
			*/

			/*
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
			*/

			/*
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);
			*/

			if(StringUtils.isBlank(tramiteDerivacion.getDependenciaNombreUsuarioFin())){
				uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
				response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
				LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);

				if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
					tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
				}else{
					tramiteDerivacion.setDependenciaNombreUsuarioFin(tramiteDerivacion.getDependenciaUsuarioFin().getNombre());
				}

				nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
				tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);

				actualizarTramiteDerivacion = true;
			}

			/*
			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			persona = (LinkedHashMap<String, String>) usuario.get("persona");
			personaDto = mapper.map(persona,Persona.class);

			if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			}else{
				tramiteDerivacion.setDependenciaNombreUsuarioFin(tramiteDerivacion.getDependenciaUsuarioFin().getNombre());
			}
			*/

			/*
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			*/
			/*
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);
			*/

			if(actualizarTramiteDerivacion){
				tramiteDerivacionMongoRepository.save(tramiteDerivacion);
			}

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
		String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
		if(!StringUtils.isBlank(dependenciaIdUserSession)){
			tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaIdUserSession);
		}
		/*
		if(tramiteDerivacionRequest.getEstado().equals("A")){
			tramiteDerivacionRequest.setNotEstadoFin("RECEPCIONADO");
		}
		*/

		List<TramiteDerivacion> tramitePendienteList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);

		if(CollectionUtils.isEmpty(tramitePendienteList))
			return tramitePendienteList;

		//Ordenamos por numero de tramite
		/*
		Collections.sort(tramitePendienteList, new Comparator<TramiteDerivacion>(){
			@Override
			public int compare(TramiteDerivacion a, TramiteDerivacion b)
			{
				return b.getTramite().getNumeroTramite() - a.getTramite().getNumeroTramite();
			}
		});
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
			parameters.remove("razonSocial");
			parameters.remove("origenDocumento");

			for (String tramiteId : tramiteIds) {
				Criteria expression = new Criteria();
				expression.and("tramite.id").is(tramiteId);
				orExpression.add(expression);
			}
			//orQuery.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}
		/*
		if(parameters.containsKey("fechaDerivacionDesde") && parameters.containsKey("fechaDerivacionHasta")){
			listCriteria.add(Criteria.where("fechaInicio").gte(((Date)parameters.get("fechaDerivacionDesde"))).lte(((Date)parameters.get("fechaDerivacionHasta"))));
			parameters.remove("fechaDerivacionDesde");
			parameters.remove("fechaDerivacionHasta");
		}
		*/
		/*
		if(parameters.containsKey("fechaDerivacionDesde"))
			listCriteria.add(Criteria.where("fechaInicio").gte((Date)parameters.get("fechaDerivacionDesde")));

		if(parameters.containsKey("fechaDerivacionHasta")){
			Date fechaHasta = (Date)parameters.get("fechaDerivacionHasta");
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			//listCriteria.add(Criteria.where("fechaInicio").lte((Date)parameters.get("fechaDerivacionHasta")));
			listCriteria.add(Criteria.where("fechaInicio").lte(fechaHasta));
		}
		*/
		if(parameters.containsKey("usuarioInicio")){
			listCriteria.add(Criteria.where("usuarioInicio.id").is(parameters.get("usuarioInicio")));
			parameters.remove("usuarioInicio");
		}
		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}
		if(parameters.containsKey("dependenciaIdUsuarioInicio")){
			if(parameters.get("dependenciaIdUsuarioInicio")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioInicio").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioInicio.id").is(parameters.get("dependenciaIdUsuarioInicio")));
			parameters.remove("dependenciaIdUsuarioInicio");
		}
		if(parameters.containsKey("dependenciaIdUsuarioFin")){
			if(parameters.get("dependenciaIdUsuarioFin")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioFin").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioFin.id").is(parameters.get("dependenciaIdUsuarioFin")));

			parameters.remove("dependenciaIdUsuarioFin");
		}
		if(parameters.containsKey("estadoFin") && parameters.get("estadoFin").toString().equals("PENDIENTE")){
			listCriteria.add(Criteria.where("estado").is("P"));
			parameters.remove("estadoFin");
		}
		if(parameters.containsKey("notEstadoFin")){
			listCriteria.add(Criteria.where("estadoFin").ne(parameters.get("notEstadoFin")));
			parameters.remove("notEstadoFin");
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
		parameters.remove("fechaDerivacionDesde");
		parameters.remove("fechaDerivacionHasta");

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
		andQuery.with(Sort.by(
				Sort.Order.desc("fechaInicio")
		));

		List<TramiteDerivacion> tramiteDerivacionList = mongoTemplate.find(andQuery, TramiteDerivacion.class);

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
		String emailUsuarioCreacion = null;
		Tramite tramite = null;
		Map<String, Map> usuarioCreacionMap = new HashMap<>();
		Map<String, Map> usuarioInicioMap = new HashMap<>();
		Map<String, Map> usuarioFinMap = new HashMap<>();
		Map<String, Object> paramTmp = new HashMap<>();
		/*
		if(!CollectionUtils.isEmpty(tramiteDerivacionList)){
			tramite = tramiteDerivacionList.get(0).getTramite();
			uriBusqueda = uri + tramite.getCreatedByUser();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			usuarioCreacion = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			emailUsuarioCreacion = usuario.get("email").toString();
			if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);
				dependenciaEmpresa = personaDto.getRazonSocialNombre();
			}else{
				dependenciaEmpresa = tramite.getDependenciaUsuarioCreacion().getNombre();; // ((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString();
			}
		}
		*/

		boolean actualizarTramiteDerivacion = false;
		for(TramiteDerivacion tramiteDerivacion : tramiteDerivacionList){
			tramite = tramiteDerivacion.getTramite();
			actualizarTramiteDerivacion = false;
			if(tramiteDerivacion.getNumeroTramiteRelacionado()==0 && tramite.getTramiteRelacionado()!=null){
				tramiteDerivacion.setNumeroTramiteRelacionado(tramite.getTramiteRelacionado().getNumeroTramite());
				actualizarTramiteDerivacion = true;
			}
			if(StringUtils.isBlank(tramiteDerivacion.getUsuarioCreacion())){
				if(!usuarioCreacionMap.containsKey(tramite.getCreatedByUser())){
					//tramite = tramiteDerivacionList.get(0).getTramite();
					uriBusqueda = uri + tramite.getCreatedByUser();
					response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
					LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
					usuarioCreacion = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
					emailUsuarioCreacion = usuario.get("email").toString();
					if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
						LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
						Persona personaDto = mapper.map(persona,Persona.class);
						dependenciaEmpresa = personaDto.getRazonSocialNombre();
					}else{
						//dependenciaEmpresa = tramite.getDependenciaUsuarioCreacion().getNombre();; // ((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString();
						//Si es tramite interno, entonces se muestra lo que se encuentra en el campo entidadExterna.razonSocial
						dependenciaEmpresa = tramite.getEntidadExterna()!=null?tramite.getEntidadExterna().getRazonSocial():"";
					}
					paramTmp = new HashMap<>();
					paramTmp.put("usuarioCreacion",usuarioCreacion);
					paramTmp.put("dependenciaEmpresa",dependenciaEmpresa);
					paramTmp.put("emailUsuarioCreacion",emailUsuarioCreacion);
					usuarioCreacionMap.put(tramite.getCreatedByUser(),paramTmp);
				}

				tramiteDerivacion.setUsuarioCreacion(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("usuarioCreacion").toString());
				tramiteDerivacion.setDependenciaEmpresa(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("dependenciaEmpresa").toString());
				tramiteDerivacion.setEmailUsuarioCreacion(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("emailUsuarioCreacion").toString());
				actualizarTramiteDerivacion = true;
			}
			/*
			if(!usuarioCreacionMap.containsKey(tramite.getCreatedByUser())){
				//tramite = tramiteDerivacionList.get(0).getTramite();
				uriBusqueda = uri + tramite.getCreatedByUser();
				response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
				LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
				usuarioCreacion = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
				emailUsuarioCreacion = usuario.get("email").toString();
				if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
					LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
					Persona personaDto = mapper.map(persona,Persona.class);
					dependenciaEmpresa = personaDto.getRazonSocialNombre();
				}else{
					dependenciaEmpresa = tramite.getDependenciaUsuarioCreacion().getNombre();; // ((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString();
				}
				paramTmp = new HashMap<>();
				paramTmp.put("usuarioCreacion",usuarioCreacion);
				paramTmp.put("dependenciaEmpresa",dependenciaEmpresa);
				paramTmp.put("emailUsuarioCreacion",emailUsuarioCreacion);
				usuarioCreacionMap.put(tramite.getCreatedByUser(),paramTmp);
			}

			tramiteDerivacion.setUsuarioCreacion(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("usuarioCreacion").toString());
			tramiteDerivacion.setDependenciaEmpresa(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("dependenciaEmpresa").toString());
			tramiteDerivacion.setEmailUsuarioCreacion(usuarioCreacionMap.get(tramite.getCreatedByUser()).get("emailUsuarioCreacion").toString());
			*/

			if(StringUtils.isBlank(tramiteDerivacion.getDependenciaNombreUsuarioInicio()) && tramiteDerivacion.getUsuarioInicio()!=null){
				uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
				response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

				LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);

				if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
					tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
				}else{
					tramiteDerivacion.setDependenciaNombreUsuarioInicio(tramiteDerivacion.getDependenciaUsuarioInicio().getNombre());
				}

				//Se aprovecha y se coloca el nombre completo del usuario inicio
				if(StringUtils.isBlank(tramiteDerivacion.getUsuarioInicioNombreCompleto())){
					nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
					tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);
				}

				actualizarTramiteDerivacion = true;
			}
			/*
			//Se completan datos de usuario inicio
			uriBusqueda = uri + tramiteDerivacion.getUsuarioInicio().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
			Persona personaDto = mapper.map(persona,Persona.class);

			if(usuario.get("tipoUsuario").equals("EXTERNO")){//tramite.getOrigenDocumento().equals("EXTERNO")){
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
			}else{
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(tramiteDerivacion.getDependenciaUsuarioInicio().getNombre());
			}
			*/

			/*
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioInicio(personaDto.getRazonSocialNombre());
			*/

			if(StringUtils.isBlank(tramiteDerivacion.getDependenciaNombreUsuarioFin()) && tramiteDerivacion.getUsuarioFin()!=null){

				//Se completan datos de usuario Fin
				uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
				response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

				LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
				LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
				Persona personaDto = mapper.map(persona,Persona.class);

				if(usuario.get("tipoUsuario").equals("EXTERNO")){// tramite.getOrigenDocumento().equals("EXTERNO")){
					tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
				}else{
					tramiteDerivacion.setDependenciaNombreUsuarioFin(tramiteDerivacion.getDependenciaUsuarioFin().getNombre());
				}

				if(StringUtils.isBlank(tramiteDerivacion.getUsuarioFinNombreCompleto())){
					nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
					tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);
				}
				actualizarTramiteDerivacion = true;
			}
			/*
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);

			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});

			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			LinkedHashMap<String, String> persona = (LinkedHashMap<String, String>) usuario.get("persona");
			Persona personaDto = mapper.map(persona,Persona.class);

			if(usuario.get("tipoUsuario").equals("EXTERNO")){// tramite.getOrigenDocumento().equals("EXTERNO")){
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			}else{
				tramiteDerivacion.setDependenciaNombreUsuarioFin(tramiteDerivacion.getDependenciaUsuarioFin().getNombre());
			}
			*/
			/*
			if(((LinkedHashMap)response.getBody().getData()).get("cargoNombre")!=null)
				tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			if(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre")!=null)
				tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			else
				tramiteDerivacion.setDependenciaNombreUsuarioFin(personaDto.getRazonSocialNombre());
			*/

			/*
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);
			*/

			if(actualizarTramiteDerivacion){
				tramiteDerivacionMongoRepository.save(tramiteDerivacion);
			}

			//Se calcula los dias qeu no ha sido atendido el tramite
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				//Fecha fin de atencion
				Date fechaDeAtencion = tramiteDerivacion.getFechaFin()!=null?tramiteDerivacion.getFechaFin():new Date();
				Date fechaMaximaDeAtencion = tramiteDerivacion.getFechaMaximaAtencion();
				int dias = (int) ((fechaDeAtencion.getTime() - fechaMaximaDeAtencion.getTime()) / 86400000);
				tramiteDerivacion.setDiasFueraPlazo(dias);
			}

		}

		return tramiteDerivacionList;
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
		String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
		String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();

		TramiteDerivacion subsanarTramiteActual = tramiteDerivacionMongoRepository.findById(subsanartramiteDerivacionBodyrequest.getId()).get();
		subsanarTramiteActual.setComentarioFin(subsanartramiteDerivacionBodyrequest.getComentarioInicial());
		subsanarTramiteActual.setEstadoFin(EstadoTramiteConstant.SUBSANACION);
		subsanarTramiteActual.setFechaFin(new Date());
		subsanarTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(subsanarTramiteActual);

		int sec = obtenerSecuencia(subsanarTramiteActual.getTramite().getId());

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setTramiteId(subsanarTramiteActual.getTramite().getId());
		tramiteDerivacionRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioFin().getId());
		List<TramiteDerivacion> tramiteDerivacionList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);
		//Ordenamos por numero de tramite
		Collections.sort(tramiteDerivacionList, new Comparator<TramiteDerivacion>(){
			@Override
			public int compare(TramiteDerivacion a, TramiteDerivacion b)
			{
				return b.getSecuencia() - a.getSecuencia();
			}
		});
		//Si esta ordenado de mayor a menos por las secuencia, entonces el segunco registro sera el ultimo derivado
		TramiteDerivacion tramiteDerivacionAnterior = tramiteDerivacionList.get(1);

		LocalDate fechaMaxima = null;
		if(subsanarTramiteActual.getFechaMaximaAtencion()!=null){
			fechaMaxima = subsanarTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			subsanarTramiteActual.setFechaMaximaAtencion(null);
		}
		TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = new TramiteDerivacionBodyRequest(); // mapper.map(subsanarTramiteActual, TramiteDerivacionBodyRequest.class);
		subsanarTramiteBodyRequest.setSecuencia(sec);
		subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
		subsanarTramiteBodyRequest.setDependenciaIdUsuarioInicio(dependenciaIdUserSession);
		subsanarTramiteBodyRequest.setCargoIdUsuarioInicio(cargoIdUserSession);

		//subsanarTramiteBodyRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioInicio().getId());
		subsanarTramiteBodyRequest.setUsuarioFin(tramiteDerivacionAnterior.getUsuarioInicio().getId());
		if(tramiteDerivacionAnterior.getDependenciaUsuarioInicio()!=null){
			subsanarTramiteBodyRequest.setDependenciaIdUsuarioFin(tramiteDerivacionAnterior.getDependenciaUsuarioInicio().getId());
		}
		if(tramiteDerivacionAnterior.getCargoUsuarioFin()!=null){
			subsanarTramiteBodyRequest.setCargoIdUsuarioFin(tramiteDerivacionAnterior.getCargoUsuarioFin().getId());
		}


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
		subsanarTramiteBodyRequest.setTramiteId(subsanarTramiteActual.getTramite().getId());

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
			atencionTramiteDerivacionBodyRequest.setComentarioFin(StringUtils.isBlank(derivartramiteBodyrequest.getComentarioFin())?"CONOCIMIENTO ATENDIDO":derivartramiteBodyrequest.getComentarioFin());
			derivacionTramiteActual = registrarAtencionTramiteDerivacion(atencionTramiteDerivacionBodyRequest);
			derivacionTramiteActual.setForma("COPIA");
			//tramiteService.actualizarEstadoTramite(derivacionTramiteActual.getTramite().getId(),EstadoTramiteConstant.ATENDIDO);
		}else{
			derivacionTramiteActual = tramiteDerivacionMongoRepository.findById(derivartramiteBodyrequest.getId()).get();
			//Si el estado ACtual ya es atendido, entonces ya no actualizo nada
			if(derivacionTramiteActual.getEstadoFin() == null || (derivacionTramiteActual.getEstadoFin()!=null && !derivacionTramiteActual.getEstadoFin().equals(EstadoTramiteConstant.ATENDIDO))){
				ZoneId defaultZoneId = ZoneId.systemDefault();
				derivacionTramiteActual.setEstadoFin(EstadoTramiteConstant.DERIVADO);
				derivacionTramiteActual.setFechaFin(new Date());
				//derivacionTramiteActual.setProveidoAtencion(derivartramiteBodyrequest.getProveidoAtencion());
				derivacionTramiteActual.setComentarioFin(derivartramiteBodyrequest.getComentarioFin());
				//derivacionTramiteActual.setFechaMaximaAtencion(Date.from(derivartramiteBodyrequest.getFechaMaximaAtencion().atStartOfDay(defaultZoneId).toInstant()));
				derivacionTramiteActual.setEstado("A");
				tramiteDerivacionMongoRepository.save(derivacionTramiteActual);

				//Actualizamos el estado a nivel de tramite
				tramiteService.actualizarEstadoTramite(derivacionTramiteActual.getTramite().getId(),EstadoTramiteConstant.DERIVADO);
			}
		}

		//Asignar valores manualmente segun condiciones
		int sec = obtenerSecuencia(derivacionTramiteActual.getTramite().getId());
		String usuarioId = securityHelper.obtenerUserIdSession();
		String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
		String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();
		LocalDate fechaMaxima = null;
		/*
		if(derivacionTramiteActual.getFechaMaximaAtencion()!=null){
			fechaMaxima = derivacionTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			derivacionTramiteActual.setFechaMaximaAtencion(null);
		}
		*/
		if(derivartramiteBodyrequest.getFechaMaximaAtencion()!=null)
			fechaMaxima = derivartramiteBodyrequest.getFechaMaximaAtencion();

		//Se limpia la fecha maxima de atencion del tramite actual, para que no se copie al nuevo tramite derivacion
		derivacionTramiteActual.setFechaMaximaAtencion(null);

		//Crear nuevo tramite detivacion
		TramiteDerivacionBodyRequest derivacionTramiteBodyRequest = mapper.map(derivacionTramiteActual, TramiteDerivacionBodyRequest.class);
		derivacionTramiteBodyRequest.setSecuencia(sec);

		//Usuario Inicio
		derivacionTramiteBodyRequest.setUsuarioInicio(usuarioId);
		derivacionTramiteBodyRequest.setDependenciaIdUsuarioInicio(dependenciaIdUserSession);
		derivacionTramiteBodyRequest.setCargoIdUsuarioInicio(cargoIdUserSession);

		//Usuario fin
		derivacionTramiteBodyRequest.setUsuarioFin(derivartramiteBodyrequest.getUsuarioFin());
		derivacionTramiteBodyRequest.setDependenciaIdUsuarioFin(derivartramiteBodyrequest.getDependenciaIdUsuarioFin());
		derivacionTramiteBodyRequest.setCargoIdUsuarioFin(derivartramiteBodyrequest.getCargoIdUsuarioFin());

		derivacionTramiteBodyRequest.setEstadoInicio(EstadoTramiteConstant.DERIVADO);
		derivacionTramiteBodyRequest.setFechaInicio(new Date());
		derivacionTramiteBodyRequest.setForma(derivartramiteBodyrequest.getForma());
		if(fechaMaxima!=null)
			derivacionTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);

		derivacionTramiteBodyRequest.setComentarioInicio(derivacionTramiteActual.getComentarioFin());
		derivacionTramiteBodyRequest.setProveidoAtencion(derivartramiteBodyrequest.getProveidoAtencion());

		derivacionTramiteBodyRequest.setId(null);
		derivacionTramiteBodyRequest.setEstadoFin(null);
		derivacionTramiteBodyRequest.setFechaFin(null);
		derivacionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(derivacionTramiteBodyRequest);

		try{
			envioCorreoDerivacion(nuevoDerivacionTramite);
		}catch (Exception ex){
			log.error("ERROR ENVIANDO CORREO DERIVACION: ",ex);
		}


		return nuevoDerivacionTramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarRecepcionTramiteDerivacion(String id) throws Exception {

		TramiteDerivacion recepcionTramiteActual = tramiteDerivacionMongoRepository.findById(id).get();
		recepcionTramiteActual.setEstadoFin(EstadoTramiteConstant.RECEPCIONADO);
		recepcionTramiteActual.setFechaFin(new Date());
		recepcionTramiteActual.setComentarioFin("Se recepciona tramite");
		recepcionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(recepcionTramiteActual);

		TramiteDerivacion nuevoRecepcionTramite = recepcionTramiteActual;

		//Solo si es original puedo hacer alguna otra accion.
		if(recepcionTramiteActual.getForma().equals("ORIGINAL")){
			int sec = obtenerSecuencia(recepcionTramiteActual.getTramite().getId());

			LocalDate fechaMaxima = null;
			if(recepcionTramiteActual.getFechaMaximaAtencion()!=null){
				fechaMaxima = recepcionTramiteActual.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				recepcionTramiteActual.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionBodyRequest recepcionTramiteBodyRequest = mapper.map(recepcionTramiteActual, TramiteDerivacionBodyRequest.class);
			recepcionTramiteBodyRequest.setSecuencia(sec);

			/*
			//Usuario Inicio
			recepcionTramiteBodyRequest.setUsuarioInicio(recepcionTramiteActual.getUsuarioFin().getId());
			if(recepcionTramiteActual.getDependenciaUsuarioInicio()!=null)
				recepcionTramiteBodyRequest.setDependenciaIdUsuarioInicio(recepcionTramiteActual.getDependenciaUsuarioInicio().getId());
			if(recepcionTramiteActual.getCargoUsuarioInicio()!=null)
				recepcionTramiteBodyRequest.setCargoIdUsuarioInicio(recepcionTramiteActual.getCargoUsuarioInicio().getId());

			//Usuario Fin
			recepcionTramiteBodyRequest.setUsuarioFin(recepcionTramiteActual.getUsuarioFin().getId());
			if(recepcionTramiteActual.getDependenciaUsuarioFin()!=null)
				recepcionTramiteBodyRequest.setDependenciaIdUsuarioFin(recepcionTramiteActual.getDependenciaUsuarioFin().getId());
			if(recepcionTramiteActual.getCargoUsuarioFin()!=null)
				recepcionTramiteBodyRequest.setCargoIdUsuarioFin(recepcionTramiteActual.getCargoUsuarioFin().getId());
			*/
			recepcionTramiteBodyRequest.setUsuarioInicio(recepcionTramiteActual.getUsuarioFin().getId());
			recepcionTramiteBodyRequest.setUsuarioFin(recepcionTramiteActual.getUsuarioFin().getId());
			if(recepcionTramiteActual.getDependenciaUsuarioFin()!=null){
				recepcionTramiteBodyRequest.setDependenciaIdUsuarioInicio(recepcionTramiteActual.getDependenciaUsuarioFin().getId());
				recepcionTramiteBodyRequest.setDependenciaIdUsuarioFin(recepcionTramiteActual.getDependenciaUsuarioFin().getId());
			}
			if(recepcionTramiteActual.getCargoUsuarioFin()!=null){
				recepcionTramiteBodyRequest.setCargoIdUsuarioInicio(recepcionTramiteActual.getCargoUsuarioFin().getId());
				recepcionTramiteBodyRequest.setCargoIdUsuarioFin(recepcionTramiteActual.getCargoUsuarioFin().getId());
			}

			recepcionTramiteBodyRequest.setEstadoInicio(recepcionTramiteActual.getEstadoFin());
			recepcionTramiteBodyRequest.setFechaInicio(new Date());
			recepcionTramiteBodyRequest.setComentarioInicio(recepcionTramiteActual.getComentarioFin());
			if(fechaMaxima!=null)
				recepcionTramiteBodyRequest.setFechaMaximaAtencion(fechaMaxima);
			recepcionTramiteBodyRequest.setId(null);
			recepcionTramiteBodyRequest.setEstadoFin(null);
			recepcionTramiteBodyRequest.setFechaFin(null);
			recepcionTramiteBodyRequest.setComentarioFin(null);

			nuevoRecepcionTramite = registrarTramiteDerivacion(recepcionTramiteBodyRequest);

			tramiteService.actualizarEstadoTramite(recepcionTramiteActual.getTramite().getId(),recepcionTramiteActual.getEstadoFin());
		}

		return nuevoRecepcionTramite;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarAtencionTramiteDerivacion(AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();
		String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
		String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();

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
			//Usuaro inicio
			subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
			subsanarTramiteBodyRequest.setDependenciaIdUsuarioInicio(dependenciaIdUserSession);
			subsanarTramiteBodyRequest.setCargoIdUsuarioInicio(cargoIdUserSession);

			//Usuario fin
			subsanarTramiteBodyRequest.setUsuarioFin(atenderTramiteDerivacion.getUsuarioInicio().getId());
			if(atenderTramiteDerivacion.getDependenciaUsuarioInicio()!=null)
				subsanarTramiteBodyRequest.setDependenciaIdUsuarioFin(atenderTramiteDerivacion.getDependenciaUsuarioInicio().getId());
			if(atenderTramiteDerivacion.getCargoUsuarioInicio()!=null)
				subsanarTramiteBodyRequest.setCargoIdUsuarioFin(atenderTramiteDerivacion.getCargoUsuarioInicio().getId());

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
		String tipoDocumento = registrotramiteDerivacion.getTramite().getTipoDocumento()!=null?registrotramiteDerivacion.getTramite().getTipoDocumento().getTipoDocumento():"-";
		String nombreDestinatario = registrotramiteDerivacion.getUsuarioFin().getNombre() + (StringUtils.isBlank(registrotramiteDerivacion.getUsuarioFin().getApePaterno())?"":" " + registrotramiteDerivacion.getUsuarioFin().getApePaterno()) + (StringUtils.isBlank(registrotramiteDerivacion.getUsuarioFin().getApeMaterno())?"":" " + registrotramiteDerivacion.getUsuarioFin().getApeMaterno());

		String urlTramite = env.getProperty("app.url.linkTramite")+registrotramiteDerivacion.getId();
		String numTramite = String.valueOf(registrotramiteDerivacion.getTramite().getNumeroTramite());
		String fecha = fechaa.format(registrotramiteDerivacion.getCreatedDate());
		String asunto = registrotramiteDerivacion.getTramite().getAsunto();
		String razonSocialEmisor = registrotramiteDerivacion.getTramite().getRazonSocial(); // registrotramiteDerivacion.getUsuarioInicio().getPersona().getRazonSocialNombre();
		//String correoEmisor = registrotramiteDerivacion.getUsuarioInicio().getEmail();

		if(registrotramiteDerivacion.getTramite().getOrigenDocumento().equals("INTERNO"))
			razonSocialEmisor = registrotramiteDerivacion.getTramite().getEntidadExterna()!=null?registrotramiteDerivacion.getTramite().getEntidadExterna().getRazonSocial():"-";

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

		/*
		String bodyHtmlFinal = String.format(msjHTML.toString(), urlTramite, numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, avisoConfidencialidad, codigoEtica, desde, hasta);
		*/
		/*
		String bodyHtmlFinal = String.format(msjHTML.toString(), urlTramite, numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, tipoDocumento, desde, hasta);
		*/
		String bodyHtmlFinal = String.format(msjHTML.toString(), numTramite, nombreDestinatario, numTramite, fecha, asunto, razonSocialEmisor,
				proveido, plazoMaximo, tipoDocumento, urlTramite);

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

	public void alertaTramiteFueraPlazoAtencion () throws Exception {
		//Obtener Lista de Tramites Derivacion, condiciones: estado->P y fechaMaxima>=Hoy
		Date todaysDate = new Date();
		Query query = new Query();
		Criteria criteria = Criteria.where("fechaMaximaAtencion").gte(todaysDate).and("estado").is("P");
		query.addCriteria(criteria);
		List<TramiteDerivacion> tramitePendienteList = mongoTemplate.find(query, TramiteDerivacion.class);
		//Obtener correo de cada usuarioFin de la lista de Tramite Derivacion Pendiente
		Map<String, Boolean> mapaUsuarios = new HashMap<>();
		String usuarioId = null;
		boolean enviarCorreoTramitePendiente;
		for(TramiteDerivacion usuarioTmp : tramitePendienteList){
			enviarCorreoTramitePendiente = true;
			usuarioId = usuarioTmp.getUsuarioFin().getId();
			if(mapaUsuarios.containsKey(usuarioId))
				enviarCorreoTramitePendiente = mapaUsuarios.get(usuarioId);
			else{
				//Obtenemos la configuracion del usuario para ver si corresponde enviarle notificacion
				ConfiguracionUsuario configuracionUsuario = configuracionUsuarioService.obtenerConfiguracionUsuario(usuarioTmp.getUsuarioFin().getId());
				if(configuracionUsuario!=null)
					enviarCorreoTramitePendiente = !StringUtils.isBlank(configuracionUsuario.getEnviarAlertaTramitePendiente())?(configuracionUsuario.getEnviarAlertaTramitePendiente().equals("S")?true:false):true;

				mapaUsuarios.put(usuarioId,enviarCorreoTramitePendiente);
			}
			String correoUsuarioFin = usuarioTmp.getUsuarioFin().getEmail();
			//Enviar correo de alerta a cada usuarioFin
			if(enviarCorreoTramitePendiente)
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
		String nombreDestinatario = tramiteDerivacion.getUsuarioFin().getNombre() + (StringUtils.isBlank(tramiteDerivacion.getUsuarioFin().getApePaterno())?"":" " + tramiteDerivacion.getUsuarioFin().getApePaterno()) + (StringUtils.isBlank(tramiteDerivacion.getUsuarioFin().getApeMaterno())?"":" " + tramiteDerivacion.getUsuarioFin().getApeMaterno());

		String urlTramite = env.getProperty("app.url.linkTramite")+tramiteDerivacion.getId();

		String bodyHtmlFinal = String.format(msjHTML.toString(),numTramite, nombreDestinatario, numTramite, fechaDerivacion,fechaMaximaAtencion,diasAtraso,urlTramite);

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
		String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
		String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();

		TramiteDerivacion subsanarTramiteActual = tramiteDerivacionMongoRepository.findById(rechazarTramiteDerivacionBodyRequest.getId()).get();
		subsanarTramiteActual.setComentarioFin(rechazarTramiteDerivacionBodyRequest.getComentarioInicial());
		subsanarTramiteActual.setEstadoFin(EstadoTramiteConstant.RECHAZADO);
		subsanarTramiteActual.setFechaFin(new Date());
		subsanarTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(subsanarTramiteActual);

		int sec = obtenerSecuencia(subsanarTramiteActual.getTramite().getId());

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setTramiteId(subsanarTramiteActual.getTramite().getId());
		tramiteDerivacionRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioFin().getId());
		List<TramiteDerivacion> tramiteDerivacionList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);
		//Ordenamos por numero de tramite
		Collections.sort(tramiteDerivacionList, new Comparator<TramiteDerivacion>(){
			@Override
			public int compare(TramiteDerivacion a, TramiteDerivacion b)
			{
				return b.getSecuencia() - a.getSecuencia();
			}
		});
		//Si esta ordenado de mayor a menos por las secuencia, entonces el segunco registro sera el ultimo derivado
		TramiteDerivacion tramiteDerivacionAnterior = tramiteDerivacionList.get(1);

		TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = new TramiteDerivacionBodyRequest(); // mapper.map(subsanarTramiteActual, TramiteDerivacionBodyRequest.class);
		subsanarTramiteBodyRequest.setSecuencia(sec);
		subsanarTramiteBodyRequest.setTramiteId(subsanarTramiteActual.getTramite().getId());
		//Usuaro inicio
		subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
		subsanarTramiteBodyRequest.setDependenciaIdUsuarioInicio(dependenciaIdUserSession);
		subsanarTramiteBodyRequest.setCargoIdUsuarioInicio(cargoIdUserSession);
		//subsanarTramiteBodyRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioInicio().getId());

		//Usuario fin
		subsanarTramiteBodyRequest.setUsuarioFin(tramiteDerivacionAnterior.getUsuarioInicio().getId());
		if(tramiteDerivacionAnterior.getDependenciaUsuarioInicio()!=null)
			subsanarTramiteBodyRequest.setDependenciaIdUsuarioFin(tramiteDerivacionAnterior.getDependenciaUsuarioInicio().getId());
		if(tramiteDerivacionAnterior.getCargoUsuarioInicio()!=null)
			subsanarTramiteBodyRequest.setCargoIdUsuarioFin(tramiteDerivacionAnterior.getCargoUsuarioInicio().getId());

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
		//Se comenta esta parte, al parecer no es necesario

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


		//Se coloca solo esta parte en lugar de las lineas comentadas anteriormente
		/*
		Usuario userInicio = new Usuario();
		userInicio.setId(tramiteDerivacionBodyRequest.getUsuarioInicio());
		Usuario userFin = new Usuario();
		userFin.setId(tramiteDerivacionBodyRequest.getUsuarioFin());
		*/

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

		if(!StringUtils.isBlank(tramiteDerivacionBodyRequest.getDependenciaIdUsuarioInicio())){
			Dependencia dependencia =  new Dependencia();
			dependencia.setId(tramiteDerivacionBodyRequest.getDependenciaIdUsuarioInicio());
			registroTramiteDerivacion.setDependenciaUsuarioInicio(dependencia);
		}else{
			registroTramiteDerivacion.setDependenciaUsuarioInicio(null);
		}
		if(!StringUtils.isBlank(tramiteDerivacionBodyRequest.getDependenciaIdUsuarioFin())){
			Dependencia dependencia =  new Dependencia();
			dependencia.setId(tramiteDerivacionBodyRequest.getDependenciaIdUsuarioFin());
			registroTramiteDerivacion.setDependenciaUsuarioFin(dependencia);
		}else{
			registroTramiteDerivacion.setDependenciaUsuarioFin(null);
		}
		//Cargos
		if(!StringUtils.isBlank(tramiteDerivacionBodyRequest.getCargoIdUsuarioInicio())){
			Cargo cargo =  new Cargo();
			cargo.setId(tramiteDerivacionBodyRequest.getCargoIdUsuarioInicio());
			registroTramiteDerivacion.setCargoUsuarioInicio(cargo);
		}else{
			registroTramiteDerivacion.setCargoUsuarioInicio(null);
		}
		if(!StringUtils.isBlank(tramiteDerivacionBodyRequest.getCargoIdUsuarioFin())){
			Cargo cargo =  new Cargo();
			cargo.setId(tramiteDerivacionBodyRequest.getCargoIdUsuarioFin());
			registroTramiteDerivacion.setCargoUsuarioFin(cargo);
		}else{
			registroTramiteDerivacion.setCargoUsuarioFin(null);
		}

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
			//UsuarioBuscarResponse usuarioBuscarResponse = mapper.map(obtenerUsuarioById(registroTramiteDerivacion.getUsuarioFin().getId()),UsuarioBuscarResponse.class);

			//LinkedHashMap<Object, Object> usuario = obtenerUsuarioById(registroTramiteDerivacion.getUsuarioFin().getId());

			//Consultamos si ese cargo fin tiene cargo con derivacion automatica
			if(registroTramiteDerivacion.getCargoUsuarioFin()!=null && !StringUtils.isBlank(registroTramiteDerivacion.getCargoUsuarioFin().getId())){
				String cargoId = registroTramiteDerivacion.getCargoUsuarioFin().getId();
				List<ConfiguracionDerivacionResponse> configuracionDerivacionResponseList = obtenerConfiguracionDerivacionResponseByCargoOrigenId(cargoId);
				for(ConfiguracionDerivacionResponse configuracionDerivacionResponse : configuracionDerivacionResponseList){
					//Obtenemos los usuario que tengan el cargo destino id.
					List<UsuarioCargoResponse> usuarioCargoResponseList = obtenerUsuarioByCargo(configuracionDerivacionResponse.getCargoDestino().getCargo());
					for (UsuarioCargoResponse usuarioCargoResponse: usuarioCargoResponseList) {
						//Registrar derivaciones de copia para cada elemento, primero validamos si ya se registro una copia.
						if(!existeDerivacionCopiaParaUsuarioInicioFinMismoTramite(registroTramiteDerivacion,usuarioCargoResponse)){
							TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = new TramiteDerivacionBodyRequest();
							subsanarTramiteBodyRequest.setTramiteId(registroTramiteDerivacion.getTramite().getId());
							subsanarTramiteBodyRequest.setUsuarioInicio(registroTramiteDerivacion.getUsuarioInicio().getId());
							if(registroTramiteDerivacion.getDependenciaUsuarioInicio()!=null){
								subsanarTramiteBodyRequest.setDependenciaIdUsuarioInicio(registroTramiteDerivacion.getDependenciaUsuarioInicio().getId());
							}
							if(registroTramiteDerivacion.getCargoUsuarioInicio()!=null){
								subsanarTramiteBodyRequest.setCargoIdUsuarioInicio(registroTramiteDerivacion.getCargoUsuarioInicio().getId());
							}
							subsanarTramiteBodyRequest.setUsuarioFin(usuarioCargoResponse.getUsuario().getId());
							subsanarTramiteBodyRequest.setDependenciaIdUsuarioFin(usuarioCargoResponse.getCargo().getDependencia().getId());
							subsanarTramiteBodyRequest.setCargoIdUsuarioFin(usuarioCargoResponse.getCargo().getId());
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

		if(registroTramiteDerivacion.getDependenciaUsuarioInicio()!=null)
			tramiteDerivacionRequest.setDependenciaIdUsuarioInicio(registroTramiteDerivacion.getDependenciaUsuarioInicio().getId());
		if(registroTramiteDerivacion.getCargoUsuarioInicio()!=null)
			tramiteDerivacionRequest.setCargoIdUsuarioInicio(registroTramiteDerivacion.getCargoUsuarioInicio().getId());

		tramiteDerivacionRequest.setUsuarioFin(usuarioCargoResponse.getUsuario().getId());
		tramiteDerivacionRequest.setDependenciaIdUsuarioFin(usuarioCargoResponse.getCargo().getDependencia().getId());
		tramiteDerivacionRequest.setCargoIdUsuarioFin(usuarioCargoResponse.getCargo().getId());

		List<TramiteDerivacion> tramiteDerivacionList = buscarTramiteDerivacionParams(tramiteDerivacionRequest);

		return CollectionUtils.isEmpty(tramiteDerivacionList)?false:true;
	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteDerivacion registrarTramiteDerivacionMigracion(TramiteDerivacionMigracionBodyRequest tramiteDerivacionBodyRequest) throws Exception {

		TramiteDerivacion registroTramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		//colocamos null porque en el tramite actual no hay cargo
		registroTramiteDerivacion.setCargoUsuarioFin(null);
		registroTramiteDerivacion.setCargoUsuarioInicio(null);
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

		Criteria orCriteria = new Criteria();
		Criteria criteriaOr = null;
		Criteria criteriaGlobal = new Criteria();
		List<Criteria> orExpression =  new ArrayList<>();

		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		if(parameters.get("numeroTramite").equals(0)){
			parameters.remove("numeroTramite");
		}

		/*
		List<Criteria> listCriteria =  new ArrayList<>();
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
		*/
		List<Criteria> listCriteria =  new ArrayList<>();
		List<Criteria> listOrCriteria =  new ArrayList<>();

		List<String> tramiteIds = obtenerTramitesId(tramiteDerivacionRequest);
		if(!CollectionUtils.isEmpty(tramiteIds)){
			listOrCriteria.add(Criteria.where("tramite.id").in(tramiteIds.toArray()));
			parameters.remove("tramiteId");
			parameters.remove("numeroTramite");
			parameters.remove("asunto");
			parameters.remove("razonSocial");
			parameters.remove("origenDocumento");

			for (String tramiteId : tramiteIds) {
				Criteria expression = new Criteria();
				expression.and("tramite.id").is(tramiteId);
				orExpression.add(expression);
			}
			//orQuery.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}
		/*
		if(parameters.containsKey("fechaDerivacionDesde") && parameters.containsKey("fechaDerivacionHasta")){
			listCriteria.add(Criteria.where("fechaInicio").gte((Date)parameters.get("fechaDerivacionDesde")).lte((Date)parameters.get("fechaDerivacionHasta")));
			parameters.remove("fechaDerivacionDesde");
			parameters.remove("fechaDerivacionHasta");
		}
		*/
		/*
		if(parameters.containsKey("fechaDerivacionDesde"))
			listCriteria.add(Criteria.where("fechaInicio").gte((Date)parameters.get("fechaDerivacionDesde")));

		if(parameters.containsKey("fechaDerivacionHasta")){
			Date fechaHasta = (Date)parameters.get("fechaDerivacionHasta");
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			//listCriteria.add(Criteria.where("fechaInicio").lte((Date)parameters.get("fechaDerivacionHasta")));
			listCriteria.add(Criteria.where("fechaInicio").lte(fechaHasta));
		}
		*/

		if(parameters.containsKey("usuarioInicio")){
			listCriteria.add(Criteria.where("usuarioInicio.id").is(parameters.get("usuarioInicio")));
			parameters.remove("usuarioInicio");
		}
		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}
		if(parameters.containsKey("dependenciaIdUsuarioInicio")){
			if(parameters.get("dependenciaIdUsuarioInicio")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioInicio").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioInicio.id").is(parameters.get("dependenciaIdUsuarioInicio")));

			parameters.remove("dependenciaIdUsuarioInicio");
		}
		if(parameters.containsKey("dependenciaIdUsuarioFin")){
			if(parameters.get("dependenciaIdUsuarioFin")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioFin").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioFin.id").is(parameters.get("dependenciaIdUsuarioFin")));
			parameters.remove("dependenciaIdUsuarioFin");
		}
		if(parameters.containsKey("notEstadoFin")){
			listCriteria.add(Criteria.where("estadoFin").ne(parameters.get("notEstadoFin")));
			parameters.remove("notEstadoFin");
		}
		if(parameters.containsKey("estadoFin") && parameters.get("estadoFin").toString().equals("PENDIENTE")){
			listCriteria.add(Criteria.where("estado").is("P"));
			parameters.remove("estadoFin");
		}

		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}

		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");
		parameters.remove("fechaDerivacionDesde");
		parameters.remove("fechaDerivacionHasta");

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

		long cantidadRegistro = mongoTemplate.count(andQuery, TramiteDerivacion.class);

		return (int)cantidadRegistro;
	}

	private List<String> obtenerTramitesId(TramiteDerivacionRequest tramiteDerivacionRequest) throws ParseException {

		List<String> idTramites = new ArrayList<>();
		List<Criteria> listCriteria =  new ArrayList<>();
		List<Criteria> andExpression =  new ArrayList<>();
		List<Criteria> orExpression =  new ArrayList<>();
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
		if(!StringUtils.isBlank(tramiteDerivacionRequest.getOrigenDocumento())){
			listCriteria.add(Criteria.where("origenDocumento").is(tramiteDerivacionRequest.getOrigenDocumento()));
		}
		//Si esta marcado origenDocumento INTERNO
		/*
		if(!StringUtils.isBlank(tramiteDerivacionRequest.getOrigenDocumento())
				&& tramiteDerivacionRequest.getOrigenDocumento().equals("INTERNO")
				&& !StringUtils.isBlank(tramiteDerivacionRequest.getRazonSocial())){
			listCriteria.add(Criteria.where("entidadExterna.razonSocial").is(tramiteDerivacionRequest.getRazonSocial()));
		}
		*/
		if(!StringUtils.isBlank(tramiteDerivacionRequest.getRazonSocial())){
			listCriteria.add(Criteria.where("razonSocial").regex(".*"+tramiteDerivacionRequest.getRazonSocial()+".*","i"));
		}

		if(tramiteDerivacionRequest.getFechaDerivacionDesde()!=null)
			listCriteria.add(Criteria.where("createdDate").gte(tramiteDerivacionRequest.getFechaDerivacionDesde()));

		if(tramiteDerivacionRequest.getFechaDerivacionHasta()!=null){
			Date fechaHasta = tramiteDerivacionRequest.getFechaDerivacionHasta();
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			//listCriteria.add(Criteria.where("fechaInicio").lte((Date)parameters.get("fechaDerivacionHasta")));
			listCriteria.add(Criteria.where("createdDate").lte(fechaHasta));
		}
		if(!StringUtils.isBlank(tramiteDerivacionRequest.getRazonSocial())
				&& !StringUtils.isBlank(tramiteDerivacionRequest.getOrigenDocumento())
				&& tramiteDerivacionRequest.getOrigenDocumento().equals("EXTERNO")){
			List<String> usuariosIds = obtenerUsuariosCreacionId(tramiteDerivacionRequest);
			if(!CollectionUtils.isEmpty(usuariosIds)){

				for (String usuarioCreacionId : usuariosIds) {
					Criteria expression = new Criteria();
					expression.and("createdByUser").is(usuarioCreacionId);
					orExpression.add(expression);
				}

			}

		}
		if(!listCriteria.isEmpty() || !CollectionUtils.isEmpty(orExpression)) {
			Criteria orCriteria = new Criteria();
			Criteria criteriaOr = null;
			Criteria criteriaGlobal = new Criteria();
			Criteria criteriaAnd = null;

			if(!CollectionUtils.isEmpty(orExpression))
				criteriaOr = orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()]));

			if(!CollectionUtils.isEmpty(listCriteria)){
				andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
				criteriaAnd = andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()]));
			}

			if(criteriaOr!=null && criteriaAnd==null)
				criteriaGlobal = criteriaGlobal.andOperator(criteriaOr);

			if(criteriaOr==null && criteriaAnd!=null)
				criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd);

			if(criteriaOr!=null && criteriaAnd!=null)
				criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd,criteriaOr);

			andQuery.addCriteria(criteriaGlobal);
			andQuery.fields().include("id");

			List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
			if(!CollectionUtils.isEmpty(tramiteList)){
				tramiteList.stream().forEach(x -> idTramites.add(x.getId()));
			}

			/*
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));

			andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));

			List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
			if(!CollectionUtils.isEmpty(tramiteList)){
				tramiteList.stream().forEach(x -> idTramites.add(x.getId()));
			}
			*/
		}

		return idTramites;
	}


	public Map buscarTramiteDerivacionParamsDashboard(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
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
			parameters.remove("razonSocial");
			parameters.remove("origenDocumento");

			for (String tramiteId : tramiteIds) {
				Criteria expression = new Criteria();
				expression.and("tramite.id").is(tramiteId);
				orExpression.add(expression);
			}
			//orQuery.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}
		/*
		if(parameters.containsKey("fechaDerivacionDesde") && parameters.containsKey("fechaDerivacionHasta")){
			listCriteria.add(Criteria.where("fechaInicio").gte((Date)parameters.get("fechaDerivacionDesde")).lte((Date)parameters.get("fechaDerivacionHasta")));
			parameters.remove("fechaDerivacionDesde");
			parameters.remove("fechaDerivacionHasta");
		}
		*/
		/*
		if(parameters.containsKey("fechaDerivacionDesde"))
			listCriteria.add(Criteria.where("fechaInicio").gte((Date)parameters.get("fechaDerivacionDesde")));

		if(parameters.containsKey("fechaDerivacionHasta")){
			Date fechaHasta = (Date)parameters.get("fechaDerivacionHasta");
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			//listCriteria.add(Criteria.where("fechaInicio").lte((Date)parameters.get("fechaDerivacionHasta")));
			listCriteria.add(Criteria.where("fechaInicio").lte(fechaHasta));
		}
		*/

		if(parameters.containsKey("usuarioInicio")){
			listCriteria.add(Criteria.where("usuarioInicio.id").is(parameters.get("usuarioInicio")));
			parameters.remove("usuarioInicio");
		}
		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}
		if(parameters.containsKey("dependenciaIdUsuarioInicio")){
			if(parameters.get("dependenciaIdUsuarioInicio")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioInicio").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioInicio.id").is(parameters.get("dependenciaIdUsuarioInicio")));
			parameters.remove("dependenciaIdUsuarioInicio");
		}
		if(parameters.containsKey("dependenciaIdUsuarioFin")){
			if(parameters.get("dependenciaIdUsuarioFin")!=null && !StringUtils.isBlank(parameters.get("dependenciaIdUsuarioFin").toString()))
				listCriteria.add(Criteria.where("dependenciaUsuarioFin.id").is(parameters.get("dependenciaIdUsuarioFin")));

			parameters.remove("dependenciaIdUsuarioFin");
		}
		if(parameters.containsKey("estadoFin") && parameters.get("estadoFin").toString().equals("PENDIENTE")){
			listCriteria.add(Criteria.where("estado").is("P"));
			parameters.remove("estadoFin");
		}

		//listCriteria.add(Criteria.where("estadoFin").ne("RECEPCIONADO"));

		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}

		//Agregamos la paginacion
		/*
		if(tramiteDerivacionRequest.getPageNumber()>=0 && tramiteDerivacionRequest.getPageSize()>0){
			Pageable pageable = PageRequest.of(tramiteDerivacionRequest.getPageNumber(), tramiteDerivacionRequest.getPageSize());
			andQuery.with(pageable);
		}
		*/

		//Retiramos las keys de paginacion
		parameters.remove("pageNumber");
		parameters.remove("pageSize");
		parameters.remove("fechaDerivacionDesde");
		parameters.remove("fechaDerivacionHasta");

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

		List<TramiteDerivacion> tramiteDerivacionList = mongoTemplate.find(andQuery, TramiteDerivacion.class);

		//Cargamos la lista de usuario
		LinkedHashMap<String,Integer> listaUsuarios = new LinkedHashMap();
		LinkedHashMap<String,Integer> listaDependencias = new LinkedHashMap();

		if(!CollectionUtils.isEmpty(tramiteDerivacionList)){
			String nombreUsuario = null;
			String nombreDependencia = null;
			for(TramiteDerivacion tramiteDerivacion : tramiteDerivacionList){
				nombreUsuario = tramiteDerivacion.getUsuarioFin().getNombreCompleto();
				nombreDependencia = tramiteDerivacion.getDependenciaUsuarioFin().getNombre();
				//Usuarios
				if(!listaUsuarios.containsKey(nombreUsuario))
					listaUsuarios.put(nombreUsuario, 1);
				else
					listaUsuarios.put(nombreUsuario, listaUsuarios.get(nombreUsuario)+1);

				//Dependencias
				if(!listaDependencias.containsKey(nombreDependencia))
					listaDependencias.put(nombreDependencia, 1);
				else
					listaDependencias.put(nombreDependencia, listaDependencias.get(nombreDependencia)+1);

				/*
				//Usuarios
				if(!listaUsuarios.containsKey(tramiteDerivacion.getUsuarioFin().getId()))
					listaUsuarios.put(tramiteDerivacion.getUsuarioFin().getId(), 0);
				else
					listaUsuarios.put(tramiteDerivacion.getUsuarioFin().getId(), listaUsuarios.get(tramiteDerivacion.getUsuarioFin().getId())+1);

				//Dependencias
				if(!listaDependencias.containsKey(tramiteDerivacion.getDependenciaUsuarioFin().getId()))
					listaDependencias.put(tramiteDerivacion.getDependenciaUsuarioFin().getId(), 0);
				else
					listaDependencias.put(tramiteDerivacion.getDependenciaUsuarioFin().getId(), listaDependencias.get(tramiteDerivacion.getDependenciaUsuarioFin().getId())+1);
				*/
			}
		}

		List listaUsuarioNombre = new LinkedList();
		List listaUsuarioCantidad = new LinkedList();
		List listaDependenciaNombre = new LinkedList();
		List listaDependenciaCantidad = new LinkedList();

		listaUsuarioNombre = listaUsuarios.keySet().stream().collect(Collectors.toList());
		listaUsuarioCantidad = Arrays.asList(listaUsuarios.values().toArray());
		listaDependenciaNombre = listaDependencias.keySet().stream().collect(Collectors.toList());
		listaDependenciaCantidad = Arrays.asList(listaDependencias.values().toArray());

		Map<String, Object> mapaResult = new HashMap<>();
		mapaResult.put("listaUsuarioNombre", listaUsuarioNombre);
		mapaResult.put("listaUsuarioCantidad", listaUsuarioCantidad);
		mapaResult.put("listaDependenciaNombre", listaDependenciaNombre);
		mapaResult.put("listaDependenciaCantidad", listaDependenciaCantidad);

		return mapaResult;
	}

	public void notificar(TramiteDerivacionNotificacionBodyRequest tramiteDerivacionNotificacionBodyRequest) throws Exception {
		//Obtenemos el trmaite derivacion que vamos a notificar
		TramiteDerivacion tramiteDerivacion = tramiteDerivacionMongoRepository.findById(tramiteDerivacionNotificacionBodyRequest.getTramiteDerivacionId()).get();

		String tramiteId = tramiteDerivacion.getTramite().getId();

		//Marcamos con estado fin notificado
		tramiteDerivacion.setEstadoFin(EstadoTramiteConstant.NOTIFICADO);
		//Cuando es mesa de partes puede llegar el indicador de rechazo, con eso se marca como rechazado por mesa de partes
		if(tramiteDerivacionNotificacionBodyRequest.isEsRechazo())
			tramiteDerivacion.setEstadoFin(EstadoTramiteConstant.RECHAZADO);
		tramiteDerivacion.setFechaFin(new Date());
		tramiteDerivacion.setEstado(EstadoTramiteDerivacionConstant.ATENDIDO);
		tramiteDerivacion.setComentarioFin(tramiteDerivacionNotificacionBodyRequest.getMensaje());
		tramiteDerivacionMongoRepository.save(tramiteDerivacion);

		//Colocamos el adjunto como un nuevo adjunto al tramite
		Resource documentoAdjuntoNotificacionResource = null;
		if(tramiteDerivacionNotificacionBodyRequest.getFile()!=null){
			DocumentoAdjuntoBodyRequest documentoAdjuntoBodyRequest = new DocumentoAdjuntoBodyRequest();
			documentoAdjuntoBodyRequest.setTramiteId(tramiteId);
			documentoAdjuntoBodyRequest.setDescripcion("NOTIFICACION");
			if(tramiteDerivacionNotificacionBodyRequest.isEsRechazo())
				documentoAdjuntoBodyRequest.setDescripcion("RECHAZO");
			documentoAdjuntoBodyRequest.setFile(tramiteDerivacionNotificacionBodyRequest.getFile());
			documentoAdjuntoBodyRequest.setTipoAdjunto(TipoAdjuntoConstant.NOTIFICACION_AMSAC);
			if(tramiteDerivacionNotificacionBodyRequest.isEsRechazo())
				documentoAdjuntoBodyRequest.setTipoAdjunto(TipoAdjuntoConstant.RECHAZO_AMSAC);
			DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoBodyRequest);

			//Preparamos el body para el envio de corro de la notificacion
			DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
			documentoAdjuntoRequest.setId(documentoAdjuntoResponse.getId());
			documentoAdjuntoNotificacionResource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);
		}


		StringBuffer cuerpo = obtenerPlantillaHtml("plantillaNotificacion.html");
		Map<String, Object> param = new HashMap<>();
		param.put("correo", tramiteDerivacionNotificacionBodyRequest.getEmail());
		param.put("asunto", "NOTIFICACION TRAMITE DOCUMENTARIO AMSAC - Nro. Tramite: "+tramiteDerivacion.getTramite().getNumeroTramite());
		if(tramiteDerivacionNotificacionBodyRequest.isEsRechazo())
			param.put("asunto", "RECHAZO TRAMITE DOCUMENTARIO AMSAC - Nro. Tramite: "+tramiteDerivacion.getTramite().getNumeroTramite());
		param.put("cuerpo",  String.format(cuerpo.toString(),tramiteDerivacionNotificacionBodyRequest.getMensaje()));
		if(documentoAdjuntoNotificacionResource!=null)
			param.put("file", documentoAdjuntoNotificacionResource);

		//Enviamos el correo
		enviarCorreo(param);

		//Actualizamos estado del tramite
		if(tramiteDerivacionNotificacionBodyRequest.isEsRechazo())
			tramiteService.actualizarEstadoTramite(tramiteId,EstadoTramiteConstant.RECHAZADO);
		else
			tramiteService.actualizarEstadoTramite(tramiteId,EstadoTramiteConstant.NOTIFICADO);

	}

	private void enviarCorreo(Map<String,Object> param){
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("to",param.get("correo").toString());
		//bodyMap.add("to","evelyn.flores@bitall.com.pe");
		bodyMap.add("asunto",param.get("asunto").toString());
		bodyMap.add("cuerpo",param.get("cuerpo").toString());
		if(param.get("file")!=null)
			bodyMap.add("files", param.get("file"));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMailAttach";

		restTemplate.postForEntity( uri, requestEntity, null);

	}

	private StringBuffer obtenerPlantillaHtml(String nombrePlantilla) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(nombrePlantilla);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String strLine;
		StringBuffer msjHTML = new StringBuffer();
		while ((strLine = bufferedReader.readLine()) != null) {
			msjHTML.append(strLine);
		}
		return msjHTML;

	}

	private List<String> obtenerUsuariosCreacionId(TramiteDerivacionRequest tramiteDerivacionRequest){
		String uri = env.getProperty("app.url.seguridad") + "/usuarios?razonSocial="+tramiteDerivacionRequest.getRazonSocial();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		List<String> usuariosIdList = new ArrayList<>();
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET,entity, CommonResponse.class);
		ObjectMapper objectMapper = new ObjectMapper();
		List<UsuarioResponse> usuarioResponseList = objectMapper.convertValue(response.getBody().getData(),new TypeReference<List<UsuarioResponse>>() {});

		if(!CollectionUtils.isEmpty(usuarioResponseList))
			usuarioResponseList.stream().forEach(x -> usuariosIdList.add(x.getId()));

		return usuariosIdList;

	}

	public void cancelar(TramiteDerivacionNotificacionBodyRequest tramiteDerivacionNotificacionBodyRequest) throws Exception {
		//Obtenemos el trmaite derivacion que vamos a notificar
		TramiteDerivacion tramiteDerivacion = tramiteDerivacionMongoRepository.findById(tramiteDerivacionNotificacionBodyRequest.getTramiteDerivacionId()).get();

		String tramiteId = tramiteDerivacion.getTramite().getId();

		//Marcamos con estado fin notificado
		tramiteDerivacion.setEstadoFin(EstadoTramiteConstant.CANCELADO);
		tramiteDerivacion.setFechaFin(new Date());
		tramiteDerivacion.setEstado(EstadoTramiteDerivacionConstant.ATENDIDO);
		tramiteDerivacion.setComentarioFin(tramiteDerivacionNotificacionBodyRequest.getMensaje());
		tramiteDerivacionMongoRepository.save(tramiteDerivacion);

		//Colocamos el adjunto como un nuevo adjunto al tramite
		DocumentoAdjuntoBodyRequest documentoAdjuntoBodyRequest = new DocumentoAdjuntoBodyRequest();
		documentoAdjuntoBodyRequest.setTramiteId(tramiteId);
		documentoAdjuntoBodyRequest.setDescripcion("SUSTENTO DE CANCELACIÓN");
		documentoAdjuntoBodyRequest.setFile(tramiteDerivacionNotificacionBodyRequest.getFile());
		documentoAdjuntoBodyRequest.setTipoAdjunto(TipoAdjuntoConstant.CANCELACION_AMSAC);
		DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoBodyRequest);

		//Preparamos el body para el envio de corro de la notificacion
		DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
		documentoAdjuntoRequest.setId(documentoAdjuntoResponse.getId());
		Resource documentoAdjuntoNotificacionResource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);

		StringBuffer cuerpo = obtenerPlantillaHtml("plantillaCancelacion.html");
		Map<String, Object> param = new HashMap<>();
		param.put("correo", tramiteDerivacionNotificacionBodyRequest.getEmail());
		param.put("asunto", "CANCELACION TRAMITE DOCUMENTARIO AMSAC - Nro. Tramite: "+tramiteDerivacion.getTramite().getNumeroTramite());
		param.put("cuerpo",  String.format(cuerpo.toString(),tramiteDerivacionNotificacionBodyRequest.getMensaje()));
		param.put("file", documentoAdjuntoNotificacionResource);

		//Enviamos el correo
		enviarCorreo(param);

		//Actualizamos estado del tramite
		tramiteService.actualizarEstadoTramite(tramiteId,EstadoTramiteConstant.CANCELADO);

	}

	public void save(TramiteDerivacion tramiteDerivacion){
		tramiteDerivacionMongoRepository.save(tramiteDerivacion);
	}


	private List<UsuarioResponse> obtenerUsuarioByUsuario(String usuario){
		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.seguridad") + "/usuarios?email="+usuario;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);

		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET,entity, CommonResponse.class);
		ObjectMapper objectMapper = new ObjectMapper();
		List<UsuarioResponse> usuarioResponseList = objectMapper.convertValue(response.getBody().getData(),new TypeReference<List<UsuarioResponse>>() {});

		//ResponseEntity<CommonResponse> response = restTemplate.exchange(uri,HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		return usuarioResponseList;
	}


	public Map atenderDerivacionLote(AtenderDerivacionLoteRequest atenderDerivacionLoteRequest) throws Exception {
		Map<String, Object> param = new HashMap<>();
		String cadenaResultado = "";
		String derivacionesIdCerrados = "";

		//Obtener id del usuario
		List<UsuarioResponse> usuarioResponseList = obtenerUsuarioByUsuario(atenderDerivacionLoteRequest.getUsuario());
		if(usuarioResponseList.size()>1)
			throw new Exception("Mas de un registro para usuario");
		
		String usuarioId = usuarioResponseList.get(0).getId();
		/*
		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setUsuarioFin(usuarioId);
		tramiteDerivacionRequest.setEstado("P");
		*/

		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		Criteria criteriaGlobal = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();

		//Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("usuarioFin",usuarioId);
		parameters.put("estado","P");

		List<Criteria> listCriteria =  new ArrayList<>();

		if(parameters.containsKey("usuarioFin")){
			listCriteria.add(Criteria.where("usuarioFin.id").is(parameters.get("usuarioFin")));
			parameters.remove("usuarioFin");
		}

		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}

		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);

		Criteria criteriaAnd = andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()]));

		criteriaGlobal = criteriaGlobal.andOperator(criteriaAnd);

		andQuery.addCriteria(criteriaGlobal);

		List<TramiteDerivacion> tramiteDerivacionList = mongoTemplate.find(andQuery, TramiteDerivacion.class);

		List<Integer> listaNumeroTramitesPendientes = new ArrayList<>();

		if(!StringUtils.isBlank(atenderDerivacionLoteRequest.getTramites())){
			String[] splitArray = atenderDerivacionLoteRequest.getTramites().split(",");
			for (int i = 0; i < splitArray.length; i++) {
				//array[i] = Integer.parseInt(splitArray[i]);
				listaNumeroTramitesPendientes.add(Integer.parseInt(splitArray[i]));
			}
		}

		Predicate<TramiteDerivacion> predicate = x -> !listaNumeroTramitesPendientes.contains(x.getTramite().getNumeroTramite());
		List<TramiteDerivacion> tramiteDerivacionListFinal = tramiteDerivacionList.stream().filter(predicate).collect(Collectors.toList());

		for(TramiteDerivacion tramiteDerivacion : tramiteDerivacionListFinal){
			tramiteDerivacion.setEstado("A");
			tramiteDerivacion.setFechaFin(new Date());
			tramiteDerivacion.setEstadoFin(EstadoTramiteConstant.ATENDIDO);
			cadenaResultado = cadenaResultado + tramiteDerivacion.getTramite().getNumeroTramite() + ",";
			derivacionesIdCerrados = derivacionesIdCerrados + tramiteDerivacion.getId() + ",";
			save(tramiteDerivacion);
		}

		param.put("tramitesCerrados", cadenaResultado.substring(0,cadenaResultado.length()-1));
		param.put("derivacionesIdCerrados", derivacionesIdCerrados.substring(0,derivacionesIdCerrados.length()-1));
		return param;
	}

}
