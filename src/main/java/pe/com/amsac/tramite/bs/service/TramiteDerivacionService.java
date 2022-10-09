package pe.com.amsac.tramite.bs.service;

import org.apache.commons.collections.CollectionUtils;
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
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.body.bean.SubsanacionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.AtencionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DerivarTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.TramiteDerivacionMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
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

	public List<TramiteDerivacion> obtenerTramiteDerivacionPendientes() throws Exception {
		//Obtener Usuario
		/*
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		DatosToken datosToken = (DatosToken)authentication.getPrincipal();

		String idUser =  datosToken.getIdUser();
		*/
		String idUser =  securityHelper.obtenerUserIdSession();

		Query query = new Query();
		Criteria criteria = Criteria.where("usuarioFin.id").is(idUser).and("estado").is("P");
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
			tramiteDerivacion.setCargoNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			tramiteDerivacion.setDependenciaNombreUsuarioInicio(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioInicioNombreCompleto(nombreCompleto);

			//Se completan datos de usuario Fin
			uriBusqueda = uri + tramiteDerivacion.getUsuarioFin().getId();
			response = restTemplate.exchange(uriBusqueda, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			tramiteDerivacion.setCargoNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("cargoNombre").toString());
			tramiteDerivacion.setDependenciaNombreUsuarioFin(((LinkedHashMap)response.getBody().getData()).get("dependenciaNombre").toString());
			nombreCompleto = ((LinkedHashMap)response.getBody().getData()).get("nombre").toString() + " " + ((LinkedHashMap)response.getBody().getData()).get("apePaterno").toString() + ((((LinkedHashMap)response.getBody().getData()).get("apeMaterno")!=null)?" "+((LinkedHashMap)response.getBody().getData()).get("apeMaterno").toString():"");
			tramiteDerivacion.setUsuarioFinNombreCompleto(nombreCompleto);

		}

		return tramitePendienteList;
	}

	public TramiteDerivacion obtenerTramiteDerivacionById(String id) throws Exception {
		TramiteDerivacion obtenerTramiteDerivacionById = tramiteDerivacionMongoRepository.findById(id).get();
		return obtenerTramiteDerivacionById;
	}

	public List<TramiteDerivacion> buscarTramiteDerivacionParams(TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(tramiteDerivacionRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);
		if(parameters.get("numeroTramite").equals(0)){
			parameters.remove("numeroTramite");
		}
		List<Criteria> listCriteria =  new ArrayList<>();
		if(parameters.containsKey("tramiteId"))
			listCriteria.add(Criteria.where("tramite.id").is(parameters.get("tramiteId")));
		if(parameters.containsKey("numeroTramite"))
			listCriteria.add(Criteria.where("tramite.numeroTramite").is(parameters.get("numeroTramite")));
		if(parameters.containsKey("emailEmisor"))
			listCriteria.add(Criteria.where("usuarioInicio.email").is(parameters.get("emailEmisor")));
		if(parameters.containsKey("tipoDocumentoId"))
			listCriteria.add(Criteria.where("tramite.tipoDocumento.id").is(parameters.get("tipoDocumentoId")));
		if(parameters.containsKey("fechaDocumentoDesde") && parameters.containsKey("fechaDocumentoHasta"))
			listCriteria.add(Criteria.where("tramite.fechaDocumento").gte(parameters.get("fechaDocumentoDesde")).lte(parameters.get("fechaDocumentoHasta")));
		if(parameters.containsKey("fechaCreacionDesde") && parameters.containsKey("fechaCreaciontoHasta"))
			listCriteria.add(Criteria.where("tramite.createdDate").gte(parameters.get("fechaCreacionDesde")).lte(parameters.get("fechaCreaciontoHasta")));
		if(!listCriteria.isEmpty()) {
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
		}
		Criteria expression = new Criteria();
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<TramiteDerivacion> tramiteList = mongoTemplate.find(andQuery, TramiteDerivacion.class);
		return tramiteList;
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

		TramiteDerivacion registrotramiteDerivacion = mapper.map(tramiteDerivacionBodyRequest,TramiteDerivacion.class);
		registrotramiteDerivacion.setUsuarioInicio(userInicio);
		registrotramiteDerivacion.setTramite(tramiteMongoRepository.findById(tramiteDerivacionBodyRequest.getTramiteId()).get());
		registrotramiteDerivacion.setEstado("P");

		int sec = obtenerSecuencia(tramiteDerivacionBodyRequest.getTramiteId());
		registrotramiteDerivacion.setSecuencia(sec);

		tramiteDerivacionMongoRepository.save(registrotramiteDerivacion);
		//Invocar a servicio para envio de correo
		//modificar susbanacion
		envioCorreoDerivacion(registrotramiteDerivacion);

		return registrotramiteDerivacion;

	}

	public TramiteDerivacion subsanarTramiteDerivacion(SubsanacionTramiteDerivacionBodyRequest subsanartramiteDerivacionBodyrequest) throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();

		TramiteDerivacion subsanarTramiteActual = tramiteDerivacionMongoRepository.findById(subsanartramiteDerivacionBodyrequest.getId()).get();
		subsanarTramiteActual.setComentarioInicio(subsanartramiteDerivacionBodyrequest.getComentarioInicial());
		subsanarTramiteActual.setEstadoFin("SUBSANACION");
		subsanarTramiteActual.setFechaFin(new Date());
		subsanarTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(subsanarTramiteActual);

		int sec = obtenerSecuencia(subsanarTramiteActual.getTramite().getId());

		TramiteDerivacionBodyRequest subsanarTramiteBodyRequest = mapper.map(subsanarTramiteActual, TramiteDerivacionBodyRequest.class);
		subsanarTramiteBodyRequest.setSecuencia(sec);
		subsanarTramiteBodyRequest.setUsuarioInicio(usuarioId);
		subsanarTramiteBodyRequest.setUsuarioFin(subsanarTramiteActual.getUsuarioInicio().getId());
		subsanarTramiteBodyRequest.setEstadoInicio(subsanarTramiteActual.getEstadoFin());
		subsanarTramiteBodyRequest.setFechaInicio(new Date());
		subsanarTramiteBodyRequest.setForma("ORIGINAL");
		subsanarTramiteBodyRequest.setId(null);
		subsanarTramiteBodyRequest.setEstadoFin(null);
		subsanarTramiteBodyRequest.setFechaFin(null);
		subsanarTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(subsanarTramiteBodyRequest);

		//Enviar correo para subsanacion
		//envioCorreoSubsanacion(nuevoDerivacionTramite);

		return nuevoDerivacionTramite;
	}

	public TramiteDerivacion registrarDerivacionTramite(DerivarTramiteBodyRequest derivartramiteBodyrequest) throws Exception {

		String usuarioId = securityHelper.obtenerUserIdSession();

		TramiteDerivacion derivacionTramiteActual = tramiteDerivacionMongoRepository.findById(derivartramiteBodyrequest.getId()).get();
		derivacionTramiteActual.setEstadoFin("DERIVADO");
		derivacionTramiteActual.setFechaFin(new Date());
		derivacionTramiteActual.setProveidoAtencion(derivartramiteBodyrequest.getProveidoAtencion());
		derivacionTramiteActual.setComentarioFin(derivartramiteBodyrequest.getComentarioFin());
		derivacionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(derivacionTramiteActual);

		//int sec = obtenerSecuencia(derivacionTramiteActual.getId()).get(0).getSecuencia();
		int sec = obtenerSecuencia(derivacionTramiteActual.getTramite().getId());

		TramiteDerivacionBodyRequest derivacionTramiteBodyRequest = mapper.map(derivacionTramiteActual, TramiteDerivacionBodyRequest.class);
		derivacionTramiteBodyRequest.setSecuencia(sec);
		derivacionTramiteBodyRequest.setUsuarioInicio(usuarioId);
		derivacionTramiteBodyRequest.setUsuarioFin(derivartramiteBodyrequest.getUsuarioFin());
		derivacionTramiteBodyRequest.setEstadoInicio(derivacionTramiteActual.getEstadoFin());
		derivacionTramiteBodyRequest.setFechaInicio(new Date());
		derivacionTramiteBodyRequest.setFechaMaximaAtencion(derivartramiteBodyrequest.getFechaMaximaAtencion());
		derivacionTramiteBodyRequest.setComentarioInicio(derivacionTramiteActual.getComentarioFin());
		derivacionTramiteBodyRequest.setId(null);
		derivacionTramiteBodyRequest.setEstadoFin(null);
		derivacionTramiteBodyRequest.setFechaFin(null);
		derivacionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoDerivacionTramite = registrarTramiteDerivacion(derivacionTramiteBodyRequest);

		return nuevoDerivacionTramite;
	}

	public TramiteDerivacion registrarRecepcionTramiteDerivacion(String id) throws Exception {

		TramiteDerivacion recepcionTramiteActual = tramiteDerivacionMongoRepository.findById(id).get();
		recepcionTramiteActual.setEstadoFin("RECEPCIONADO");
		recepcionTramiteActual.setFechaFin(new Date());
		recepcionTramiteActual.setComentarioFin("Se recepciona tramite para verificacion");
		recepcionTramiteActual.setEstado("A");
		tramiteDerivacionMongoRepository.save(recepcionTramiteActual);

		//int sec = obtenerSecuencia(recepcionTramiteActual.getId()).get(0).getSecuencia();
		int sec = obtenerSecuencia(recepcionTramiteActual.getTramite().getId());

		TramiteDerivacionBodyRequest recepcionTramiteBodyRequest = mapper.map(recepcionTramiteActual, TramiteDerivacionBodyRequest.class);
		recepcionTramiteBodyRequest.setSecuencia(sec);
		recepcionTramiteBodyRequest.setUsuarioInicio(recepcionTramiteActual.getUsuarioFin().getId());
		recepcionTramiteBodyRequest.setUsuarioFin(recepcionTramiteActual.getUsuarioFin().getId());
		recepcionTramiteBodyRequest.setEstadoInicio(recepcionTramiteActual.getEstadoFin());
		recepcionTramiteBodyRequest.setFechaInicio(new Date());
		recepcionTramiteBodyRequest.setComentarioInicio(recepcionTramiteActual.getComentarioFin());
		recepcionTramiteBodyRequest.setId(null);
		recepcionTramiteBodyRequest.setEstadoFin(null);
		recepcionTramiteBodyRequest.setFechaFin(null);
		recepcionTramiteBodyRequest.setComentarioFin(null);

		TramiteDerivacion nuevoRecepcionTramite = registrarTramiteDerivacion(recepcionTramiteBodyRequest);

		return nuevoRecepcionTramite;
	}

	public TramiteDerivacion registrarAtencionTramiteDerivacion(AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		TramiteDerivacion atenderTramiteDerivacion = tramiteDerivacionMongoRepository.findById(atenciontramiteDerivacionBodyrequest.getId()).get();
		atenderTramiteDerivacion.setEstadoFin(atenciontramiteDerivacionBodyrequest.getEstadoFin());
		atenderTramiteDerivacion.setFechaFin(new Date());
		atenderTramiteDerivacion.setComentarioFin(atenciontramiteDerivacionBodyrequest.getComentarioFin());
		atenderTramiteDerivacion.setEstado("A");
		tramiteDerivacionMongoRepository.save(atenderTramiteDerivacion);

		//Ahora se actualiza el estado del tramite
		Tramite tramite = tramiteService.findById(atenderTramiteDerivacion.getTramite().getId());
		tramite.setEstado(atenciontramiteDerivacionBodyrequest.getEstadoFin());
		tramiteService.save(tramite);

		return atenderTramiteDerivacion;
	}

	//public List<TramiteDerivacion> obtenerSecuencia(String id){
	public int obtenerSecuencia(String id){
		int secuencia = 1;
		Query query = new Query();
		//Criteria criteria = Criteria.where("tramite.id").is(id).and("estado").is("A");
		Criteria criteria = Criteria.where("tramite.id").is(id);//.and("estado").is("A");
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
			forma = "PENDIENTE DE ATENCION";
		else
			forma = "TRAMITE - PARA SU CONOCIMIENTO";

		//Correo: Destinatario
		String correoDestinatario = subsanartramiteDerivacion.getUsuarioFin().getEmail();

		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat Formato = new SimpleDateFormat("dd/mm/yyyy");
		DateFormat fechaa = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

		//Armar el body del Email
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

		Map<String, String> params = new HashMap<String, String>();
		params.put("to", correoDestinatario);
		params.put("subject", forma);
		params.put("text", "bodyHtmlFinal");

		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		restTemplate.postForEntity( uri, params, null);


		//TODO Enviar al destinatario que se esta derivando.

	}

	public void envioCorreoDerivacion(TramiteDerivacion registrotramiteDerivacion) throws IOException {
		//TODO Armar mensaje del cuerpo de correo, obteniendo la plantillaDerivacion.html
		//Si la forma es ORIGINAL, se envia como pendientes, pero si es COPIA entonces que el mensaje indique que le ha llegago tramite como copia
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("plantillaDerivacion.html");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		String strLine;
		StringBuffer msjHTML = new StringBuffer();
		while ((strLine = bufferedReader.readLine()) != null) {
			msjHTML.append(strLine);
		}
		//Asunto: Segun Forma
		String forma;
		if(registrotramiteDerivacion.getForma().equals("ORIGINAL"))
			forma = "PENDIENTE DE ATENCION";
		else
			forma = "TRAMITE - PARA SU CONOCIMIENTO";

		//Correo: Destinatario
		String correoDestinatario = registrotramiteDerivacion.getUsuarioFin().getEmail();

		DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat Formato = new SimpleDateFormat("dd/mm/yyyy");
		DateFormat fechaa = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");

		//Armar el body del Email
		String plazoMaximo = "";
		String hasta = "";
		String urlTramite = "linkdeprueba";
		String numTramite = String.valueOf(registrotramiteDerivacion.getTramite().getNumeroTramite());
		String fecha = fechaa.format(registrotramiteDerivacion.getCreatedDate());
		String asunto = registrotramiteDerivacion.getTramite().getAsunto();
		String razonSocialEmisor = registrotramiteDerivacion.getUsuarioInicio().getPersona().getRazonSocialNombre();
		String correoEmisor = registrotramiteDerivacion.getUsuarioInicio().getEmail();
		String proveido = registrotramiteDerivacion.getProveidoAtencion();
		if(registrotramiteDerivacion.getFechaMaximaAtencion()!=null)
			plazoMaximo = fechaa.format(registrotramiteDerivacion.getFechaMaximaAtencion());

		String horaRecepcion = hourFormat.format(registrotramiteDerivacion.getCreatedDate());
		String avisoConfidencialidad = registrotramiteDerivacion.getTramite().getAvisoConfidencial();
		String codigoEtica = registrotramiteDerivacion.getTramite().getCodigoEtica();
		String desde = Formato.format(registrotramiteDerivacion.getCreatedDate());
		if(registrotramiteDerivacion.getFechaMaximaAtencion()!=null)
			hasta = Formato.format(registrotramiteDerivacion.getFechaMaximaAtencion());

		String bodyHtmlFinal = String.format(msjHTML.toString(), urlTramite, numTramite, fecha, asunto, razonSocialEmisor,
				correoEmisor, proveido, plazoMaximo, horaRecepcion, avisoConfidencialidad, codigoEtica, desde, hasta);

		Map<String, String> params = new HashMap<String, String>();
		params.put("to", correoDestinatario);
		params.put("subject", forma);
		params.put("text", bodyHtmlFinal);

		RestTemplate restTemplate = new RestTemplate();
		String uri = env.getProperty("app.url.mail") + "/api/mail/sendMail";
		restTemplate.postForEntity( uri, params, null);


		//TODO Enviar al destinatario que se esta derivando.

	}
}
