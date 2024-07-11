package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.DatosToken;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ResourceNotFoundException;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteExternoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteHibridoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.CustomMultipartFile;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.FirmaDocumentoJPARepository;
import pe.com.amsac.tramite.bs.util.DescripcionDocumentoAdjuntoConstant;
import pe.com.amsac.tramite.bs.util.EstadoFirmaDocumentoConstant;
import pe.com.amsac.tramite.bs.util.TipoDocumentoFirmaConstant;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class FirmaDocumentoService {

	@Autowired
	private FirmaDocumentoJPARepository firmaDocumentoJPARepository;

	@Autowired
	private DocumentoAdjuntoService documentoAdjuntoService;

	@Autowired
	private Environment environment;

	@Autowired
	private UsuarioFirmaService usuarioFirmaService;

	@Autowired
	private ConfiguracionService configuracionService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	@Autowired
	private ImagenFirmaDigitalService imagenFirmaDigitalService;

	@Autowired
	private ImagenFirmaPositionService imagenFirmaPositionService;

	@Autowired
	private Mapper mapper;

	public void firmarDocumentoTramite(FirmaDocumentoTramiteBodyRequest firmaDocumentoTramiteBodyRequest) throws Exception {
		//Obtenemos el id del usuario que desea firmar
		String usuarioId = securityHelper.obtenerUserIdSession();

		//Obtenemos los datos del usuario
		//UsuarioDTOResponse usuarioBuscarResponse = mapper.map(obtenerUsuarioById(usuarioId), UsuarioDTOResponse.class);

		//Validar mandatoriedad de datos
		//Validamos si el usuario tiene credenciales para firmar o etsa habilitado para firmar
		//List<UsuarioFirma> usuarioFirmaLista = usuarioFirmaService.obtenerUsuarioFirmaByUsuario(usuarioBuscarResponse.getUsuario());
		/*
		List<UsuarioFirma> usuarioFirmaLista = usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);
		if(CollectionUtils.isEmpty(usuarioFirmaLista) || !"A".equals(usuarioFirmaLista.get(0).getEstado())){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","No tiene permisos para firmar o no tiene configurado sus credenciales en el sistema, contactar con el administrador"));
			throw new ServiceException(mensajes);
		}

		UsuarioFirma usuarioFirma = usuarioFirmaLista.get(0);
		*/

		UsuarioFirma usuarioFirma = usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);
		if(!"A".equals(usuarioFirma.getEstado())){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","No tiene permisos para firmar o no tiene configurado sus credenciales en el sistema, contactar con el administrador"));
			throw new ServiceException(mensajes);
		}

		//Crear indicador UUID para le archivo
		String uuidParaArchivoFirmado = UUID.randomUUID().toString();

		//Obtenemos configuracion del servicio
		Configuracion configuracion = configuracionService.obtenerConfiguracion();
		String usernameComponenteFirma = usuarioFirma.getUsernameServicioFirma(); //usuarioFirmaLista.get(0).getUsernameServicioFirma();
		String passwordComponenteFirma = usuarioFirma.getPasswordServicioFirma(); //usuarioFirmaLista.get(0).getPasswordServicioFirma();

		//Armamos la entidad y registramos en la tabla de correspondencia nombre archivo y archivo a firmar.
		DocumentoAdjunto documentoAdjuntoAfirmar = documentoAdjuntoService.obtenerDocumentoAdjunto(firmaDocumentoTramiteBodyRequest.getDocumentoAdjuntoId());
		Resource archivoAFirmar = documentoAdjuntoService.obtenerArchivo(documentoAdjuntoAfirmar);
		//String idDocumentoAdjuntoAFirmar = firmaDocumentoTramiteBodyRequest.getDocumentoAdjuntoId();
		//DocumentoAdjunto documentoAdjuntoAfirmar = crearDocumentoAdjuntoFirmarTest();// documentoAdjuntoService.obtenerDocumentoAdjunto(idDocumentoAdjuntoAFirmar);
		//Resource archivoAFirmar = crearResourceFirmarTest("/Users/ealvino/Downloads/prueba_firma.pdf");

		//Se arma el nombre del archivo firma, que se le concatena la primera inicial del apellido paterno.
		String nombreArchivoFirmado = documentoAdjuntoAfirmar.getNombreArchivo();
		String[] arregloCadena = nombreArchivoFirmado.split("\\.");
		String extension = arregloCadena[arregloCadena.length - 1];
		nombreArchivoFirmado = this.obtenerNombreArchivo(arregloCadena);
		//nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioBuscarResponse.getApePaterno().substring(0,1).toUpperCase() + "." + extension;
		nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioFirma.getSiglaFirma() + "." + extension;

		if(!extension.toUpperCase().equals("PDF")){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E002","ERROR","El archivo no tiene extensión PDF"));
			throw new ServiceException(mensajes);
		}

		FirmaDocumento firmaDocumento = FirmaDocumento.builder()
				.tipoDocumento(TipoDocumentoFirmaConstant.DOCUMENTO_TRAMITE)
				.nombreOriginalDocumento(nombreArchivoFirmado)
				.nombreTemporalDocumento(uuidParaArchivoFirmado)
				.idTramite(documentoAdjuntoAfirmar.getTramite().getId())
				.contentType(documentoAdjuntoAfirmar.getExtension())
				.estado(EstadoFirmaDocumentoConstant.PENDIENTE_FIRMA)
				.fechaRegistro(new Date())
				.idDocumentoAdjuntoOrigen(firmaDocumentoTramiteBodyRequest.getDocumentoAdjuntoId())
				.tramiteDerivacionId(firmaDocumentoTramiteBodyRequest.getTramiteDerivacionId())
				.build();
		firmaDocumentoJPARepository.save(firmaDocumento);

		String idTransaccionFirma = firmaDocumento.getId();

		//Imagen para la firma
		//ImagenFirmaDigital imagenFirmaDigital = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId(), usuarioFirma);
		UsuarioFirmaLogo usuarioFirmaLogo = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getUsuarioFirmaLogoId());
		//byte[] fileContent = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId(), usuarioFirmaLista.get(0).getRutaImagenFirma());
		//ImagenFirmaDigital imagenFirmaDigital = imagenFirmaDigitalService.obtenerImagenFirmaDigitalById(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File(usuarioFirmaLogo.getRutaImagenFirma()));
		byte[] fileContent = FileUtils.readFileToByteArray(usuarioFirmaLogoService.loadFileAsResource(usuarioFirmaLogo).getFile());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File("/Users/ealvino/ealvino/Tools/share_folder/firma amsac/logo-amsac.png"));
		String encodedImagenString = Base64.getEncoder().encodeToString(fileContent);

		//Posicion de la firma
		//ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteBodyRequest.getPositionId(), firmaDocumentoTramiteBodyRequest.getPositionCustom());//imagenFirmaPositionService.obtenerImagenFirmaPositionById(firmaDocumentoTramiteBodyRequest.getPositionId());
		ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteBodyRequest);//imagenFirmaPositionService.obtenerImagenFirmaPositionById(firmaDocumentoTramiteBodyRequest.getPositionId());

		//Colocamos el mensaje en el paragraph format
		String textoFirma = StringUtils.isBlank(firmaDocumentoTramiteBodyRequest.getTextoFirma())?"":firmaDocumentoTramiteBodyRequest.getTextoFirma();
		String paragraphFormat = configuracion.getFirmaParagraphFormat().replace("[MENSAJE]",textoFirma);

		//Armamos la invocacion para la firma
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

		multipartBodyBuilder.part("file_in", archivoAFirmar, MediaType.APPLICATION_PDF);
		multipartBodyBuilder.part("url_out", configuracion.getUrlFirmaBack()+idTransaccionFirma);
		multipartBodyBuilder.part("urlback", configuracion.getUrlLogBack()+idTransaccionFirma);
		multipartBodyBuilder.part("env", configuracion.getFirmaEnv());
		multipartBodyBuilder.part("format", configuracion.getFormatoFirma());
		multipartBodyBuilder.part("username", usernameComponenteFirma);
		multipartBodyBuilder.part("password", passwordComponenteFirma);
		multipartBodyBuilder.part("pin", firmaDocumentoTramiteBodyRequest.getPin());
		multipartBodyBuilder.part("billing_username", configuracion.getBillingUsername());
		multipartBodyBuilder.part("billing_password", configuracion.getBillingPassword());
		multipartBodyBuilder.part("img", encodedImagenString);
		multipartBodyBuilder.part("img_size", usuarioFirmaLogo.getSize());
		multipartBodyBuilder.part("position", imagenFirmaPosition.getPositionpxl());
		multipartBodyBuilder.part("paragraph_format", paragraphFormat);

		//[{ "font" : ["Universal-Bold",15],"align":"right","data_format" : { "timezone":"America/Lima", "strtime":"%d/%m/%Y %H:%M:%S%z"},"format": ["Firmado por:","$(CN)s","$(serialNumber)s","Fecha: $(date)s","[MENSAJE]"]}]

/*
		// Load a file from disk.
		Resource file1 = new FileSystemResource("C:\\logo.png");
		multipartBodyBuilder.part("avatar", file1, MediaType.IMAGE_JPEG);

		// Load the file from the classpath and add some extra request headers.
		Resource file2 = new ClassPathResource("logo.png");
		multipartBodyBuilder.part("avatar", file2, MediaType.TEXT_PLAIN)
				.header("X-Size", "400")
				.header("X-width", "400");

 */

		// multipart/form-data request body
		MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

		// The complete http request body.
		HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(configuracion.getUrlFirmador(), httpEntity,
				String.class);

		//Actulizamos con el id que retorna el firmador
		firmaDocumento.setIdTransaccionFirma(responseEntity.getBody());
		firmaDocumentoJPARepository.save(firmaDocumento);

	}

	public void firmarDocumentoExterno(FirmaDocumentoTramiteExternoBodyRequest firmaDocumentoTramiteExternoBodyRequest) throws Exception {

		//Obtenemos el id del usuario que desea firmar
		String usuarioId = securityHelper.obtenerUserIdSession();

		/*
		//Obtenemos los datos del usuario
		UsuarioDTOResponse usuarioBuscarResponse = mapper.map(obtenerUsuarioById(usuarioId), UsuarioDTOResponse.class);

		//Validar mandatoriedad de datos
		//Validamos si el usuario tiene credenciales para firmar o etsa habilitado para firmar
		List<UsuarioFirma> usuarioFirmaLista = usuarioFirmaService.obtenerUsuarioFirmaByUsuario(usuarioBuscarResponse.getUsuario());
		if(CollectionUtils.isEmpty(usuarioFirmaLista) || !"A".equals(usuarioFirmaLista.get(0).getEstado())){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","No tiene permisos para firmar o no tiene configurado sus credenciales en el sistema, contactar con el administrador"));
			throw new ServiceException(mensajes);
		}

		UsuarioFirma usuarioFirma = usuarioFirmaLista.get(0);
		*/

		UsuarioFirma usuarioFirma = usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);
		if(!"A".equals(usuarioFirma.getEstado())){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","No tiene permisos para firmar o no tiene configurado sus credenciales en el sistema, contactar con el administrador"));
			throw new ServiceException(mensajes);
		}

		//Crear indicador UUID para le archivo
		String uuidParaArchivoFirmado = UUID.randomUUID().toString();

		//Obtenemos configuracion del servicio
		Configuracion configuracion = configuracionService.obtenerConfiguracion();
		String usernameComponenteFirma = usuarioFirma.getUsernameServicioFirma(); //usuarioFirmaLista.get(0).getUsernameServicioFirma();
		String passwordComponenteFirma = usuarioFirma.getPasswordServicioFirma();

		//Se arma el nombre del archivo firma, que se le concatena la primera inicial del apellido paterno.
		String nombreArchivoFirmado = firmaDocumentoTramiteExternoBodyRequest.getFile().getOriginalFilename();
		String[] arregloCadena = nombreArchivoFirmado.split("\\.");
		String extension = arregloCadena[arregloCadena.length - 1];
		nombreArchivoFirmado = this.obtenerNombreArchivo(arregloCadena);
		//nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioBuscarResponse.getApePaterno().substring(0,1).toUpperCase() + "." + extension;
		nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioFirma.getSiglaFirma() + "." + extension;

		if(!extension.toUpperCase().equals("PDF")){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E002","ERROR","El archivo no tiene extensión PDF"));
			throw new ServiceException(mensajes);
		}

		FirmaDocumento firmaDocumento = FirmaDocumento.builder()
				.tipoDocumento(TipoDocumentoFirmaConstant.DOCUMENTO_EXTERNO)
				.nombreOriginalDocumento(nombreArchivoFirmado)
				.nombreTemporalDocumento(uuidParaArchivoFirmado)
				//.idTramite(documentoAdjuntoAfirmar.getTramite().getId())
				.contentType(firmaDocumentoTramiteExternoBodyRequest.getFile().getContentType())
				.estado(EstadoFirmaDocumentoConstant.PENDIENTE_FIRMA)
				.fechaRegistro(new Date())
				.email(firmaDocumentoTramiteExternoBodyRequest.getEmail())
				.build();
		firmaDocumentoJPARepository.save(firmaDocumento);

		String idTransaccionFirma = firmaDocumento.getId();

		//Imagen para la firma
		//ImagenFirmaDigital imagenFirmaDigital = obtenerImagenDeFirma(firmaDocumentoTramiteExternoBodyRequest.getImagenFirmaDigitalId(), usuarioFirma);
		UsuarioFirmaLogo usuarioFirmaLogo = obtenerImagenDeFirma(firmaDocumentoTramiteExternoBodyRequest.getUsuarioFirmaLogoId());
		//byte[] fileContent = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId(), usuarioFirmaLista.get(0).getRutaImagenFirma());
		//ImagenFirmaDigital imagenFirmaDigital = imagenFirmaDigitalService.obtenerImagenFirmaDigitalById(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File(usuarioFirmaLogo.getRutaImagenFirma()));
		byte[] fileContent = FileUtils.readFileToByteArray(usuarioFirmaLogoService.loadFileAsResource(usuarioFirmaLogo).getFile());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File("/Users/ealvino/ealvino/Tools/share_folder/firma amsac/logo-amsac.png"));
		String encodedImagenString = Base64.getEncoder().encodeToString(fileContent);

		//Posicion de la firma
		//ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteExternoBodyRequest.getPositionId(), firmaDocumentoTramiteExternoBodyRequest.getPositionCustom()); //imagenFirmaPositionService.obtenerImagenFirmaPositionById(firmaDocumentoTramiteExternoBodyRequest.getPositionId());
		FirmaDocumentoTramiteBodyRequest firmaDocumentoTramiteBodyRequest = mapper.map(firmaDocumentoTramiteExternoBodyRequest, FirmaDocumentoTramiteBodyRequest.class);
		ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteBodyRequest);

		//Colocamos el mensaje en el paragraph format
		String textoFirma = StringUtils.isBlank(firmaDocumentoTramiteExternoBodyRequest.getTextoFirma())?"":firmaDocumentoTramiteExternoBodyRequest.getTextoFirma();
		String paragraphFormat = configuracion.getFirmaParagraphFormat().replace("[MENSAJE]",textoFirma);

		//Armamos la invocacion para la firma
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

		multipartBodyBuilder.part("file_in", firmaDocumentoTramiteExternoBodyRequest.getFile().getResource(), MediaType.APPLICATION_PDF);
		multipartBodyBuilder.part("url_out", configuracion.getUrlFirmaBack()+idTransaccionFirma);
		multipartBodyBuilder.part("urlback", configuracion.getUrlLogBack()+idTransaccionFirma);
		multipartBodyBuilder.part("env", configuracion.getFirmaEnv());
		multipartBodyBuilder.part("format", configuracion.getFormatoFirma());
		multipartBodyBuilder.part("username", usernameComponenteFirma);
		multipartBodyBuilder.part("password", passwordComponenteFirma);
		multipartBodyBuilder.part("pin", firmaDocumentoTramiteExternoBodyRequest.getPin());
		multipartBodyBuilder.part("billing_username", configuracion.getBillingUsername());
		multipartBodyBuilder.part("billing_password", configuracion.getBillingPassword());
		multipartBodyBuilder.part("img", encodedImagenString);
		multipartBodyBuilder.part("img_size", usuarioFirmaLogo.getSize());
		multipartBodyBuilder.part("position", imagenFirmaPosition.getPositionpxl());
		multipartBodyBuilder.part("paragraph_format", paragraphFormat);

		//[{ "font" : ["Universal-Bold",15],"align":"right","data_format" : { "timezone":"America/Lima", "strtime":"%d/%m/%Y %H:%M:%S%z"},"format": ["Firmado por:","$(CN)s","$(serialNumber)s","Fecha: $(date)s","[MENSAJE]"]}]

/*
		// Load a file from disk.
		Resource file1 = new FileSystemResource("C:\\logo.png");
		multipartBodyBuilder.part("avatar", file1, MediaType.IMAGE_JPEG);

		// Load the file from the classpath and add some extra request headers.
		Resource file2 = new ClassPathResource("logo.png");
		multipartBodyBuilder.part("avatar", file2, MediaType.TEXT_PLAIN)
				.header("X-Size", "400")
				.header("X-width", "400");

 */

		log.info("Se envia a firmar el documento");
		// multipart/form-data request body
		MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

		// The complete http request body.
		HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(configuracion.getUrlFirmador(), httpEntity,
				String.class);

		log.info("Se actualiza el registro de firma documento");
		//Actulizamos con el id que retorna el firmador
		firmaDocumento.setIdTransaccionFirma(responseEntity.getBody());
		firmaDocumentoJPARepository.save(firmaDocumento);


	}

	private String obtenerNombreArchivo(String[] arregloCadena) {
		String nombreArchivo = null;

		for(int i = 0; i < arregloCadena.length - 1; ++i) {
			if (org.apache.commons.lang3.StringUtils.isBlank(nombreArchivo)) {
				nombreArchivo = arregloCadena[i];
			} else {
				nombreArchivo = nombreArchivo.concat(".").concat(arregloCadena[i]);
			}
		}

		return nombreArchivo;
	}

	private LinkedHashMap<Object, Object> obtenerUsuarioById(String usuarioId){
		RestTemplate restTemplate = new RestTemplate();
		String uri = environment.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+usuarioId;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<CommonResponse> response = restTemplate.exchange(uri, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
		return response.getBody().getData()!=null?(LinkedHashMap<Object, Object>) response.getBody().getData():null;
	}

	//private ImagenFirmaDigital obtenerImagenDeFirma(String imagenFirmaDigitalId, UsuarioFirmaLogo usuarioFirmaLogo) throws Exception {
	private UsuarioFirmaLogo obtenerImagenDeFirma(String usuarioFirmaLogoId) throws Exception {
		//ImagenFirmaDigital imagenFirmaDigital = null;
		UsuarioFirmaLogo usuarioFirmaLogo = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoById(usuarioFirmaLogoId);
		//Validamos mandatoriedad
		if(usuarioFirmaLogo==null){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E002","ERROR","No tiene imagen de firma Cargada, cargue un logo de firma y vuelva a intentar"));
			throw new ServiceException(mensajes);
		}
		/*
		if(StringUtils.isBlank(imagenFirmaDigitalId)){
			imagenFirmaDigital = new ImagenFirmaDigital();
			imagenFirmaDigital.setRuta(usuarioFirmaLogo.getRutaImagenFirma());
			imagenFirmaDigital.setSize(usuarioFirmaLogo.getSize());
		}
		else{
			imagenFirmaDigital = imagenFirmaDigitalService.obtenerImagenFirmaDigitalById(imagenFirmaDigitalId);
		}
		return imagenFirmaDigital;
		*/
		return usuarioFirmaLogo;
	}

	private DocumentoAdjunto crearDocumentoAdjuntoFirmarTest(){
		Tramite tramite = new Tramite();
		tramite.setId("sdafasfasdfasf");

		DocumentoAdjunto documentoAdjunto = new DocumentoAdjunto();
		documentoAdjunto.setExtension("application/pdf");
		documentoAdjunto.setNombreArchivo("prueba_firma.pdf");
		documentoAdjunto.setTramite(tramite);

		return documentoAdjunto;
	}

	public Resource crearResourceFirmarTest(String fileName) throws Exception {
		try {
			String fulFilePath = fileName;
			Resource resource = new UrlResource("file:" + fulFilePath);
			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException("Archivo no encontrato " + fileName);
			}
		} catch (MalformedURLException var4) {
			throw new ResourceNotFoundException("Archivo no encontrado " + fileName);
		}
	}

	public void recepcionarFileDocumento(String nombreArchivo, byte[] archivoFirmado) throws Exception {

		FirmaDocumento firmaDocumento = firmaDocumentoJPARepository.findById(nombreArchivo).get();
		if(firmaDocumento.getTipoDocumento().equals(TipoDocumentoFirmaConstant.DOCUMENTO_TRAMITE)){
			procesarDocumentoTramite(nombreArchivo,archivoFirmado);
		}else{
			procesarDocumentoExterno(nombreArchivo,archivoFirmado);
		}

	}
	public void recepcionarLogDocumento(String nombreArchivo, String archivoLog){
		FirmaDocumento firmaDocumento = firmaDocumentoJPARepository.findById(nombreArchivo).get();
		createSecurityContextHolder(firmaDocumento.getCreatedByUser());
		firmaDocumento.setLogFirmador(archivoLog);
		firmaDocumentoJPARepository.save(firmaDocumento);
	}

	private void procesarDocumentoTramite(String nombreArchivo, byte[] archivoFirmado) throws Exception {

		log.info(">> Regisramos documento adjunto: "+nombreArchivo);

		FirmaDocumento firmaDocumento = firmaDocumentoJPARepository.findById(nombreArchivo).get();
		createSecurityContextHolder(firmaDocumento.getCreatedByUser());

		CustomMultipartFile file = new CustomMultipartFile(archivoFirmado,firmaDocumento.getNombreOriginalDocumento(),firmaDocumento.getContentType());

		//DocumentoAdjunto documentoAdjuntoOriginal = documentoAdjuntoService.obtenerDocumentoAdjunto(firmaDocumento.getIdDocumentoAdjuntoOrigen());

		DocumentoAdjuntoBodyRequest documentoAdjuntoRequest = new DocumentoAdjuntoBodyRequest();
		documentoAdjuntoRequest.setTramiteId(firmaDocumento.getIdTramite());
		documentoAdjuntoRequest.setDescripcion(DescripcionDocumentoAdjuntoConstant.DOCUMENTO_FIRMADO_DIGITALMENTE);// "ARCHIVO CON FIRMA DIGITAL");
		documentoAdjuntoRequest.setFile(file);
		documentoAdjuntoRequest.setTramiteDerivacionId(firmaDocumento.getTramiteDerivacionId());
		//documentoAdjuntoRequest.setTramiteDerivacionId(documentoAdjuntoOriginal.getTramiteDerivacionId());
		//documentoAdjuntoRequest.setTipoAdjunto(TipoAdjuntoConstant.);
		documentoAdjuntoService.registrarDocumentoAdjunto(documentoAdjuntoRequest);


		//Actualizamos el estado en firma documento
		firmaDocumento.setEstado(EstadoFirmaDocumentoConstant.FIRMADO);
		firmaDocumento.setFechaFirmaDocumento(new Date());
		firmaDocumentoJPARepository.save(firmaDocumento);

		//TODO: Esto es temporla para pruebas
		//FileUtils.writeByteArrayToFile(new File("/Users/ealvino/Downloads/"+nombreArchivo+".pdf"), archivoFirmado);
	}

	private void procesarDocumentoExterno(String nombreArchivo, byte[] archivoFirmado) throws Exception {

		FileUtils.writeByteArrayToFile(new File( environment.getProperty("app.ruta.documento-firma-externo")+nombreArchivo+".pdf"), archivoFirmado);
		FirmaDocumento firmaDocumento = firmaDocumentoJPARepository.findById(nombreArchivo).get();
		createSecurityContextHolder(firmaDocumento.getCreatedByUser());
		firmaDocumento.setEstado(EstadoFirmaDocumentoConstant.FIRMADO);
		firmaDocumento.setFechaFirmaDocumento(new Date());
		firmaDocumentoJPARepository.save(firmaDocumento);

		//Envio de correo con el documento firmado
		Resource resource = obtenerDocumentoExternoFirmado(nombreArchivo);

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("to",firmaDocumento.getEmail());
		bodyMap.add("asunto","Documento Firmado Digitalmente - AMSAC");
		bodyMap.add("cuerpo","<h4>Estimado(a).</h4> </br> <p>Usted a recibido un documento firmado digitalmente.</p>");
		bodyMap.add("files", resource); //new FileSystemResource(param.get("ruta").toString()));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		String uri = environment.getProperty("app.url.mail") + "/api/mail/sendMailAttach";

		restTemplate.postForEntity( uri, requestEntity, null);
	}

	public Resource obtenerDocumentoExternoFirmado(String firmaDocumentoId) throws Exception {
		try {
			String fulFilePath = environment.getProperty("app.ruta.documento-firma-externo")+firmaDocumentoId+".pdf";
			log.info("fulFilePath: " + fulFilePath);
			Resource resource = new UrlResource("file:" + fulFilePath);
			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException("Archivo no encontrato " + firmaDocumentoId);
			}
		} catch (MalformedURLException var4) {
			throw new ResourceNotFoundException("Archivo no encontrado " + firmaDocumentoId);
		}
	}

	public ImagenFirmaPosition generarPosition(String positionId, String positionCustom) throws Exception {

		if(!StringUtils.isBlank(positionId))
			return imagenFirmaPositionService.obtenerImagenFirmaPositionById(positionId);

		String positionX=positionCustom.split(",")[0];
		String positionY=positionCustom.split(",")[1];

		Integer finalPositionX = Integer.valueOf(positionX)+200;
		Integer finalPositiony = Integer.valueOf(positionY)+50;

		String positionFinalXYFirma = positionX + "," + positionY + "," + String.valueOf(finalPositionX) + "," + String.valueOf(finalPositiony);

		ImagenFirmaPosition imagenFirmaPosition = new ImagenFirmaPosition();
		imagenFirmaPosition.setPositionpxl(positionFinalXYFirma);

		return imagenFirmaPosition;

	}

	public ImagenFirmaPosition generarPosition(FirmaDocumentoTramiteBodyRequest firmaDocumentoTramiteBodyRequest) throws Exception {

		ImagenFirmaPosition imagenFirmaPosition = null;

		//Se obtiene la posicion de la tabla
		if(!StringUtils.isBlank(firmaDocumentoTramiteBodyRequest.getPosition())){
			imagenFirmaPosition = imagenFirmaPositionService.obtenerImagenFirmaPositionByPositionAndOrientacion(firmaDocumentoTramiteBodyRequest.getPosition(), firmaDocumentoTramiteBodyRequest.getOrientacion()).get(0);
		}else{//Se genera la posicion customizada
			String positionCustom = firmaDocumentoTramiteBodyRequest.getPositionCustom();
			String positionX=positionCustom.split(",")[0];
			String positionY=positionCustom.split(",")[1];

			Integer finalPositionX = Integer.valueOf(positionX)+200;
			Integer finalPositiony = Integer.valueOf(positionY)+50;

			String positionFinalXYFirma = positionX + "," + positionY + "," + String.valueOf(finalPositionX) + "," + String.valueOf(finalPositiony);

			imagenFirmaPosition = new ImagenFirmaPosition();
			imagenFirmaPosition.setPositionpxl(positionFinalXYFirma);
		}

		return imagenFirmaPosition;

	}

	//Crear el segurity context
	private void createSecurityContextHolder(String idUser){
		DatosToken datosToken = new DatosToken();
		datosToken.setIdUser(idUser);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(datosToken, null, null);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}


	public void firmarDocumentoHibrido(FirmaDocumentoTramiteHibridoBodyRequest firmaDocumentoTramiteHibridoBodyRequest) throws Exception {

		//Obtenemos el id del usuario que desea firmar
		String usuarioId = securityHelper.obtenerUserIdSession();

		UsuarioFirma usuarioFirma = usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);
		if(!"A".equals(usuarioFirma.getEstado())){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","No tiene permisos para firmar o no tiene configurado sus credenciales en el sistema, contactar con el administrador"));
			throw new ServiceException(mensajes);
		}

		//Crear indicador UUID para le archivo
		String uuidParaArchivoFirmado = UUID.randomUUID().toString();

		//Obtenemos configuracion del servicio
		Configuracion configuracion = configuracionService.obtenerConfiguracion();
		String usernameComponenteFirma = usuarioFirma.getUsernameServicioFirma(); //usuarioFirmaLista.get(0).getUsernameServicioFirma();
		String passwordComponenteFirma = usuarioFirma.getPasswordServicioFirma(); //usuarioFirmaLista.get(0).getPasswordServicioFirma();

		String nombreArchivoFirmado = firmaDocumentoTramiteHibridoBodyRequest.getFile().getOriginalFilename();
		String[] arregloCadena = nombreArchivoFirmado.split("\\.");
		String extension = arregloCadena[arregloCadena.length - 1];
		nombreArchivoFirmado = this.obtenerNombreArchivo(arregloCadena);
		//nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioBuscarResponse.getApePaterno().substring(0,1).toUpperCase() + "." + extension;
		nombreArchivoFirmado = nombreArchivoFirmado + "-" + usuarioFirma.getSiglaFirma() + "." + extension;

		if(!extension.toUpperCase().equals("PDF")){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E002","ERROR","El archivo no tiene extensión PDF"));
			throw new ServiceException(mensajes);
		}

		FirmaDocumento firmaDocumento = FirmaDocumento.builder()
				.tipoDocumento(TipoDocumentoFirmaConstant.DOCUMENTO_TRAMITE)
				.nombreOriginalDocumento(nombreArchivoFirmado)
				.nombreTemporalDocumento(uuidParaArchivoFirmado)
				.idTramite(firmaDocumentoTramiteHibridoBodyRequest.getTramiteId())
				.contentType(firmaDocumentoTramiteHibridoBodyRequest.getFile().getContentType())
				.estado(EstadoFirmaDocumentoConstant.PENDIENTE_FIRMA)
				.fechaRegistro(new Date())
				.tramiteDerivacionId(firmaDocumentoTramiteHibridoBodyRequest.getTramiteDerivacionId())
				.build();
		firmaDocumentoJPARepository.saveAndFlush(firmaDocumento);

		String idTransaccionFirma = firmaDocumento.getId();

		//Imagen para la firma
		//ImagenFirmaDigital imagenFirmaDigital = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId(), usuarioFirma);
		UsuarioFirmaLogo usuarioFirmaLogo = obtenerImagenDeFirma(firmaDocumentoTramiteHibridoBodyRequest.getUsuarioFirmaLogoId());
		//byte[] fileContent = obtenerImagenDeFirma(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId(), usuarioFirmaLista.get(0).getRutaImagenFirma());
		//ImagenFirmaDigital imagenFirmaDigital = imagenFirmaDigitalService.obtenerImagenFirmaDigitalById(firmaDocumentoTramiteBodyRequest.getImagenFirmaDigitalId());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File(usuarioFirmaLogo.getRutaImagenFirma()));
		byte[] fileContent = FileUtils.readFileToByteArray(usuarioFirmaLogoService.loadFileAsResource(usuarioFirmaLogo).getFile());
		//byte[] fileContent = FileUtils.readFileToByteArray(new File("/Users/ealvino/ealvino/Tools/share_folder/firma amsac/logo-amsac.png"));
		String encodedImagenString = Base64.getEncoder().encodeToString(fileContent);

		//Posicion de la firma
		//ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteBodyRequest.getPositionId(), firmaDocumentoTramiteBodyRequest.getPositionCustom());//imagenFirmaPositionService.obtenerImagenFirmaPositionById(firmaDocumentoTramiteBodyRequest.getPositionId());
		FirmaDocumentoTramiteBodyRequest firmaDocumentoTramiteBodyRequest = mapper.map(firmaDocumentoTramiteHibridoBodyRequest, FirmaDocumentoTramiteBodyRequest.class);
		ImagenFirmaPosition imagenFirmaPosition = generarPosition(firmaDocumentoTramiteBodyRequest);//imagenFirmaPositionService.obtenerImagenFirmaPositionById(firmaDocumentoTramiteBodyRequest.getPositionId());

		//Colocamos el mensaje en el paragraph format
		String textoFirma = StringUtils.isBlank(firmaDocumentoTramiteHibridoBodyRequest.getTextoFirma())?"":firmaDocumentoTramiteHibridoBodyRequest.getTextoFirma();
		String paragraphFormat = configuracion.getFirmaParagraphFormat().replace("[MENSAJE]",textoFirma);

		//Armamos la invocacion para la firma
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

		multipartBodyBuilder.part("file_in", firmaDocumentoTramiteHibridoBodyRequest.getFile().getResource(), MediaType.APPLICATION_PDF);
		multipartBodyBuilder.part("url_out", configuracion.getUrlFirmaBack()+idTransaccionFirma);
		multipartBodyBuilder.part("urlback", configuracion.getUrlLogBack()+idTransaccionFirma);
		multipartBodyBuilder.part("env", configuracion.getFirmaEnv());
		multipartBodyBuilder.part("format", configuracion.getFormatoFirma());
		multipartBodyBuilder.part("username", usernameComponenteFirma);
		multipartBodyBuilder.part("password", passwordComponenteFirma);
		multipartBodyBuilder.part("pin", firmaDocumentoTramiteHibridoBodyRequest.getPin());
		multipartBodyBuilder.part("billing_username", configuracion.getBillingUsername());
		multipartBodyBuilder.part("billing_password", configuracion.getBillingPassword());
		multipartBodyBuilder.part("img", encodedImagenString);
		multipartBodyBuilder.part("img_size", usuarioFirmaLogo.getSize());
		multipartBodyBuilder.part("position", imagenFirmaPosition.getPositionpxl());
		multipartBodyBuilder.part("paragraph_format", paragraphFormat);

		//[{ "font" : ["Universal-Bold",15],"align":"right","data_format" : { "timezone":"America/Lima", "strtime":"%d/%m/%Y %H:%M:%S%z"},"format": ["Firmado por:","$(CN)s","$(serialNumber)s","Fecha: $(date)s","[MENSAJE]"]}]

/*
		// Load a file from disk.
		Resource file1 = new FileSystemResource("C:\\logo.png");
		multipartBodyBuilder.part("avatar", file1, MediaType.IMAGE_JPEG);

		// Load the file from the classpath and add some extra request headers.
		Resource file2 = new ClassPathResource("logo.png");
		multipartBodyBuilder.part("avatar", file2, MediaType.TEXT_PLAIN)
				.header("X-Size", "400")
				.header("X-width", "400");

 */

		// multipart/form-data request body
		MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

		// The complete http request body.
		HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(configuracion.getUrlFirmador(), httpEntity,
				String.class);

		//Actulizamos con el id que retorna el firmador
		firmaDocumento.setIdTransaccionFirma(responseEntity.getBody());
		firmaDocumentoJPARepository.saveAndFlush(firmaDocumento);


	}

}
