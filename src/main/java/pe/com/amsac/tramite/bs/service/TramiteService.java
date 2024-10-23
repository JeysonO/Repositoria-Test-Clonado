package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.pdf.PdfDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.bean.*;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.CustomMultipartFile;
import pe.com.amsac.tramite.api.util.InternalErrorException;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.*;
import pe.com.amsac.tramite.bs.util.*;
import pe.com.amsac.tramite.pide.soap.cuo.request.*;
import pe.com.amsac.tramite.pide.soap.cuo.request.ObjectFactory;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPCUOConnector;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPConnector;
import pe.com.amsac.tramite.pide.soap.tramite.request.*;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Stream;

@Service
@Slf4j
//TODO validar si dejar el requestscope
//@RequestScope
public class TramiteService {

	@Autowired
	private TramiteJPARepository tramiteJPARepository;

	@Autowired
	private TramitePrioridadService tramitePrioridadService;

	//@Autowired
	//private TramiteMigracionJPARepository tramiteMigracionJPARepository;

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private UsuarioJPARepository usuarioMongoRepository;

	@Autowired
	private TipoDocumentoJPARepository tipoDocumentoJPARepository;

	@Autowired
	private TramiteEntidadExternaJPARepository tramiteEntidadExternaJPARepository;

	@Autowired
	private TramiteEntidadInternaJPARepository tramiteEntidadInternaJPARepository;

	@Autowired
	private SOAPConnector soapConnector;

	@Autowired
	private SOAPCUOConnector soapCuoConnector;

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
	private TipoDocumentoPideJPARepository tipoDocumentoPideJPARepository;

	@Autowired
	private Util util;

	@Autowired
	private TramiteEnvioPideJPARepository tramiteEnvioPideJPARepository;

	@Autowired
	private FirmaDocumentoService firmaDocumentoService;

	@Autowired
	private TipoTramiteJPARepository tipoTramiteJPARepository;

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	@Autowired
	private UsuarioFirmaService usuarioFirmaService;

	Map<String, Object> filtroParam = new HashMap<>();

	public List<Tramite> buscarTramiteParams(TramiteRequest tramiteRequest) throws Exception {
		/*
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
		andQuery.with(Sort.by(
				Sort.Order.desc("numeroTramite")
		));

		List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
		*/

		Long cantidadMaximaIntentos = Long.parseLong(env.getProperty("app.micelaneos.cantidad-maxima-intentos").toString());

		Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);

		if(parameters.containsKey("soloOriginal")){
			parameters.remove("soloOriginal");
		}
		if((Integer) parameters.get("numeroTramite")==0) {
			parameters.remove("numeroTramite");
		}
		if(tramiteRequest.getFechaCreaciontoHasta()!=null){
			Date fechaHasta = tramiteRequest.getFechaCreaciontoHasta();
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			parameters.put("fechaCreaciontoHasta",fechaHasta);
		}
		if(tramiteRequest.getTipoTramiteId()!=null){
			parameters.put("tipoTramiteId",tramiteRequest.getTipoTramiteId());
		}
		if(!StringUtils.isBlank(tramiteRequest.getMisTramite())){
			parameters.remove("misTramite");
			parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
			//listCriteria.add(Criteria.where("createdByUser").regex(".*"+securityHelper.obtenerUserIdSession()+".*"));
			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				parameters.put("dependenciaUsuarioCreacion",dependenciaIdUserSession);
			}
		}

		List<Tramite> tramiteList = tramiteJPARepository.findByParams(parameters,"numeroTramite",null,tramiteRequest.getPageNumber(),tramiteRequest.getPageSize());

		Optional.ofNullable(tramiteList)
				.map(Collection::stream)
				.orElseGet(Stream::empty).forEach( x -> {
					x.setEntidadExterna(obtenerTramiteEntidadExternaByTramiteId(x.getId()));
					x.setEntidadInterna(obtenerTramiteEntidadInternaByTramiteId(x.getId()));
					x.setCantidadMaximaIntentos(cantidadMaximaIntentos);
				});

		/*
		tramiteList.stream().forEach( x -> {
			x.setEntidadExterna(obtenerTramiteEntidadExternaByTramiteId(x.getId()));
			x.setEntidadInterna(obtenerTramiteEntidadInternaByTramiteId(x.getId()));
		});
		*/

		return tramiteList;
	}

	public List<Tramite> buscarHistorialTramite(Map<String, Object> param) throws Exception {

		//Buscar Historico y ordenar por fecha mas reciente
		/*
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
		*/

		List<Tramite> tramite = tramiteJPARepository.findByParams(param,"createdDate",null,0,0);

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
		//Origen Documento es el atributo que me indica si el documento ha sido registrado por un usuario externo o uno interno
		//Si es externo entonces viene de un usuario externo
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.EXTERNO)){
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
		}
		//Tramites generados de forma interna, lo hace un usuario interno, ya sea de origen interno o externo, este ultimo si estan regularizando un documento que ingreso un documento en fisico.
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.INTERNO)){
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
		//Estos trammites vienen de la recepcion de tramite pide, desde la mesa de partes virtual de pide
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.PIDE)){

			/*
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			tramite.setCargoUsuarioCreacion(null);
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));
			*/

			tramite.setEntidadInterna(null);
			tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
			tramite.setDependenciaDestino(null);
			tramite.setCreatedDate(new Date());
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));

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
			Tramite tramiteTemporal = tramiteJPARepository.findById(tramite.getId()).get();
			tramite.setCreatedByUser(tramiteTemporal.getCreatedByUser());
			tramite.setCreatedDate(tramiteTemporal.getCreatedDate());
			tramite.setNumeroTramite(tramiteTemporal.getNumeroTramite());
		}

		tramite.setTipoTramite(generarTipoTramite(tramiteBodyRequest));

		if(tramite.getTramitePrioridad()==null)
			tramite.setTramitePrioridad(tramitePrioridadService.findByAllTramitePrioridad().stream().filter(x -> x.getPrioridad().equals("2")).findFirst().get()); //Seteamos la prioridad Normal = 2

		tramiteJPARepository.save(tramite);
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.EXTERNO)){
			registrarDerivacion(tramite);
			Map param = generarReporteAcuseTramite(tramite);
			DocumentoAdjuntoResponse documentoAdjuntoResponse = registrarAcuseComoDocumentoDelTramite(param);
			param.put("documentoAdjuntoId",documentoAdjuntoResponse.getId());
			enviarAcuseTramite(param);
		}

		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.PIDE)){
			Map param = generarReporteAcuseTramiteInteroperabilidad(tramite,tramiteBodyRequest.getDependenciaInternaDestinoTramitePide());
			tramiteBodyRequest.setId(tramite.getId());
			firmarDocumentoAcuse(param, tramiteBodyRequest.getPinFirma(), tramiteBodyRequest.getId());
			//Nos aseguramos que se haya firmado el documento para continuar, sino lanzamos excepcion
			actualizarAcuseComoDocumentoDelTramite(tramite.getId());
		}

		//Guardamos datos de la entidad interna
		if(tramite.getEntidadExterna()!=null){
			registrarTramiteEntidadExterna(tramite);
		}
		if(tramite.getEntidadInterna()!=null){
			registrarTramiteEntidadInterna(tramite);
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
		/*
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
		*/
		/*
		int numeroTramite=1;
		Optional<Tramite> tramite = tramiteJPARepository.obtenerUltimoRegistroMaxNumeroTramite(PageRequest.of(0, 1, Sort.by("numeroTramite").descending())).get().findFirst();
		if(tramite.isPresent()){
			numeroTramite = tramite.get().getNumeroTramite()+1;
		}
		*/
		return tramiteJPARepository.obtenerUltimoRegistroMaxNumeroTramite().intValue();
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
		/*
		Collections.sort(tramiteList, new Comparator<Tramite>(){
			@Override
			public int compare(Tramite a, Tramite b)
			{
				return Long.compare(b.getCreatedDate().getTime(), a.getCreatedDate().getTime());
			}
		});
		*/

		return tramiteList;
	}

	public Tramite findById(String id){
		return tramiteJPARepository.findById(id).get();
	}

	public Tramite save(Tramite tramite){
		return tramiteJPARepository.save(tramite);
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

	public boolean documentoFirmadoPide(MultipartFile multipartFile) {
		try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
			List<PDSignature> signatures = document.getSignatureDictionaries();
			return signatures != null && !signatures.isEmpty();
		} catch (IOException e) {
			log.error("Error",e);
			return false;
		}
	}

	public MultipartFile agregarDependencia(MultipartFile file, String tramiteDependencia) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDPage page = document.getPage(0);
			float pageWidth = page.getMediaBox().getWidth();
			float pageHeight = page.getMediaBox().getHeight();
			float x = pageWidth / 3;
			float y = pageHeight - 30;
			float angle = 0;

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
				contentStream.saveGraphicsState();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
				contentStream.setNonStrokingColor(205, 205, 205); // Color gris
				contentStream.beginText();
				contentStream.setTextMatrix(
						(float) Math.cos(angle),
						(float) Math.sin(angle),
						(float) -Math.sin(angle),
						(float) Math.cos(angle),
						x,
						y
				);
				contentStream.showText("N° Doc. Dependencia: " + tramiteDependencia);
				contentStream.endText();
				contentStream.restoreGraphicsState();
			}

			document.save(outputStream); // Guardar en el outputStream
		}

		MultipartFile filePrincipalMarcaAgua= new CustomMultipartFile(outputStream.toByteArray(), "watermarked.pdf","application/pdf");
		return filePrincipalMarcaAgua;
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
		TipoDocumentoTramite tipoDocumento = tipoDocumentoJPARepository.findById(tramite.getTipoDocumento().getId()).get();

		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("numeroTramite", tramite.getNumeroTramite());
		parameters.put("tipoDocumento", tipoDocumento.getTipoDocumento().toUpperCase());
		parameters.put("fechaGeneracion", fechaGeneracion);
		parameters.put("fechaHoraIngreso", fechaGeneracion);
		parameters.put("estado", "EN CUSTODIA ELECTRÓNICA POR AMSAC");
		parameters.put("emisorNombreCompleto", user.getNombreCompleto());
		parameters.put("emisorRazonSocial", person.getRazonSocialNombre().toUpperCase());
		parameters.put("emisorRuc", person.getNumeroDocumento());
		//parameters.put("emisorRazonSocial", user.getPersona().getRazonSocialNombre().toUpperCase());
		//parameters.put("emisorRuc", user.getPersona().getNumeroDocumento());
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
		documentoAdjuntoRequest.setSeccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL);
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
		/*
		DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
		documentoAdjuntoRequest.setTramiteId(tramiteId);
		documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.ACUSE_RECIBO_TRAMITE_AMSAC);
		*/
		DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.obtenerDocumentoAdjuntoList(DocumentoAdjuntoRequest.builder()
				.tramiteId(tramiteId)
				.tipoAdjunto(TipoAdjuntoConstant.ACUSE_RECIBO_TRAMITE_AMSAC)
				.build()).get(0);
		//DocumentoAdjuntoResponse documentoAdjuntoResponse = documentoAdjuntoService.obtenerDocumentoAdjuntoList(documentoAdjuntoRequest).get(0);

		//Obtenemos el resource del acuse a enviar
		/*
		documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
		documentoAdjuntoRequest.setId(documentoAdjuntoResponse.getId());
		*/
		//Resource resource = documentoAdjuntoService.obtenerDocumentoAdjunto(documentoAdjuntoRequest);
		Resource resource = documentoAdjuntoService.obtenerDocumentoAdjunto(DocumentoAdjuntoRequest.builder().id(documentoAdjuntoResponse.getId()).build());

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

		//tramite.setEstado("A");
		tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
		tramite.setCargoUsuarioCreacion(null); //No va porque no registran cargo
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			//tramite.setCargoUsuarioCreacion(null); //No va porque no registran cargo
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
		tramiteMigracionJPARepository.save(tramite);
		return tramite;
		*/

		return null;

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
			Tramite tramite = tramiteJPARepository.findById(tramiteId).get();
			tramite.setEstado(estadoTramite);
			tramiteJPARepository.save(tramite);
		}
	}

	public int totalRegistros(TramiteRequest tramiteRequest) throws Exception {
		/*
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
			listCriteria.add(Criteria.where("asunto").regex(".*"+parameters.get("asunto")+".*"));
			parameters.remove("asunto");
		}

		if(parameters.containsKey("razonSocial")){
			listCriteria.add(Criteria.where("razonSocial").regex(".*"+parameters.get("razonSocial")+".*","i"));
			parameters.remove("razonSocial");
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
		*/

		Map<String, Object> parameters = mapper.map(tramiteRequest,Map.class);
		parameters.values().removeIf(Objects::isNull);

		if(parameters.containsKey("soloOriginal")){
			parameters.remove("soloOriginal");
		}
		if((Integer) parameters.get("numeroTramite")==0) {
			parameters.remove("numeroTramite");
		}
		if(tramiteRequest.getFechaCreaciontoHasta()!=null){
			Date fechaHasta = tramiteRequest.getFechaCreaciontoHasta();
			String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
			fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
			fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
			parameters.put("fechaCreaciontoHasta",fechaHasta);
		}
		if(!StringUtils.isBlank(tramiteRequest.getMisTramite())){
			parameters.remove("misTramite");
			parameters.put("createdByUser",securityHelper.obtenerUserIdSession());
			//listCriteria.add(Criteria.where("createdByUser").regex(".*"+securityHelper.obtenerUserIdSession()+".*"));
			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				parameters.put("dependenciaUsuarioCreacion",dependenciaIdUserSession);
			}
		}

		List<Tramite> tramiteList = tramiteJPARepository.findByParams(parameters,"numeroTramite",null,0,0);

		return CollectionUtils.isEmpty(tramiteList)?0:tramiteList.size();
	}

	public void actualizarDependenciaUsuarioCreacionTramite(String tramiteId, String dependenciaCreacionTramiteId){
		Tramite tramite = tramiteJPARepository.findById(tramiteId).get();
		Dependencia dependencia = new Dependencia();
		dependencia.setId(dependenciaCreacionTramiteId);
		tramite.setDependenciaUsuarioCreacion(dependencia);
		tramiteJPARepository.save(tramite);
	}

	public Map obtenerIndicadoresDashboardByTokenUsuario(){
		String usuarioId = securityHelper.obtenerUserIdSession();
		String dependenciaId = securityHelper.obtenerDependenciaIdUserSession();
		String cargoId = securityHelper.obtenerCargoIdUserSession();
		return obtenerIndicadoresDashboardByUsuarioIdAndDependencia(usuarioId, dependenciaId, cargoId);
	}

	public Map obtenerIndicadoresDashboardByUsuarioIdAndDependencia(String usuarioId, String dependenciaId, String cargoId) {

		Map mapaRespuesta = new HashMap();
		try{
			//Obtenemos los tramites creados por el usuario
			int cantidadTramitesGeneradosPorUsuarioYDependencia = cantidadTramitesGeneradosByUsuarioAndDependencia(usuarioId,dependenciaId, cargoId);

			//Obtener tramites pendientes por usuario y dependencia
			int cantidadTramitesPendientesPorUsuarioYDependencia = cantidadTramitesPendientesByUsuarioAndDependencia(usuarioId,dependenciaId, cargoId);

			//Obtener tramites atendidos por usuario y dependencia
			int cantidadTramitesAtendidosPorUsuarioYDependencia = cantidadTramitesAtendidosByUsuarioAndDependencia(usuarioId,dependenciaId, cargoId);

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

	public int cantidadTramitesGeneradosByUsuarioAndDependencia(String usuarioId, String dependenciaId, String cargoId) throws InternalErrorException {

		/*
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
		*/
		Map<String, Object> param = new HashMap<>();
		param.put("createdByUser", usuarioId);
		if(!StringUtils.isBlank(dependenciaId)){
			param.put("dependenciaUsuarioCreacion",dependenciaId);
		}
		if(!StringUtils.isBlank(dependenciaId)){
			param.put("cargoUsuarioCreacion",cargoId);
		}
		List<Tramite> tramiteList = tramiteJPARepository.findByParams(param,null,null,0,0);

		return CollectionUtils.isEmpty(tramiteList)?0:tramiteList.size();
	}

	public int cantidadTramitesPendientesByUsuarioAndDependencia(String usuarioId, String dependenciaId, String cargoId) throws Exception {

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaId);
		tramiteDerivacionRequest.setCargoIdUsuarioFin(cargoId);
		tramiteDerivacionRequest.setUsuarioFin(usuarioId);
		tramiteDerivacionRequest.setEstado("P");

		return tramiteDerivacionService.totalRegistros(tramiteDerivacionRequest);
	}

	public int cantidadTramitesAtendidosByUsuarioAndDependencia(String usuarioId, String dependenciaId, String cargoId) throws Exception {

		TramiteDerivacionRequest tramiteDerivacionRequest = new TramiteDerivacionRequest();
		tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaId);
		tramiteDerivacionRequest.setCargoIdUsuarioFin(cargoId);
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

	public void ejecutarActividadesComplementarias(){


		/*
		//Obtenemos los tramites que tiene el campo razonSocial en blanco

		Query andQuery = new Query();
		List<Criteria> listCriteria =  new ArrayList<>();
		//listCriteria.add(Criteria.where("razonSocial").is(null));
		listCriteria.add(Criteria.where("createdByUser").is(null));

		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();



		if(!listCriteria.isEmpty())
			andExpression.add(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));



		//Criteria criteria = Criteria.where("createdByUser").regex(".*63326743b5ebc131b21522e1.*");
		//andQuery.addCriteria(criteria);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));



		Pageable pageable = PageRequest.of(0, 500);
		andQuery.with(pageable);
		andQuery.with(Sort.by(
				Sort.Order.desc("numeroTramite")
		));


		List<Tramite> tramiteList = mongoTemplate.find(andQuery, Tramite.class);
		UsuarioResponse usuarioResponse = null;
		for(Tramite tramite:tramiteList){
			if(tramite.getOrigenDocumento().equals("EXTERNO") && !StringUtils.isBlank(tramite.getCreatedByUser())){
				usuarioResponse = obtenerUsuarioById(tramite.getCreatedByUser());
				tramite.setRazonSocial(usuarioResponse.getPersona().getRazonSocialNombre());
				save(tramite);
				System.out.println("Tramite Externo:"+tramite.getNumeroTramite());
			}
			if(tramite.getOrigenDocumento().equals("INTERNO")
					&& tramite.getEntidadExterna()!=null){
				tramite.setRazonSocial(tramite.getEntidadExterna().getRazonSocial());
				save(tramite);
				System.out.println("Tramite Interno/Externo:"+tramite.getNumeroTramite());
			}
		}

		System.out.println(tramiteList.size());
		*/

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TramiteMigracion registrarTramiteCargBatch(TramiteMigracionBatchBodyRequest tramiteBodyRequest) throws Exception {

		/*
		TramiteMigracion tramite = null;

		try{
			UsuarioResponse usuarioResponse = obtenerUsuarioById(tramiteBodyRequest.getCreatedByUser());
			if(StringUtils.isBlank(usuarioResponse.getId())){
				tramite = new TramiteMigracion();
				tramite.setId("NO_EXISTE_USUARIO");
				return tramite;
			}

		}catch (Exception ex){
			log.error("ERROR",ex);
			tramite = new TramiteMigracion();
			tramite.setId("NO_EXISTE_USUARIO");
			return tramite;
		}

		//Validamos que no haya repetidos
		Map<String, Object> param = new HashMap<>();
		param.put("numeroTramite", tramiteBodyRequest.getNumeroTramite());
		if(!CollectionUtils.isEmpty(buscarHistorialTramite(param))){
			tramite = new TramiteMigracion();
			tramite.setId("YA_EXISTE");
			return tramite;
		}

		Date fechaDocumento = null;
		if(tramiteBodyRequest.getFechaDocumento()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			LocalDate localDate = tramiteBodyRequest.getFechaDocumento();
			tramiteBodyRequest.setFechaDocumento(null);
			fechaDocumento =Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
		}

		tramite = mapper.map(tramiteBodyRequest,TramiteMigracion.class);

		if(fechaDocumento!=null)
			tramite.setFechaDocumento(fechaDocumento);

		tramite.setTramiteRelacionado(null);

		//tramite.setEstado("A");
		tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			tramite.setCargoUsuarioCreacion(null);
			//Se setea la forma de recepcion siempre como digital
			//tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));
		}else{
			if(tramiteBodyRequest.getOrigen().equals("INTERNO")){
				tramite.setEntidadExterna(null);
				tramite.setFormaRecepcion(null);
			}else{
				tramite.setEntidadInterna(null);
			}
			tramite.setDependenciaDestino(null);

		}

		tramiteMigracionJPARepository.save(tramite);

		if(tramiteBodyRequest.getOrigenDocumento().equals("EXTERNO")){
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

			tramiteDerivacionBodyRequest.setUsuarioFin(((LinkedHashMap)((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("usuario")).get("id").toString());

			CargoDTOResponse cargoResponse = mapper.map(((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("cargo"),CargoDTOResponse.class);

			tramiteDerivacionBodyRequest.setDependenciaIdUsuarioFin(cargoResponse.getDependencia().getId());
			tramiteDerivacionBodyRequest.setCargoIdUsuarioFin(cargoResponse.getId());
			tramiteDerivacionBodyRequest.setEstadoInicio("REGISTRADO");
			tramiteDerivacionBodyRequest.setFechaInicio(tramite.getCreatedDate());
			tramiteDerivacionBodyRequest.setTramiteId(tramite.getId());
			tramiteDerivacionBodyRequest.setComentarioInicio("Se inicia registro del Tramite");
			tramiteDerivacionBodyRequest.setForma("ORIGINAL");

			tramiteDerivacionBodyRequest.setFechaFin(tramite.getCreatedDate());
			tramiteDerivacionBodyRequest.setEstadoFin("DERIVADO");

			Tramite tramiteRegistrado = tramiteJPARepository.findById(tramiteDerivacionBodyRequest.getTramiteId()).get();

			tramiteDerivacionService.registrarTramiteDerivacionBatch(tramiteDerivacionBodyRequest, tramiteRegistrado);

		}

		return tramite;
		*/
		return null;

	}

	/*
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void registrarDerivacionTramiteBatch(TramiteMigracion tramite) throws Exception {
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


		tramiteDerivacionBodyRequest.setUsuarioFin(((LinkedHashMap)((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("usuario")).get("id").toString());

		CargoDTOResponse cargoResponse = mapper.map(((LinkedHashMap)((List)response.getBody().getData()).get(0)).get("cargo"),CargoDTOResponse.class);

		tramiteDerivacionBodyRequest.setDependenciaIdUsuarioFin(cargoResponse.getDependencia().getId());
		tramiteDerivacionBodyRequest.setCargoIdUsuarioFin(cargoResponse.getId());
		tramiteDerivacionBodyRequest.setEstadoInicio("REGISTRADO");
		tramiteDerivacionBodyRequest.setFechaInicio(tramite.getCreatedDate());
		tramiteDerivacionBodyRequest.setTramiteId(tramite.getId());
		tramiteDerivacionBodyRequest.setComentarioInicio("Se inicia registro del Tramite");
		tramiteDerivacionBodyRequest.setForma("ORIGINAL");
		TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyRequest);

		TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyRequest);

		//Se recepciona el tramite para que mesa de partes lo deje pendiente de atencion

		tramiteDerivacion = tramiteDerivacionService.registrarRecepcionTramiteDerivacion(tramiteDerivacion.getId());


	}
	*/

	public Map ejecutarActividadesComplementariasMigracion(TareasComplementariasMigracionRequest tareasComplementariasMigracionRequest) throws Exception {

		/*
		//Obtener tramite
		Tramite tramite = tramiteJPARepository.findById(tareasComplementariasMigracionRequest.getIdTramite()).get();

		//Aumentamos 5 horas, para nivelas la horas al tiempo correcto
		tramite.setCreatedDate(sumarCincoHoras(tramite.getCreatedDate()));
		tramite.setLastModifiedDate(sumarCincoHoras(tramite.getLastModifiedDate()));

		save(tramite);

		Map<String, Object> param = new HashMap<>();
		param.put("tramiteActualizado", true);
		int cantidadDerivacionesActualizadas = 0;
		try{
			Query query = new Query();
			Criteria criteria = Criteria.where("tramite.id").is(tareasComplementariasMigracionRequest.getIdTramite());
			query.addCriteria(criteria);
			List<TramiteDerivacion> tramiteDerivacionList = mongoTemplate.find(query, TramiteDerivacion.class);

			for(TramiteDerivacion tramiteDerivacion : tramiteDerivacionList){

				actualizarFechaHoraDerivacion(tramiteDerivacion);

				if((tramiteDerivacion.getSecuencia()==1 && tramite.getOrigenDocumento().equals("EXTERNO"))
						|| tareasComplementariasMigracionRequest.isIncluirDerivaciones()){
					tramiteDerivacionService.save(tramiteDerivacion);
					cantidadDerivacionesActualizadas++;
				}

			}
			param.put("actualizacionDerivacionesConExito", true);
			param.put("cantidadDerivacionesActualizadas", cantidadDerivacionesActualizadas);

		}catch (Exception ex){
			log.error("ERROR",ex);
			param.put("actualizacionDerivacionesConExito", false);
			param.put("cantidadDerivacionesActualizadas", cantidadDerivacionesActualizadas);
		}

		return param;
		*/

		return null;

	}

	private void actualizarFechaHoraDerivacion(TramiteDerivacion tramiteDerivacion){
		tramiteDerivacion.setFechaInicio(sumarCincoHoras(tramiteDerivacion.getFechaInicio()));
		tramiteDerivacion.setFechaFin(sumarCincoHoras(tramiteDerivacion.getFechaFin()));
	}

	private Date sumarCincoHoras(Date fechaHoraAnterior){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaHoraAnterior); // Configuramos la fecha que se recibe
		calendar.add(Calendar.HOUR, 5);
		return calendar.getTime();
	}

	//@Transactional
	public void registrarTramiteEntidadExterna(Tramite tramite){

		//eliminamos la relacion de entidad externa
		tramiteEntidadExternaJPARepository.eliminarEntidadExternaByTramiteId(tramite.getId());

		//registramos entidad externa del tramite
		EntidadExterna entidadExterna = tramite.getEntidadExterna();
		entidadExterna.setTramiteId(tramite.getId());
		tramiteEntidadExternaJPARepository.save(entidadExterna);

	}

	private void registrarTramiteEntidadInterna(Tramite tramite){

		//eliminamos la relacion de entidad externa
		tramiteEntidadInternaJPARepository.eliminarEntidadInternaByTramiteId(tramite.getId());

		//registramos entidad externa del tramite
		EntidadInterna entidadInterna = tramite.getEntidadInterna();
		entidadInterna.setTramiteId(tramite.getId());
		tramiteEntidadInternaJPARepository.save(entidadInterna);

	}

	public EntidadExterna obtenerTramiteEntidadExternaByTramiteId(String tramiteId){

		return tramiteEntidadExternaJPARepository.findByTramiteId(tramiteId).orElse(null);

	}

	private EntidadInterna obtenerTramiteEntidadInternaByTramiteId(String tramiteId){

		return tramiteEntidadInternaJPARepository.findByTramiteId(tramiteId).orElse(null);

	}

	public Map generarReporteAcuseTramiteInteroperabilidad(Tramite tramite, String dependenciaInternaDestino) throws Exception {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream url = classloader.getResourceAsStream("acuseTramiteExternoInteroperabilidad.jrxml");

		JasperReport jasperReport = JasperCompileManager.compileReport(url);

		DateFormat Formato = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String fechaGeneracion = Formato.format(determinarFechaGeneracion(tramite.getCreatedDate()));

		//Obtener Tipo Documento de Tramite
		TipoDocumentoTramite tipoDocumento = tipoDocumentoJPARepository.findById(tramite.getTipoDocumento().getId()).get();

		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("numeroTramite", tramite.getNumeroTramite());
		parameters.put("tipoDocumento", tipoDocumento.getTipoDocumento().toUpperCase());
		parameters.put("fechaGeneracion", fechaGeneracion);
		parameters.put("fechaHoraIngreso", fechaGeneracion);
		parameters.put("estado", "RECEPCIONADO");
		parameters.put("emisorRazonSocial", tramite.getEntidadExterna().getRazonSocial());// user.getPersona().getRazonSocialNombre().toUpperCase());
		parameters.put("emisorRuc", tramite.getEntidadExterna().getRucEntidadRemitente()); // user.getPersona().getNumeroDocumento());
		parameters.put("asunto", tramite.getAsunto());
		parameters.put("destino", dependenciaInternaDestino);

		List<String> lista = null;
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(lista);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters,source);

		//Directorio donde se guardará una copia fisica
		String nombreArchivoAcuse = "acuseRecibo-" + new SimpleDateFormat("ddMMyyyyHHmmssSSS").format(new Date()) + ".pdf";

		String rutaAcuse = env.getProperty("file.base-upload-dir") + File.separator + "acuse";
		fileStorageService.createDirectory(rutaAcuse);

		final String reportPdf = rutaAcuse + File.separator + nombreArchivoAcuse;

		//Guardamos en el directorio
		JasperExportManager.exportReportToPdfFile(print, reportPdf);

		Map<String, Object> param = new HashMap<>();
		param.put("ruta",reportPdf);
		param.put("numeroTramite",tramite.getNumeroTramite());
		//param.put("correo",user.getEmail());
		param.put("tramiteId",tramite.getId());
		param.put("nombreArchivo",nombreArchivoAcuse);

		return param;
	}

	//@Transactional//(readOnly = false, propagation = Propagation.REQUIRED)
	public Map registrarTramitePideHandler(TramitePideBodyRequest tramitePideBodyRequest, MultipartFile filePrincipal, List<MultipartFile> fileAnexos, DatosFirmaDocumentoRequest datosFirmaDocumentoRequest) throws Exception {

		Tramite tramitePide =  registrarTramitePide(tramitePideBodyRequest, filePrincipal, fileAnexos, datosFirmaDocumentoRequest);

		if(datosFirmaDocumentoRequest!=null){
			enviarDocumentoParaFirma(datosFirmaDocumentoRequest, filePrincipal, tramitePide);
			actualizarDatosDocumentoFirmadoDigitalmente(tramitePide.getId());
		}

		String estadoTramite = tramitePide.getEstado();

		Map resultadEnvio = new HashMap();

		//try{
		//resultadEnvio = enviarTramiteAPide(tramitePide.getId());
		resultadEnvio = enviarTramiteAPide(tramitePide, tramitePideBodyRequest, filePrincipal, fileAnexos);
		//Vemos el indicados de resultado, si es ok entonces solocamos el estado enviado pide, sino con error pide.
		estadoTramite = EstadoTramiteConstant.ENVIADO_PIDE;
		if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR))
			estadoTramite = EstadoTramiteConstant.CON_ERROR_PIDE;
		if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR_SERVICIO)){
			estadoTramite = EstadoTramiteConstant.POR_ENVIAR_PIDE;
			resultadEnvio.put("mensaje", Map.of("vcodres", "001", "vdesres", "Hubo un error en la transmisión, se volverá a intentar de forma automática en unos momentos y se informará por correo de los resultados"));
		}

		/*
		}catch (Exception ex){
			estadoTramite = EstadoTramiteConstant.POR_ENVIAR_PIDE;
			resultadEnvio.put("mensaje", Map.of("vcodres", "001", "vdesres", "Hubo un error en la transmisión, se volverá a intentar de forma automática en unos momentos y se informará por correo de los resultados"));
			log.error("ERROR", ex);
		}
		*/

		Tramite tramite = findById(tramitePide.getId());
		tramite.setEstado(estadoTramite);
		tramite.setIntentosEnvio(tramite.getIntentosEnvio()==null?1:tramite.getIntentosEnvio()+1);
		if(resultadEnvio.containsKey("mensaje")){
			tramite.setResultadoTransmision(((Map)resultadEnvio.get("mensaje")).get("vdesres").toString());
		}
		save(tramite);

		resultadEnvio.put("tramiteId",tramite.getId());
		resultadEnvio.put("resultado",estadoTramite);

		return resultadEnvio;

	}

	public Map enviarTramiteAPide(Tramite tramite, TramitePideBodyRequest tramitePideBodyRequest, MultipartFile filePrincipal, List<MultipartFile> fileAnexos) throws Exception {

		Map resultadoEnvio = new HashMap();
		resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.OK);

		String cuo = tramite.getCuo();
		if(StringUtils.isBlank(cuo)){
			cuo = obtenerCuo();
			tramite.setCuo(cuo);
		}

		String tipoDocumentoPide = tipoDocumentoPideJPARepository.findByTipoDocumentoPide(tipoDocumentoJPARepository.findById(tramitePideBodyRequest.getTipoDocumentoPideId()).get().getTipoDocumento()).get(0).getId();

		//Obtenemos el archivo de la tabla adjunto porque puede ser que haya sido firmada
		List<DocumentoAdjunto> documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(DocumentoAdjuntoRequest.builder().tramiteId(tramite.getId()).build());

		DocumentoAdjunto documentoAdjuntoPrincipal = documentoAdjuntoList.stream().filter(x -> x.getSeccionAdjunto().equals(SeccionAdjuntoConstant.PRINCIPAL) && x.getTipoAdjunto().equals(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)).findFirst().get();
		documentoAdjuntoPrincipal.setTramite(tramite);
		Resource documentopPrincipalResource = documentoAdjuntoService.obtenerArchivo(documentoAdjuntoPrincipal);//documentoAdjuntoService.obtenerDocumentoAdjunto(DocumentoAdjuntoRequest.builder().id(documentoAdjuntoPrincipal.getId()).build());


		//Creamos el body para envio
		RecepcionTramite recepcionTramite = new RecepcionTramite();
		recepcionTramite.setVrucentrem (env.getProperty("app.micelaneos.ruc-amsac"));//("20103030791");
		recepcionTramite.setVrucentrec(tramitePideBodyRequest.getRucEntidadDestino());
		recepcionTramite.setVnomentemi( env.getProperty("app.micelaneos.nombre-amsac"));
		recepcionTramite.setVuniorgrem(tramitePideBodyRequest.getDependenciaRemitenteNombre());
		recepcionTramite.setVcuo(cuo);
		//recepcionTramite.setVcuoref("0");
		recepcionTramite.setCcodtipdoc(tipoDocumentoPide);//tramitePideBodyRequest.getTipoDocumentoPideId());
		recepcionTramite.setVnumdoc(tramitePideBodyRequest.getNumeroDocumento());
		//recepcionTramite.setDfecdoc(DatatypeFactory.newInstance().newXMLGregorianCalendar(Date.from(tramitePideBodyRequest.getFechaDocumento().atStartOfDay(ZoneId.systemDefault()).toInstant()).toString()));
		recepcionTramite.setDfecdoc(DatatypeFactory.newInstance().newXMLGregorianCalendar(tramitePideBodyRequest.getFechaDocumento().toString()+"T00:00:00"));
		recepcionTramite.setVuniorgdst(tramitePideBodyRequest.getUnidadOrganicaDestino());
		recepcionTramite.setVnomdst(tramitePideBodyRequest.getNombreDestinatario());
		recepcionTramite.setVnomcardst(tramitePideBodyRequest.getCargoDestinatario());
		recepcionTramite.setVasu(tramitePideBodyRequest.getAsunto());
		recepcionTramite.setSnumanx(CollectionUtils.isEmpty(fileAnexos) ? 0 : fileAnexos.size());
		recepcionTramite.setSnumfol(Integer.valueOf(tramitePideBodyRequest.getNumeroFolios()));
		//recepcionTramite.setBpdfdoc(filePrincipal.getBytes());
		//recepcionTramite.setBpdfdoc(Base64.getEncoder().encode(filePrincipal.getBytes()));
		recepcionTramite.setBpdfdoc(Base64.getEncoder().encode(documentopPrincipalResource.getInputStream().readAllBytes()));
		recepcionTramite.setVnomdoc(getFileName(filePrincipal.getOriginalFilename()));
		recepcionTramite.setVurldocanx(env.getProperty("app.url.descargaAnexoPideAmsac"));

		fileAnexos.stream().forEach(x -> {
			DocumentoAnexo documentoAnexo = new DocumentoAnexo();
			documentoAnexo.setVnomdoc(getFileName(x.getOriginalFilename()));
			recepcionTramite.getLstanexos().add(documentoAnexo);
		});

		UsuarioResponse usuario = obtenerUsuarioById(tramitePideBodyRequest.getUsuarioRemitenteId());

		recepcionTramite.setCtipdociderem("1"); //DNI
		recepcionTramite.setVnumdociderem(usuario.getPersona().getNumeroDocumento());

		//llenamos datos
		RecepcionarTramiteResponse recepcionarTramiteResponse = new RecepcionarTramiteResponse();
		recepcionarTramiteResponse.setRequest(recepcionTramite);

		//Realizamos el envio del tramite
		String respuestaPide = null;
		String estadoSeguimientoEnvio = EstadoTramiteConstant.ENVIADO_PIDE;
		pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory objectFactory = new pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory();
		JAXBElement jaxbTramiteResponse = objectFactory.createRecepcionarTramiteResponse(recepcionarTramiteResponse);
		Date fechaEnvio = new Date();
		try{
			log.info("Enviado Tramite: "+ tramite.getId() +", CUO:"+tramite.getCuo()+", a la siguiente ruta: "+env.getProperty("app.url.pideServer"));
			JAXBElement jaxbElementResponse = (JAXBElement) soapConnector.callWebService(env.getProperty("app.url.pideServer"), jaxbTramiteResponse);
			//RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = soapConnector.callWebService(recepcionarTramiteResponse);

			RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = (RecepcionarTramiteResponseResponse) jaxbElementResponse.getValue();
			log.info("Respuesta Envio: " + recepcionarTramiteResponseResponse.getReturn().getVcodres());
			resultadoEnvio.put("mensaje", new ObjectMapper()
					.convertValue(recepcionarTramiteResponseResponse.getReturn(), new TypeReference<Map<String, Object>>() {})); //recepcionarTramiteResponseResponse.getReturn());
			if(!recepcionarTramiteResponseResponse.getReturn().getVcodres().equals("0000")){
				if(recepcionarTramiteResponseResponse.getReturn().getVcodres().equals("-1")){
					throw new Exception(new ObjectMapper().writeValueAsString(recepcionarTramiteResponseResponse.getReturn()));
				}
				resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.ERROR);
				estadoSeguimientoEnvio = EstadoTramiteConstant.CON_ERROR_PIDE;
			}else{
				tramite.setFechaEnvio(fechaEnvio);
			}
			//TODO Falta considerar el estado -1, con estado -1 debe entrar a la politica de reintentos.
			respuestaPide = new ObjectMapper().writeValueAsString(recepcionarTramiteResponseResponse.getReturn());

		}catch(Exception ex){
			//Map<String, Object> mapaMensaje = new HashMap<>();
			//mapaMensaje.put("vcodres", "Servicio de PIDE no disponible, se intentará en breve.");
			//resultadoEnvio.put("mensaje",mapaMensaje);
			resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.ERROR_SERVICIO);
			estadoSeguimientoEnvio = EstadoTramiteConstant.POR_ENVIAR_PIDE;
			respuestaPide = ex.getMessage();
			log.error("ERROR",ex);
		}
		Date fechaRespuesta = new Date();

		//Registrammos en una tabla, el envio y la respuesta obtenida del envio, asi como la fecha y hora.
		TramiteEnvioPide tramiteEnvioPide = new TramiteEnvioPide();
		tramiteEnvioPide.setRequest(new ObjectMapper().writeValueAsString(recepcionTramite));
		tramiteEnvioPide.setResponse(respuestaPide);
		tramiteEnvioPide.setTramite(tramite);
		tramiteEnvioPide.setEstado(estadoSeguimientoEnvio);
		tramiteEnvioPide.setSecuencia(tramiteEnvioPideJPARepository.obtenerSecuencia(tramite.getId()).intValue());
		tramiteEnvioPide.setFechaEnvio(fechaEnvio);
		tramiteEnvioPide.setFechaRespuesta(fechaRespuesta);
		tramiteEnvioPide.setCreatedByUser(securityHelper.obtenerUserIdSession());
		tramiteEnvioPideJPARepository.save(tramiteEnvioPide);

		//Se actualizan datos de la transmision
		//tramite.setCuo(cuo);
		save(tramite);
		//}catch (Exception ex){
			//Si hay error en la ejecución, se queda con estado POR_ENVIAR para que se vuelva a intentar, hasta 3 veces

		//}
		return resultadoEnvio;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Tramite registrarTramitePide(TramitePideBodyRequest tramitePideBodyRequest, MultipartFile filePrincipal, List<MultipartFile> fileAnexos, DatosFirmaDocumentoRequest datosFirmaDocumentoRequest) throws Exception {
		Date fechaDocumento = null;
		LocalDate localDateFechaDocumento = null;
		if(tramitePideBodyRequest.getFechaDocumento()!=null){
			ZoneId defaultZoneId = ZoneId.systemDefault();
			localDateFechaDocumento = tramitePideBodyRequest.getFechaDocumento();
			tramitePideBodyRequest.setFechaDocumento(null);
			fechaDocumento =Date.from(localDateFechaDocumento.atStartOfDay(defaultZoneId).toInstant());
		}

		//Mapeamos hacia el objeto Tramite
		Tramite tramite = mapper.map(tramitePideBodyRequest,Tramite.class);

		//Completamos datos
		if(fechaDocumento!=null)
			tramite.setFechaDocumento(fechaDocumento);

		tramite.setTramiteRelacionado(null);
		if(StringUtils.isBlank(tramite.getId())){
			tramite.setNumeroTramite(obtenerNumeroTramite());
			tramite.setNumeroTramiteDependencia(obtenerNumeroTramiteDependencia(tramitePideBodyRequest.getDependenciaRemitenteId()));
			tramite.setAnioTramiteDependencia(Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())));
			tramite.setTramiteDependencia(StringUtils.leftPad(tramite.getNumeroTramiteDependencia().toString(),6,"0") + "-" + tramite.getAnioTramiteDependencia() + "-" + tramitePideBodyRequest.getDependenciaRemitenteSigla());
		}

		tramite.setEntidadInterna(null);
		tramite.setDependenciaDestino(null);
		tramite.setRazonSocial(tramitePideBodyRequest.getNombreEntidadDestino());
		tramite.setEstado(EstadoTramiteConstant.PENDIENTE);
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

		//Si es modificacion
		if(!StringUtils.isBlank(tramite.getId())){
			Tramite tramiteTemporal = tramiteJPARepository.findById(tramite.getId()).get();
			tramite.setCreatedByUser(tramiteTemporal.getCreatedByUser());
			tramite.setCreatedDate(tramiteTemporal.getCreatedDate());
			tramite.setNumeroTramite(tramiteTemporal.getNumeroTramite());
		}
		tramite.setCreatedDate(new Date());
		TramiteBodyRequest tramiteBodyRequest = new TramiteBodyRequest();
		tramiteBodyRequest.setOrigenDocumento(tramitePideBodyRequest.getOrigenDocumento());
		tramiteBodyRequest.setOrigen(tramitePideBodyRequest.getOrigen());
		tramite.setTipoTramite(generarTipoTramite(tramiteBodyRequest));
		tramite.setTramitePrioridad(tramitePrioridadService.findByAllTramitePrioridad().stream().filter(x -> x.getPrioridad().equals("2")).findFirst().get()); //Seteamos la prioridad Normal = 2

		tramiteJPARepository.save(tramite);
		tramitePideBodyRequest.setFechaDocumento(localDateFechaDocumento);

		//Guardamos datos de la entidad interna
		if(tramite.getEntidadExterna()!=null){
			registrarTramiteEntidadExterna(tramite);
		}

		if(datosFirmaDocumentoRequest==null){
			//Registramos el documento principal
			MultipartFile filePrincipalMarcaAgua = filePrincipal;
			if(env.getProperty("app.micelaneos.mostrar-marca-agua").toString().equals("S"))
				filePrincipalMarcaAgua = agregarDependencia(filePrincipal,tramite.getTramiteDependencia().toString());
			DocumentoAdjuntoBodyRequest documentoAdjuntoRequest = new DocumentoAdjuntoBodyRequest();
			documentoAdjuntoRequest.setTramiteId(tramite.getId());
			documentoAdjuntoRequest.setDescripcion("DOCUMENTO PRINCIPAL");
			documentoAdjuntoRequest.setFile(filePrincipalMarcaAgua);
			documentoAdjuntoRequest.setSeccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL);
			documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE);
			documentoAdjuntoRequest.setOmitirValidacionAdjunto(true);
			documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoRequest);
		}

		/*
		if(datosFirmaDocumentoRequest!=null){
			FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest = new FirmaDocumentoTramiteHibridoBodyRequest();
			firmaDocumentoTramiteHibridoBodyRequest.setTextoFirma(datosFirmaDocumentoRequest.getTextoFirma());
			firmaDocumentoTramiteHibridoBodyRequest.setPosition(datosFirmaDocumentoRequest.getPosition());
			firmaDocumentoTramiteHibridoBodyRequest.setOrientacion(datosFirmaDocumentoRequest.getOrientacion());
			firmaDocumentoTramiteHibridoBodyRequest.setPositionCustom(datosFirmaDocumentoRequest.getPositionCustom());
			firmaDocumentoTramiteHibridoBodyRequest.setPin(datosFirmaDocumentoRequest.getPin());
			firmaDocumentoTramiteHibridoBodyRequest.setUsuarioFirmaLogoId(datosFirmaDocumentoRequest.getUsuarioFirmaLogoId());
			firmaDocumentoTramiteHibridoBodyRequest.setFile(filePrincipal);
			firmaDocumentoTramiteHibridoBodyRequest.setTramiteId(tramite.getId());
			firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);

			//Al ser asincrono, se espera un mmomento a que el documento ya se encuentre firmado
			DocumentoAdjuntoRequest documentoAdjuntoRequest = DocumentoAdjuntoRequest.builder().descripcion(DescripcionDocumentoAdjuntoConstant.DOCUMENTO_FIRMADO_DIGITALMENTE).build();
			List<DocumentoAdjunto> documentoAdjuntoList = null;
			boolean seObtuvoDocumentoFirmado = false;
			int cantidadIntentos = 0;
			int cantidadIntentosMaximo = 5;
			while(!seObtuvoDocumentoFirmado){
				if(cantidadIntentos == cantidadIntentosMaximo)
					throw new ServiceException("No se pudo firmar documento, volver a intentarlo en breve");

				documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
				if(!CollectionUtils.isEmpty(documentoAdjuntoList)){
					DocumentoAdjunto documentoAdjunto = documentoAdjuntoList.get(0);
					documentoAdjunto.setSeccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL);
					documentoAdjunto.setTipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE);
					documentoAdjuntoService.guardarAdjunto(documentoAdjunto);
					seObtuvoDocumentoFirmado = true;
				}else{
					Thread.sleep(1000);
					++cantidadIntentos;
				}
			}
		}else{
			//Registramos el documento principal
			DocumentoAdjuntoBodyRequest documentoAdjuntoRequest = new DocumentoAdjuntoBodyRequest();
			documentoAdjuntoRequest.setTramiteId(tramite.getId());
			documentoAdjuntoRequest.setDescripcion("DOCUMENTO PRINCIPAL");
			documentoAdjuntoRequest.setFile(filePrincipal);
			documentoAdjuntoRequest.setSeccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL);
			documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE);
			documentoAdjuntoRequest.setOmitirValidacionAdjunto(true);
			documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoRequest);
		}
		*/

		//Registramos los adjuntos
		fileAnexos.stream().forEach(x -> {
			DocumentoAdjuntoBodyRequest documentoAdjuntoSecundarioRequest = new DocumentoAdjuntoBodyRequest();
			documentoAdjuntoSecundarioRequest.setTramiteId(tramite.getId());
			documentoAdjuntoSecundarioRequest.setDescripcion("DOCUMENTO SECUNDARIO");
			documentoAdjuntoSecundarioRequest.setFile(x);
			documentoAdjuntoSecundarioRequest.setSeccionAdjunto(SeccionAdjuntoConstant.SECUNDARIO);
			documentoAdjuntoSecundarioRequest.setTipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE);
			documentoAdjuntoSecundarioRequest.setOmitirValidacionAdjunto(true);
			try {
				documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoSecundarioRequest);
			} catch (Exception e) {
				log.error("ERROR", e);
			}
		});

		return tramite;
	}

	private Map enviarTramiteAPide(String tramiteId) throws Exception {

		Map resultadoEnvio = new HashMap();
		resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.OK);

		//Obtenemos el documento principal en byte[]
		/*
		List<DocumentoAdjuntoResponse> documentoAdjuntoResponseList = documentoAdjuntoService.obtenerDocumentoAdjuntoList(DocumentoAdjuntoRequest.builder().tramiteId(tramiteId).build());

		DocumentoAdjuntoResponse documentoAdjuntoPrincipalResponse = documentoAdjuntoResponseList.stream().filter(x -> x.getSeccionAdjunto().equals(SeccionAdjuntoConstant.PRINCIPAL) && x.getTipoAdjunto().equals(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)).findFirst().get();

		Resource documentopPrincipalResource = documentoAdjuntoService.obtenerDocumentoAdjunto(DocumentoAdjuntoRequest.builder().id(documentoAdjuntoPrincipalResponse.getId()).build());

		List<DocumentoAdjuntoResponse> documentoAdjuntoAnexosResponse = documentoAdjuntoResponseList.stream().filter(x -> x.getSeccionAdjunto().equals(SeccionAdjuntoConstant.SECUNDARIO) && x.getTipoAdjunto().equals(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)).collect(Collectors.toList());
		*/

		List<DocumentoAdjunto> documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(DocumentoAdjuntoRequest.builder().tramiteId(tramiteId).build());

		DocumentoAdjunto documentoAdjuntoPrincipal = documentoAdjuntoList.stream().filter(x -> x.getSeccionAdjunto().equals(SeccionAdjuntoConstant.PRINCIPAL) && x.getTipoAdjunto().equals(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)).findFirst().get();

		Resource documentopPrincipalResource = documentoAdjuntoService.obtenerDocumentoAdjunto(DocumentoAdjuntoRequest.builder().id(documentoAdjuntoPrincipal.getId()).build());

		List<DocumentoAdjunto> documentoAdjuntoAnexos = documentoAdjuntoList.stream().filter(x -> x.getSeccionAdjunto().equals(SeccionAdjuntoConstant.SECUNDARIO) && x.getTipoAdjunto().equals(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)).collect(Collectors.toList());

		TramiteRequest tramiteRequest = new TramiteRequest();
		tramiteRequest.setId(tramiteId);
		Tramite tramite = buscarTramiteParams(tramiteRequest).get(0);
		String cuo = tramite.getCuo();

		//Obtenemos el tipo de documento para pide a partir del documento
		String tipoDocumentoPide = tipoDocumentoPideJPARepository.findByTipoDocumentoPide(tramite.getTipoDocumento().getTipoDocumento()).get(0).getId();

		if(StringUtils.isBlank(tramite.getCuo()))
			//generar cuo
			cuo = obtenerCuo();//"0000000060";

		//Creamos el body para envio
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		RecepcionTramite recepcionTramite = new RecepcionTramite();
		recepcionTramite.setVrucentrem(env.getProperty("app.micelaneos.ruc-amsac"));
		recepcionTramite.setVrucentrec(tramite.getEntidadExterna().getRucEntidadDestino());
		recepcionTramite.setVnomentemi(env.getProperty("app.micelaneos.nombre-amsac"));
		recepcionTramite.setVuniorgrem(tramite.getDependenciaRemitente().getNombre());
		recepcionTramite.setVcuo(cuo);
		recepcionTramite.setVcuoref("");
		recepcionTramite.setCcodtipdoc(tipoDocumentoPide);//tramite.getTipoDocumento().getTipoDocumento());
		recepcionTramite.setVnumdoc(tramite.getNumeroDocumento());// tramitePideBodyRequest.getNumeroDocumento());
		recepcionTramite.setDfecdoc(DatatypeFactory.newInstance().newXMLGregorianCalendar(sdf.format(tramite.getFechaDocumento())));
		recepcionTramite.setVuniorgdst(tramite.getEntidadExterna().getUnidadOrganicaDestino());// tramitePideBodyRequest.getUnidadOrganicaDestino());
		recepcionTramite.setVnomdst(tramite.getEntidadExterna().getNombre());//  tramitePideBodyRequest.getNombreDestinatario());
		recepcionTramite.setVnomcardst(tramite.getEntidadExterna().getCargo());// tramitePideBodyRequest.getCargoDestinatario());
		recepcionTramite.setVasu(tramite.getAsunto());// tramitePideBodyRequest.getAsunto());
		recepcionTramite.setSnumanx(CollectionUtils.isEmpty(documentoAdjuntoAnexos) ? 0 : documentoAdjuntoAnexos.size());
		recepcionTramite.setSnumfol(Integer.valueOf(tramite.getFolio()));
		//recepcionTramite.setBpdfdoc(documentopPrincipalResource.getInputStream().readAllBytes());
		recepcionTramite.setBpdfdoc(Base64.getEncoder().encode(documentopPrincipalResource.getInputStream().readAllBytes()));
		recepcionTramite.setVnomdoc(getFileName(documentopPrincipalResource.getFilename()));//.getOriginalFilename()));
		recepcionTramite.setVurldocanx(env.getProperty("app.micelaneos.ruta-anexos"));

		documentoAdjuntoAnexos.stream().forEach(x -> {
			DocumentoAnexo documentoAnexo = new DocumentoAnexo();
			try {
				documentoAnexo.setVnomdoc(documentoAdjuntoService.obtenerDocumentoAdjunto(DocumentoAdjuntoRequest.builder().id(x.getId()).build()).getFilename());
				recepcionTramite.getLstanexos().add(documentoAnexo);
			} catch (Exception e) {
				log.error("ERROR", e);
			}
		});

		UsuarioResponse usuario = obtenerUsuarioById(tramite.getUsuarioRemitente().getId());// tramitePideBodyRequest.getUsuarioRemitenteId());

		recepcionTramite.setCtipdociderem("1"); //DNI
		recepcionTramite.setVnumdociderem(usuario.getPersona().getNumeroDocumento());

		//llenamos datos
		RecepcionarTramiteResponse recepcionarTramiteResponse = new RecepcionarTramiteResponse();
		recepcionarTramiteResponse.setRequest(recepcionTramite);

		//Realizamos el envio del tramite
		/*
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement jaxbTramiteResponse = objectFactory.createRecepcionarTramiteResponse(recepcionarTramiteResponse);
		Date fechaEnvio = new Date();
		JAXBElement jaxbElementResponse = (JAXBElement) soapConnector.callWebService(env.getProperty("app.url.pideServer"), jaxbTramiteResponse);
		RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = (RecepcionarTramiteResponseResponse) jaxbElementResponse.getValue();
		Date fechaRespuesta = new Date();
		log.info("Respuesta Envio: " + recepcionarTramiteResponseResponse.getReturn().getVcodres());

		String estadoSeguimientoEnvio = EstadoTramiteConstant.ENVIADO_PIDE;
		resultadoEnvio.put("mensaje", new ObjectMapper()
				.convertValue(recepcionarTramiteResponseResponse.getReturn(), new TypeReference<Map<String, Object>>() {})); //recepcionarTramiteResponseResponse.getReturn());
		if(!recepcionarTramiteResponseResponse.getReturn().getVcodres().equals("0000")){
			resultadoEnvio.put("resultado","ERROR");
			estadoSeguimientoEnvio = EstadoTramiteConstant.CON_ERROR_PIDE;
		}
		*/

		//Se adecua esta parte
		String respuestaPide = null;
		String estadoSeguimientoEnvio = EstadoTramiteConstant.ENVIADO_PIDE;
		pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory objectFactory = new pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory();
		JAXBElement jaxbTramiteResponse = objectFactory.createRecepcionarTramiteResponse(recepcionarTramiteResponse);
		Date fechaEnvio = new Date();
		try{
			JAXBElement jaxbElementResponse = (JAXBElement) soapConnector.callWebService(env.getProperty("app.url.pideServer"), jaxbTramiteResponse);
			//RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = soapConnector.callWebService(recepcionarTramiteResponse);

			RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = (RecepcionarTramiteResponseResponse) jaxbElementResponse.getValue();
			log.info("Respuesta Envio: " + recepcionarTramiteResponseResponse.getReturn().getVcodres());
			resultadoEnvio.put("mensaje", new ObjectMapper()
					.convertValue(recepcionarTramiteResponseResponse.getReturn(), new TypeReference<Map<String, Object>>() {})); //recepcionarTramiteResponseResponse.getReturn());
			if(!recepcionarTramiteResponseResponse.getReturn().getVcodres().equals("0000")){
				if(recepcionarTramiteResponseResponse.getReturn().getVcodres().equals("-1")){
					throw new Exception(new ObjectMapper().writeValueAsString(recepcionarTramiteResponseResponse.getReturn()));
				}
				resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.ERROR);
				estadoSeguimientoEnvio = EstadoTramiteConstant.CON_ERROR_PIDE;
			}
			respuestaPide = new ObjectMapper().writeValueAsString(recepcionarTramiteResponseResponse.getReturn());

		}catch(Exception ex){
			resultadoEnvio.put("resultado",EstadoResultadoEnvioPideConstant.ERROR_SERVICIO);
			estadoSeguimientoEnvio = EstadoTramiteConstant.POR_ENVIAR_PIDE;
			respuestaPide = ex.getMessage();
			log.error("ERROR",ex);
		}
		Date fechaRespuesta = new Date();


		//Registrammos en una tabla, el envio y la respuesta obtenida del envio, asi como la fecha y hora.
		TramiteEnvioPide tramiteEnvioPide = new TramiteEnvioPide();
		tramiteEnvioPide.setRequest(new ObjectMapper().writeValueAsString(recepcionTramite));
		tramiteEnvioPide.setResponse(respuestaPide);
		tramiteEnvioPide.setTramite(tramite);
		tramiteEnvioPide.setEstado(estadoSeguimientoEnvio);
		tramiteEnvioPide.setSecuencia(tramiteEnvioPideJPARepository.obtenerSecuencia(tramite.getId()).intValue());
		tramiteEnvioPide.setFechaEnvio(fechaEnvio);
		tramiteEnvioPide.setFechaRespuesta(fechaRespuesta);
		tramiteEnvioPide.setCreatedByUser(securityHelper.obtenerUserIdSession());
		tramiteEnvioPideJPARepository.save(tramiteEnvioPide);

		return resultadoEnvio;


	}

	public String getFileName(String fileNameOriginal) {
		String fileName = org.springframework.util.StringUtils.cleanPath(fileNameOriginal);
		//Reemplazamos el doble punto por uno solo
		if (fileName.contains("..")) {
			fileName = fileName.replace("..",".");
		}
		return fileName;
	}

	public void enviarTramitePendientePide() {
		//Se obtiene la lista de tramite con estado POR_ENVIAR_PIDE
		List<Tramite> tramiteList = tramiteJPARepository.findByEstado(EstadoTramiteConstant.POR_ENVIAR_PIDE);

		//Iteramos por cada uno y enviamos
		tramiteList.stream().forEach(x -> {
			log.info("PROCESANDO TRAMITE:" + x.getNumeroTramite());
			enviarTramitePendientePideAutomatico(x);
		});

	}

	public void enviarTramitePendientePideAutomatico(Tramite tramite)  {
		String estadoTramite = tramite.getEstado();
		log.info("estado inicial tramite: " + estadoTramite);
		Long cantidadIntentos = tramiteEnvioPideJPARepository.obtenerSecuencia(tramite.getId());
		Long cantidadMaximaIntentos = Long.parseLong(env.getProperty("app.micelaneos.cantidad-maxima-intentos").toString());
		try {
			Map resultadEnvio = enviarTramiteAPide(tramite.getId());
			//Vemos el indicados de resultado, si es ok entonces solocamos el estado enviado pide, sino con error pide.
			estadoTramite = EstadoTramiteConstant.ENVIADO_PIDE;
			if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR)){
				estadoTramite = EstadoTramiteConstant.CON_ERROR_PIDE;
			}
			if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR_SERVICIO)){
				estadoTramite = EstadoTramiteConstant.POR_ENVIAR_PIDE;
				tramite.setIntentosEnvio(tramite.getIntentosEnvio()==null?1:tramite.getIntentosEnvio()+1);
				//resultadEnvio.put("mensaje", Map.of("vcodres", "001", "vdesres", "Hubo un error en la transmisión, se volverá a intentar de forma automática en unos momentos y se informará por correo de los resultados"));
			}
			if(resultadEnvio.containsKey("mensaje")){
				tramite.setResultadoTransmision(((Map)resultadEnvio.get("mensaje")).get("vdesres").toString());
			}
		} catch (Exception e) {
			log.error("ERROR", e);
		}


		//Si el envio no ha sido exitoso y ya se ha commpletado la cantidad de envios, entonces se coloca en estado con error
		if(cantidadMaximaIntentos.compareTo(cantidadIntentos)==0 && estadoTramite.equals(EstadoTramiteConstant.POR_ENVIAR_PIDE)){
			estadoTramite = EstadoTramiteConstant.CON_ERROR_PIDE;
			tramite.setResultadoTransmision("Se alcanzo el máximo de intentos (" + cantidadIntentos + "), se tendrá que enviar manualmente.");
		}

		tramite.setEstado(estadoTramite);
		save(tramite);

		//TODO Enviamos correo si tiene estado con error PIDE
		//if(estadoTramite.equals(EstadoTramiteConstant.CON_ERROR_PIDE))

	}

	public String renviarTramitePendientePide(String tramiteId) throws Exception {
		Tramite tramite = tramiteJPARepository.findById(tramiteId).get();
		String estadoTramite = tramite.getEstado();
		try {
			Map resultadEnvio = enviarTramiteAPide(tramite.getId());
			//Vemos el indicados de resultado, si es ok entonces solocamos el estado enviado pide, sino con error pide.
			estadoTramite = EstadoTramiteConstant.ENVIADO_PIDE;
			if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR) || resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR_SERVICIO))
				estadoTramite = EstadoTramiteConstant.CON_ERROR_PIDE;
			if(resultadEnvio.containsKey("mensaje")){
				tramite.setResultadoTransmision(((Map)resultadEnvio.get("mensaje")).get("vdesres").toString());
			}
			tramite.setEstado(estadoTramite);
			save(tramite);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw e;
		}

		return estadoTramite;
	}

	public EntidadExterna registrarEntidadExterna(EntidadExterna entidadExterna){
		tramiteEntidadExternaJPARepository.save(entidadExterna);
		return entidadExterna;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void enviarDocumentoParaFirma(DatosFirmaDocumentoRequest datosFirmaDocumentoRequest, MultipartFile filePrincipal, Tramite tramite) throws Exception {
		FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest = new FirmaDocumentoTramiteHibridoBodyRequest();
		firmaDocumentoTramiteHibridoBodyRequest.setTextoFirma(datosFirmaDocumentoRequest.getTextoFirma());
		firmaDocumentoTramiteHibridoBodyRequest.setPosition(datosFirmaDocumentoRequest.getPosition());
		firmaDocumentoTramiteHibridoBodyRequest.setOrientacion(datosFirmaDocumentoRequest.getOrientacion());
		firmaDocumentoTramiteHibridoBodyRequest.setPositionCustom(datosFirmaDocumentoRequest.getPositionCustom());
		firmaDocumentoTramiteHibridoBodyRequest.setPin(datosFirmaDocumentoRequest.getPin());
		firmaDocumentoTramiteHibridoBodyRequest.setUsuarioFirmaLogoId(datosFirmaDocumentoRequest.getUsuarioFirmaLogoId());
		firmaDocumentoTramiteHibridoBodyRequest.setFile(filePrincipal);
		firmaDocumentoTramiteHibridoBodyRequest.setTramiteId(tramite.getId());
		firmaDocumentoTramiteHibridoBodyRequest.setTipoDocumentoFirma(TipoDocumentoFirmaConstant.DOCUMENTO_TRAMITE_PIDE);
		firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);

		/*
		//Al ser asincrono, se espera un mmomento a que el documento ya se encuentre firmado
		DocumentoAdjuntoRequest documentoAdjuntoRequest = DocumentoAdjuntoRequest.builder().descripcion(DescripcionDocumentoAdjuntoConstant.DOCUMENTO_FIRMADO_DIGITALMENTE).build();
		List<DocumentoAdjunto> documentoAdjuntoList = null;
		boolean seObtuvoDocumentoFirmado = false;
		int cantidadIntentos = 0;
		int cantidadIntentosMaximo = 5;
		while(!seObtuvoDocumentoFirmado){
			if(cantidadIntentos == cantidadIntentosMaximo)
				throw new ServiceException("No se pudo firmar documento, volver a intentarlo en breve");

			documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
			if(!CollectionUtils.isEmpty(documentoAdjuntoList)){
				DocumentoAdjunto documentoAdjunto = documentoAdjuntoList.get(0);
				documentoAdjunto.setSeccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL);
				documentoAdjunto.setTipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE);
				documentoAdjuntoService.guardarAdjunto(documentoAdjunto);
				seObtuvoDocumentoFirmado = true;
			}else{
				Thread.sleep(1000);
				++cantidadIntentos;
			}
		}
		*/
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void actualizarDatosDocumentoFirmadoDigitalmente(String tramiteId) throws Exception {
		//Al ser asincrono, se espera un momento a que el documento ya se encuentre firmado
		DocumentoAdjuntoRequest documentoAdjuntoRequest = DocumentoAdjuntoRequest.builder()
				.descripcion(DescripcionDocumentoAdjuntoConstant.DOCUMENTO_FIRMADO_DIGITALMENTE)
				.seccionAdjunto(SeccionAdjuntoConstant.PRINCIPAL)
				.tipoAdjunto(TipoAdjuntoConstant.DOCUMENTO_TRAMITE)
				.tramiteId(tramiteId)
				.build();

		List<DocumentoAdjunto> documentoAdjuntoList = null;
		boolean seObtuvoDocumentoFirmado = false;
		int cantidadIntentos = 0;
		int cantidadIntentosMaximo = 10;
		while(!seObtuvoDocumentoFirmado){
			if(cantidadIntentos == cantidadIntentosMaximo)
				throw new ServiceException("No se pudo firmar documento, volver a intentarlo en breve");

			documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
			if(!CollectionUtils.isEmpty(documentoAdjuntoList)){
				seObtuvoDocumentoFirmado = true;
			}else{
				Thread.sleep(1000);
				++cantidadIntentos;
			}
		}
	}

	private Integer obtenerNumeroTramiteDependencia(String dependenciaId){
		Integer anioActual = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
		return tramiteJPARepository.obtenerNumeroTramiteByDependencia(dependenciaId,anioActual).intValue();
	}

	public TipoTramite generarTipoTramite(TramiteBodyRequest tramiteBodyRequest){
		TipoTramite tipoTramite = null;
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.EXTERNO))
			tipoTramite = tipoTramiteJPARepository.findByTipoTramite(TipoTramiteConstant.EXTERNO_MESA_PARTES).get(0);
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.INTERNO)
				&& tramiteBodyRequest.getOrigen().equals(OrigenConstant.INTERNO))
			tipoTramite = tipoTramiteJPARepository.findByTipoTramite(TipoTramiteConstant.INTERNO).get(0);
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.INTERNO)
				&& tramiteBodyRequest.getOrigen().equals(OrigenConstant.EXTERNO))
			tipoTramite = tipoTramiteJPARepository.findByTipoTramite(TipoTramiteConstant.INTERNO_REGUL).get(0);
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.PIDE))
			tipoTramite = tipoTramiteJPARepository.findByTipoTramite(TipoTramiteConstant.EXTERNO_PIDE).get(0);
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.INTERNO)
				&& tramiteBodyRequest.getOrigen().equals(OrigenConstant.PIDE))
			tipoTramite = tipoTramiteJPARepository.findByTipoTramite(TipoTramiteConstant.DESPACHO_PIDE).get(0);

		return tipoTramite;
	}

	public String obtenerCuo(){

		String environment = env.getProperty("app.micelaneos.environment");
		String cuo = null;
		ObjectFactory objectFactory = new ObjectFactory();

		if(environment.equals("TEST")){ //TEST
			GetCUO cuoEntidad = objectFactory.createGetCUO();
			cuoEntidad.setIp("127.0.0.1");
			GetCUOResponse getCUOEntidadResponse = soapCuoConnector.callWebService(cuoEntidad);
			cuo = getCUOEntidadResponse.getReturn();
		}else if(environment.equals("PROD")){ //PRODUCCION
			GetCUOEntidad cuoEntidad = objectFactory.createGetCUOEntidad();
			cuoEntidad.setRuc(env.getProperty("app.micelaneos.ruc-amsac"));
			cuoEntidad.setServicio(env.getProperty("app.micelaneos.servicio-cuo"));
			GetCUOEntidadResponse getCUOEntidadResponse = soapCuoConnector.callWebService(cuoEntidad);
			cuo = getCUOEntidadResponse.getReturn();
		}else{ //DESARROLLO
			cuo = "0000000040";
		}

		return cuo;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void firmarDocumentoAcuse(Map mapaArchivo, String pinFirma, String tramiteId) throws Exception {
		//Obtenemos el archiov
		Path path = Paths.get(mapaArchivo.get("ruta").toString());
		byte[] archivoAcuseByteArray = Files.readAllBytes(path);
		CustomMultipartFile file = new CustomMultipartFile(archivoAcuseByteArray,mapaArchivo.get("nombreArchivo").toString(),"application/pdf");

		//Obtenemos el usuario firma logo id
		String usuarioFirmaLogoId = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(securityHelper.obtenerUserIdSession()).getId()).get(0).getId();
		FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest = new FirmaDocumentoTramiteHibridoBodyRequest();
		firmaDocumentoTramiteHibridoBodyRequest.setTextoFirma("En señal de conformidad");
		firmaDocumentoTramiteHibridoBodyRequest.setPosition("3.5");
		firmaDocumentoTramiteHibridoBodyRequest.setOrientacion("VERTICAL");
		firmaDocumentoTramiteHibridoBodyRequest.setPin(pinFirma);
		firmaDocumentoTramiteHibridoBodyRequest.setUsuarioFirmaLogoId(usuarioFirmaLogoId);
		firmaDocumentoTramiteHibridoBodyRequest.setFile(file);
		firmaDocumentoTramiteHibridoBodyRequest.setTramiteId(tramiteId);
		firmaDocumentoTramiteHibridoBodyRequest.setTipoDocumentoFirma(TipoDocumentoFirmaConstant.DOCUMENTO_ACUSE_PIDE);
		firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void actualizarAcuseComoDocumentoDelTramite(String tramiteId) throws Exception {

		DocumentoAdjuntoRequest documentoAdjuntoRequest = DocumentoAdjuntoRequest.builder()
				.descripcion(DescripcionDocumentoAdjuntoConstant.DOCUMENTO_FIRMADO_DIGITALMENTE)
				.tipoAdjunto(TipoAdjuntoConstant.ACUSE_RECIBO_TRAMITE_PIDE)
				.seccionAdjunto(SeccionAdjuntoConstant.SECUNDARIO)
				.tramiteId(tramiteId)
				.build();
		List<DocumentoAdjunto> documentoAdjuntoList = null;
		boolean seObtuvoDocumentoFirmado = false;
		int cantidadIntentos = 0;
		int cantidadIntentosMaximo = 6;
		while(!seObtuvoDocumentoFirmado){
			if(cantidadIntentos == cantidadIntentosMaximo)
				throw new ServiceException("No se pudo firmar documento, volver a intentarlo en breve");

			documentoAdjuntoList = documentoAdjuntoService.obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
			if(!CollectionUtils.isEmpty(documentoAdjuntoList)){
				seObtuvoDocumentoFirmado = true;
			}else{
				Thread.sleep(1000);
				++cantidadIntentos;
			}
		}

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void eliminarTramite(String tramiteId){
		//Se considera eliminacion logica
		Tramite tramite = findById(tramiteId);
		tramite.setEstado(EstadoTramiteConstant.ELIMINADO);
		save(tramite);

	}

	public Map generarReporteAcuseTramiteInteroperabilidadObservacion(Map<String, Object> parameters) throws Exception {

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream url = classloader.getResourceAsStream("acuseTramiteExternoInteroperabilidadObservado.jrxml");

		JasperReport jasperReport = JasperCompileManager.compileReport(url);

		/*
		DateFormat Formato = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String fechaGeneracion = Formato.format(determinarFechaGeneracion(tramite.getCreatedDate()));

		//Obtener Tipo Documento de Tramite
		TipoDocumentoTramite tipoDocumento = tipoDocumentoJPARepository.findById(tramite.getTipoDocumento().getId()).get();

		// Parameters for report
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("tipoDocumento", tipoDocumento.getTipoDocumento().toUpperCase());
		parameters.put("fechaGeneracion", fechaGeneracion);
		parameters.put("fechaHoraIngreso", fechaGeneracion);
		parameters.put("estado", "RECEPCIONADO");
		parameters.put("emisorRazonSocial", tramite.getEntidadExterna().getRazonSocial());// user.getPersona().getRazonSocialNombre().toUpperCase());
		parameters.put("emisorRuc", tramite.getEntidadExterna().getRucEntidadRemitente()); // user.getPersona().getNumeroDocumento());
		parameters.put("asunto", tramite.getAsunto());
		parameters.put("cuo", dependenciaInternaDestino);
		parameters.put("observacion", dependenciaInternaDestino);
		*/

		List<String> lista = null;
		JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(lista);

		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters,source);

		//Directorio donde se guardará una copia fisica
		String nombreArchivoAcuse = "acuseRecibo-" + new SimpleDateFormat("ddMMyyyyHHmmssSSS").format(new Date()) + ".pdf";

		String rutaAcuse = env.getProperty("file.base-upload-dir") + File.separator + "temporales";
		fileStorageService.createDirectory(rutaAcuse);

		final String reportPdf = rutaAcuse + File.separator + nombreArchivoAcuse;

		//Guardamos en el directorio
		JasperExportManager.exportReportToPdfFile(print, reportPdf);

		Map<String, Object> param = new HashMap<>();
		param.put("ruta",reportPdf);
		//param.put("numeroTramite",tramite.getNumeroTramite());
		//param.put("correo",user.getEmail());
		//param.put("tramiteId",tramite.getId());
		param.put("nombreArchivo",nombreArchivoAcuse);

		return param;
	}

	/*
	public Map generarAcuseObservacionFirmado(AcuseReciboObservacionPideRequest acuseReciboObservacionPideRequest) throws Exception {

		//Generamos el acuse
		Map<String, Object> parameters = new ObjectMapper().convertValue(acuseReciboObservacionPideRequest, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> param =  generarReporteAcuseTramiteInteroperabilidadObservacion(parameters);

		//Ahora se firma el documento
		Resource fileResource = firmarDocumentoAcuseObservado(param, acuseReciboObservacionPideRequest.getPinFirma());

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("file",fileResource);
		resultMap.put("nombreArchivo",fileResource.getFilename());

		return resultMap;
	}
	*/

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String firmarDocumentoAcuseObservado(Map mapaArchivo, String pinFirma) throws Exception {
		//Obtenemos el archiov
		Path path = Paths.get(mapaArchivo.get("ruta").toString());
		byte[] archivoAcuseByteArray = Files.readAllBytes(path);
		CustomMultipartFile file = new CustomMultipartFile(archivoAcuseByteArray,mapaArchivo.get("nombreArchivo").toString(),"application/pdf");

		//Obtenemos el usuario firma logo id
		String usuarioFirmaLogoId = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(securityHelper.obtenerUserIdSession()).getId()).get(0).getId();
		FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest = new FirmaDocumentoTramiteHibridoBodyRequest();
		firmaDocumentoTramiteHibridoBodyRequest.setTextoFirma("En señal de conformidad");
		firmaDocumentoTramiteHibridoBodyRequest.setPosition("3.5");
		firmaDocumentoTramiteHibridoBodyRequest.setOrientacion("VERTICAL");
		firmaDocumentoTramiteHibridoBodyRequest.setPin(pinFirma);
		firmaDocumentoTramiteHibridoBodyRequest.setUsuarioFirmaLogoId(usuarioFirmaLogoId);
		firmaDocumentoTramiteHibridoBodyRequest.setFile(file);
		//firmaDocumentoTramiteHibridoBodyRequest.setTramiteId(tramiteId);
		firmaDocumentoTramiteHibridoBodyRequest.setTipoDocumentoFirma(TipoDocumentoFirmaConstant.DOCUMENTO_ACUSE_OBSERVADO_PIDE);
		String idTransaccionFirma = firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);

		/*
		//Obtener el archivo firmado
		boolean encontrado = false;
		Resource resource = null;
		Integer cantidadIntentos = 0;
		Integer cantidadIntentosMaximo = 5;
		while(!encontrado){
			Thread.sleep(1000l);
			if(cantidadIntentos<=cantidadIntentosMaximo)
				throw new ServiceException("NO SE OBTUVO EL ARCHIVO ACUSE FIRMADO");
			try{
				++cantidadIntentos;
				resource = firmaDocumentoService.obtenerDocumentoExternoFirmado(idTransaccionFirma);
				encontrado = true;
			}catch (ResourceNotFoundException ex){
				log.info("ARCHIVO NO ENCONTRADO "+idTransaccionFirma);
			}catch (Exception ex){
				throw ex;
			}
		}
		return resource;
		*/
		return idTransaccionFirma;

	}


	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Tramite recepcionarTramitePide(TramitePIDERecepcionBodyRequest tramiteBodyRequest) throws Exception {

		/*
		if(StringUtils.isBlank(tramiteBodyRequest.getIdTramiteRelacionado()) && tramiteBodyRequest.isValidarTramiteRelacionado()){
			Map<String, Object> mapaRetorno = numeroDocumentoRepetido(tramiteBodyRequest);
			if(mapaRetorno!=null && !((Map)mapaRetorno.get("atributos")).get("idTramiteRelacionado").toString().equals(tramiteBodyRequest.getId()) ){
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
		//Origen Documento es el atributo que me indica si el documento ha sido registrado por un usuario externo o uno interno
		//Si es externo entonces viene de un usuario externo
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.EXTERNO)){
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
		}
		//Tramites generados de forma interna, lo hace un usuario interno, ya sea de origen interno o externo, este ultimo si estan regularizando un documento que ingreso un documento en fisico.
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.INTERNO)){
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
		//Estos trammites vienen de la recepcion de tramite pide, desde la mesa de partes virtual de pide
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.PIDE)){

			/*
			tramite.setEntidadInterna(null);
			tramite.setEntidadExterna(null);
			tramite.setTramitePrioridad(null);
			tramite.setDependenciaUsuarioCreacion(null);
			tramite.setCargoUsuarioCreacion(null);
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));
			*/

			tramite.setEntidadInterna(null);
			tramite.setRazonSocial(tramiteBodyRequest.getRazonSocial());
			tramite.setDependenciaDestino(null);
			tramite.setCreatedDate(new Date());
			tramite.setFormaRecepcion(formaRecepcionService.findByFormaRecepcion("DIGITAL").get(0));

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
			Tramite tramiteTemporal = tramiteJPARepository.findById(tramite.getId()).get();
			tramite.setCreatedByUser(tramiteTemporal.getCreatedByUser());
			tramite.setCreatedDate(tramiteTemporal.getCreatedDate());
			tramite.setNumeroTramite(tramiteTemporal.getNumeroTramite());
		}

		TramiteBodyRequest tramiteBodyRequestTmp = new TramiteBodyRequest();
		tramiteBodyRequestTmp.setOrigen(tramiteBodyRequest.getOrigen());
		tramiteBodyRequestTmp.setOrigenDocumento(tramiteBodyRequest.getOrigenDocumento());

		tramite.setTipoTramite(generarTipoTramite(tramiteBodyRequestTmp));

		tramiteJPARepository.save(tramite);
		/*
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.EXTERNO)){
			registrarDerivacion(tramite);
			Map param = generarReporteAcuseTramite(tramite);
			DocumentoAdjuntoResponse documentoAdjuntoResponse = registrarAcuseComoDocumentoDelTramite(param);
			param.put("documentoAdjuntoId",documentoAdjuntoResponse.getId());
			enviarAcuseTramite(param);
		}
		*/

		/*
		if(tramiteBodyRequest.getOrigenDocumento().equals(OrigenDocumentoConstant.PIDE)){
			Map param = generarReporteAcuseTramiteInteroperabilidad(tramite,tramiteBodyRequest.getDependenciaInternaDestinoTramitePide());
			tramiteBodyRequest.setId(tramite.getId());
			firmarDocumentoAcuse(param, tramiteBodyRequest.getPinFirma(), tramiteBodyRequest.getId());
			//Nos aseguramos que se haya firmado el documento para continuar, sino lanzamos excepcion
			actualizarAcuseComoDocumentoDelTramite(tramite.getId());
		}
		*/

		//Guardamos datos de la entidad interna
		if(tramite.getEntidadExterna()!=null){
			registrarTramiteEntidadExterna(tramite);
		}
		if(tramite.getEntidadInterna()!=null){
			registrarTramiteEntidadInterna(tramite);
		}

		return tramite;

	}

	public boolean probarConexionAPide(){
		boolean conexionValida = true;
		//Realizamos el envio del tramite
		String respuestaPide = null;
		String estadoSeguimientoEnvio = EstadoTramiteConstant.ENVIADO_PIDE;
		pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory objectFactory = new pe.com.amsac.tramite.pide.soap.tramite.request.ObjectFactory();
		RecepcionTramite recepcionTramite = new RecepcionTramite();
		recepcionTramite.setVrucentrem(env.getProperty("app.micelaneos.ruc-amsac"));
		RecepcionarTramiteResponse recepcionarTramiteResponse = new RecepcionarTramiteResponse();
		recepcionarTramiteResponse.setRequest(recepcionTramite);
		JAXBElement jaxbTramiteResponse = objectFactory.createRecepcionarTramiteResponse(recepcionarTramiteResponse);
		Date fechaEnvio = new Date();
		try{
			JAXBElement jaxbElementResponse = (JAXBElement) soapConnector.callWebService(env.getProperty("app.url.pideServer"), jaxbTramiteResponse);
			//RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = soapConnector.callWebService(recepcionarTramiteResponse);

			RecepcionarTramiteResponseResponse recepcionarTramiteResponseResponse = (RecepcionarTramiteResponseResponse) jaxbElementResponse.getValue();
			log.info("Respuesta Envio: " + recepcionarTramiteResponseResponse.getReturn().getVcodres());

		}catch(Exception ex){
			log.error("ERROR", ex);
			conexionValida = false;
		}
		return conexionValida;
	}


	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String firmarDocumentoCargoPIDE(FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest) throws Exception {
		firmaDocumentoTramiteHibridoBodyRequest.setTipoDocumentoFirma(TipoDocumentoFirmaConstant.DOCUMENTO_ACUSE_PIDE);
		return firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);
	}


	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String firmarDocumentoAcuseObservado(FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest) throws Exception {

		//Obtenemos el usuario firma logo id
		firmaDocumentoTramiteHibridoBodyRequest.setTipoDocumentoFirma(TipoDocumentoFirmaConstant.DOCUMENTO_ACUSE_OBSERVADO_PIDE);
		String idTransaccionFirma = firmaDocumentoService.firmarDocumentoHibrido(firmaDocumentoTramiteHibridoBodyRequest);

		return idTransaccionFirma;

	}

}
