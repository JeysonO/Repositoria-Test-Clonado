package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.file.bean.FileTxProperties;
import pe.com.amsac.tramite.api.file.bean.TramitePathFileStorage;
import pe.com.amsac.tramite.api.file.bean.UploadFileResponse;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoCargaFromDirectoryBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoMigracionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.DocumentoAdjuntoResponse;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.util.FileUtils;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoJPARepository;
import pe.com.amsac.tramite.bs.repository.TramiteJPARepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DocumentoAdjuntoService {

	@Autowired
	private TramiteJPARepository tramiteJPARepository;

	@Autowired
	private DocumentoAdjuntoJPARepository documentoAdjuntoJPARepository;

	@Autowired
	private TramitePathFileStorage tramiteRuthFileStorage;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

	//@Autowired
	//private MongoTemplate mongoTemplate;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private TramiteService tramiteService;

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private FileTxProperties fileTxProperties;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private EntityManager entityManager;

	public List<DocumentoAdjuntoResponse> obtenerDocumentoAdjuntoList(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {

		List<DocumentoAdjunto> listaDocumentoEscala = obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
		List<DocumentoAdjuntoResponse> responseList = new ArrayList();
		listaDocumentoEscala.forEach((entity) -> {
			responseList.add(mapper.map(entity, DocumentoAdjuntoResponse.class));
		});
		List<DocumentoAdjuntoResponse> documentoAdjuntoResponse = responseList;

		for (DocumentoAdjuntoResponse documentoAdjuntoTMP : documentoAdjuntoResponse) {
			DocumentoAdjunto documentoAdjunto = obtenerDocumentoAdjunto(documentoAdjuntoTMP.getId());
			Resource file = obtenerArchivo(documentoAdjunto);
			documentoAdjuntoTMP.setUploadFileResponse(createUploadFileResponse(file, documentoAdjunto));
			//documentoAdjuntoTMP.setUsuarioCreacionAdjuntoNombre(crearUsuarioCreacionNombre(documentoAdjuntoTMP));
		}
		return documentoAdjuntoResponse;
	}

	public List<DocumentoAdjunto> obtenerDocumentoAdjuntoParams(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {

		/*
		Query andQuery = new Query();
		Criteria andCriteria = new Criteria();
		List<Criteria> andExpression =  new ArrayList<>();
		Map<String, Object> parameters = mapper.map(documentoAdjuntoRequest,Map.class);
		Criteria expression = new Criteria();
		parameters.values().removeIf(Objects::isNull);
		parameters.forEach((key, value) -> expression.and(key).is(value));
		andExpression.add(expression);
		andQuery.addCriteria(andCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		List<DocumentoAdjunto> documentoAdjuntoList = mongoTemplate.find(andQuery, DocumentoAdjunto.class);
		*/
		Map<String, Object> parameters = mapper.map(documentoAdjuntoRequest, Map.class);
		parameters.values().removeIf(Objects::isNull);

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DocumentoAdjunto> query = builder.createQuery(DocumentoAdjunto.class);
		Root<DocumentoAdjunto> root = query.from(DocumentoAdjunto.class);

		List<Predicate> listaFiltros = new ArrayList<>();
		parameters.forEach((key, value) -> builder.equal(root.get(key), value));

		List<DocumentoAdjunto> documentoAdjuntoList = entityManager.createQuery(query.select(root).where((Predicate[])listaFiltros.toArray())).getResultList();

		return documentoAdjuntoList;
	}

	public DocumentoAdjuntoResponse registrarDocumentoAdjunto(DocumentoAdjuntoBodyRequest documentoAdjuntoRequest) throws Exception {

		//Validar suma de adjuntos
		List<Mensaje> mensajesError = validarAdjuntos(documentoAdjuntoRequest);
		if(!CollectionUtils.isEmpty(mensajesError)){
			throw new ServiceException(mensajesError);
		}

		String fileName = fileStorageService.getFileName(documentoAdjuntoRequest.getFile().getOriginalFilename());
		String fileNameInServer = UUID.randomUUID().toString() + "." + obtenerExtensionArchivo(fileName);
		Usuario usuario = new Usuario();
		usuario.setId(securityHelper.obtenerUserIdSession());

		DocumentoAdjunto documentoAdjunto = mapper.map(documentoAdjuntoRequest, DocumentoAdjunto.class);
		documentoAdjunto.setEstado("A");
		documentoAdjunto.setNombreArchivo(fileName);
		documentoAdjunto.setNombreArchivoDescarga(fileName);
		documentoAdjunto.setNombreArchivoServer(fileNameInServer);
		documentoAdjunto.setExtension(documentoAdjuntoRequest.getFile().getContentType());
		documentoAdjunto.setUsuarioCreacionAdjunto(usuario);
		documentoAdjunto.setTramiteDerivacionId(documentoAdjuntoRequest.getTramiteDerivacionId());

		//Primero registramos el archivo en disco
		//11631

		Tramite tramiteTmp = null;

		String rutaArchivo = null;

		if(documentoAdjunto.getTramite()!=null
				&& documentoAdjunto.getTramite().getId()!=null) {
			// Se crea el archivo para guardarlo
			tramiteTmp = tramiteJPARepository.findById(documentoAdjunto.getTramite().getId()).get();
			String nombreArchivoDescarga = crearNombreDescarga(tramiteTmp,fileName);
			//String nombreArchivoDescarga = "CUT_"+tramiteTmp.getNumeroTramite()+"_"+tramiteTmp.getNumeroDocumento()+"_"+documentoAdjuntoRequest.getFile().getOriginalFilename();
			documentoAdjunto.setNombreArchivoDescarga(nombreArchivoDescarga);

			// Seteamos la ruta
			//fileStorageService.setFileStorageLocation(construirRutaArchivo(tramiteTmp, documentoAdjunto.getTipoAdjunto()));
			rutaArchivo = construirRutaArchivo(tramiteTmp, documentoAdjunto.getTipoAdjunto());
		}else {
			// Seteamos la ruta
			//fileStorageService.setFileStorageLocation(construirRutaArchivo(documentoAdjunto));
			rutaArchivo = construirRutaArchivo(documentoAdjunto);
			//rutaArchivo = crearRutaDocumentoAdjunto(documentoAdjunto);
		}

		// Registramos el archivo adjunto
		// Enviamos reemplazar igual a N siempre, para que no chanque los archivos.
		//fileName = fileStorageService.storeFile(documentoAdjuntoRequest.getFile().getInputStream(), fileName,false);
		//documentoAdjunto.setNombreArchivo(fileName);
		//fileNameInServer = fileStorageService.storeFile(documentoAdjuntoRequest.getFile().getInputStream(), fileNameInServer,false);
		fileNameInServer = fileStorageService.storeFile(rutaArchivo,documentoAdjuntoRequest.getFile().getInputStream(), fileNameInServer,false);
		documentoAdjunto.setNombreArchivoServer(fileNameInServer);

		//Seteamos tamanio de archivo
		documentoAdjunto.setSize(documentoAdjuntoRequest.getFile().getSize());
		//Guardamos en BD
		documentoAdjuntoJPARepository.save(documentoAdjunto);

		DocumentoAdjuntoResponse response = mapper.map(documentoAdjunto, DocumentoAdjuntoResponse.class);
		response.setUploadFileResponse(createUploadFileResponse(documentoAdjuntoRequest.getFile(), documentoAdjunto));

		//Marcar el tramite derivacion adjunto como que tiene adjunto
		actualizarAdjuntosPorTramiteDerivacion(documentoAdjunto.getTramiteDerivacionId());

		return response;
	}

	private String construirRutaArchivo(Tramite tramite, String tipoAdjunto) {
		DocumentoAdjunto documentoAdjunto = new DocumentoAdjunto();
		documentoAdjunto.setTipoAdjunto(tipoAdjunto);
		documentoAdjunto.setTramite(tramite);
		return construirRutaArchivo(documentoAdjunto);
		//return crearRutaDocumentoAdjunto(documentoAdjunto);

	}
	private String construirRutaArchivo(DocumentoAdjunto documentoAdjunto) {
		String ruta = "";

		ruta = tramiteRuthFileStorage.setObject(documentoAdjunto).build();

		fileStorageService.createDirectory(ruta);

		return ruta;
	}

	private UploadFileResponse createUploadFileResponse(MultipartFile file, DocumentoAdjunto documentoAdjunto) {

		String fileDownloadUri = "/documentos-adjuntos"
				.concat("/downloadFile/" + documentoAdjunto.getId());

		String fileName = fileStorageService.getFileName(file.getOriginalFilename());
		UploadFileResponse uploadFileResponse = new UploadFileResponse(fileName, fileDownloadUri,
				file.getContentType(), FileUtils.getFileSize(file.getSize()));
		/*
		UploadFileResponse uploadFileResponse = new UploadFileResponse(file.getOriginalFilename(), fileDownloadUri,
				file.getContentType(), FileUtils.getFileSize(file.getSize()));

		 */
		return uploadFileResponse;
	}

	public Resource obtenerDocumentoAdjunto(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
		DocumentoAdjunto documentoAdjunto = obtenerDocumentoAdjunto(documentoAdjuntoRequest.getId());
		return obtenerArchivo(documentoAdjunto);
	}

	public Resource obtenerArchivo(DocumentoAdjunto documentoAdjunto) throws Exception {
			//fileStorageService.setFileStorageLocation(tramiteRuthFileStorage.setObject(documentoAdjunto).build());

		//return fileStorageService.loadFileAsResource(documentoAdjunto.getNombreArchivo());

		//Creamos la ruta de acuerdo a los datos del documento
		String rutaArchivo = crearRutaDocumentoAdjunto(documentoAdjunto);
		//Obtenemos el archivo
		return fileStorageService.loadFileAsResource(rutaArchivo, documentoAdjunto.getNombreArchivoServer());
	}

	public DocumentoAdjunto obtenerDocumentoAdjunto(String documentoAdjuntoId) throws Exception {
		return documentoAdjuntoJPARepository.findById(documentoAdjuntoId).get();
	}

	private UploadFileResponse createUploadFileResponse(Resource file, DocumentoAdjunto documentoAdjunto)
			throws IOException {
		String fileDownloadUri = "/documentos-adjuntos"
				.concat("/downloadFile/" + documentoAdjunto.getId());

		Tramite tramite = documentoAdjunto.getTramite();
		if(StringUtils.isBlank(documentoAdjunto.getNombreArchivoDescarga()) && tramite!=null)
			documentoAdjunto.setNombreArchivoDescarga(crearNombreDescarga(tramite,documentoAdjunto.getNombreArchivo()));

		String nombreArchivo = StringUtils.isBlank(documentoAdjunto.getNombreArchivoDescarga())?documentoAdjunto.getNombreArchivo():documentoAdjunto.getNombreArchivoDescarga();

		UploadFileResponse uploadFileResponse = new UploadFileResponse(nombreArchivo, fileDownloadUri,
				documentoAdjunto.getExtension(), FileUtils.getFileSize(file.contentLength()));
		/*
		UploadFileResponse uploadFileResponse = new UploadFileResponse(file.getFilename(), fileDownloadUri,
				documentoAdjunto.getExtension(), FileUtils.getFileSize(file.contentLength()));
		*/
		return uploadFileResponse;
	}

	private List<Mensaje> validarAdjuntos(DocumentoAdjuntoBodyRequest documentoAdjuntoBodyRequest) throws Exception {
		Double suma = 0.0;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("tramiteId",documentoAdjuntoBodyRequest.getTramiteId());
		DocumentoAdjuntoRequest documentoAdjuntoRequest = mapper.map(parameters,DocumentoAdjuntoRequest.class);
		List<Mensaje> mensajes = new ArrayList<>();
		if(!obtenerDocumentoAdjuntoList(documentoAdjuntoRequest).isEmpty()){
			List<DocumentoAdjuntoResponse> adjuntos = obtenerDocumentoAdjuntoList(documentoAdjuntoRequest);
			if(!CollectionUtils.isEmpty(Arrays.asList(adjuntos))){
				for (DocumentoAdjuntoResponse tmp : adjuntos) {
					String[] textoSeparado = tmp.getUploadFileResponse().getSize().split("\\s");
					String unidad = textoSeparado[1];
					double tamaño = Double.parseDouble(textoSeparado[0]);
					if(unidad.equals("KB"))
						suma = suma + (tamaño/1024);
					else
						suma = suma + tamaño;
				}
			}
			double tamañoFileActual = documentoAdjuntoBodyRequest.getFile().getSize()/1048576;
			suma = suma + tamañoFileActual;
			String regEx="[^0-9]+";
			Pattern pattern = Pattern.compile(regEx);
			String[] cs = pattern.split(env.getProperty("spring.servlet.multipart.max-file-size"));
			double sumTotal = Double.parseDouble(cs[0]);
			String tamañoArchivo = String.valueOf(Math.round((sumTotal-suma)*100.0)/100.0).replace("-","");
			if(suma>sumTotal){
				mensajes.add(new Mensaje("E001","ERROR","La suma total de los archivos adjuntos para este Tramite excede el tamaño permitido. El archivo a adjuntar debe tener un tamaño menor a:" + tamañoArchivo));
			}
		}
		return mensajes;
	}

	public void registrarDocumentoAdjuntoMigracion(DocumentoAdjuntoMigracionBodyRequest documentoAdjuntoMigracionBodyRequest ) throws Exception {

		//Se obtiene el archivo de la carpeta determinada
		String rutaArchivoTramiteActual = documentoAdjuntoMigracionBodyRequest.getCarpetaDocumento() + "/" + documentoAdjuntoMigracionBodyRequest.getNombreArchivo();
		String rutaCompletaArchivo = "/tramite/file/uploads/"+rutaArchivoTramiteActual;
		File fileTramiteAntiguo = new File(rutaCompletaArchivo);

		Path path = fileTramiteAntiguo.toPath();
		String mimeType = Files.probeContentType(path);

		//Validar suma de adjuntos
		String fileName = documentoAdjuntoMigracionBodyRequest.getNombreOriginalArchivo();//fileStorageService.getFileName(documentoAdjuntoMigracionBodyRequest.getFile().getOriginalFilename());
		Tramite tramite = tramiteService.findById(documentoAdjuntoMigracionBodyRequest.getTramiteId());
		String fileNameInServer = UUID.randomUUID().toString() + "." + obtenerExtensionArchivo(fileName);
		/*
		Usuario usuario = new Usuario();
		usuario.setId(documentoAdjuntoMigracionBodyRequest.get);
		*/

		DocumentoAdjunto documentoAdjunto = new DocumentoAdjunto();
		documentoAdjunto.setNombreArchivo(fileName);
		documentoAdjunto.setEstado("A");
		documentoAdjunto.setExtension(mimeType);
		documentoAdjunto.setDescripcion(documentoAdjuntoMigracionBodyRequest.getDescripcion());
		documentoAdjunto.setTramite(tramite);
		documentoAdjunto.setNombreArchivoServer(fileNameInServer);
		documentoAdjunto.setSize(Files.size(path));

		//Primero registramos el archivo en disco

		Tramite tramiteTmp = null;
		String rutaArchivo = null;
		if(documentoAdjunto.getTramite()!=null
				&& documentoAdjunto.getTramite().getId()!=null) {
			// Se crea el archivo para guardarlo
			tramiteTmp = tramiteJPARepository.findById(documentoAdjunto.getTramite().getId()).get();

			// Seteamos la ruta
			/*
			fileStorageService.setFileStorageLocation(
					construirRutaArchivo(tramiteTmp, documentoAdjunto.getTipoAdjunto()));
			*/
			rutaArchivo = construirRutaArchivo(tramiteTmp, documentoAdjunto.getTipoAdjunto());
		}else {
			// Seteamos la ruta
			/*
			fileStorageService.setFileStorageLocation(
					construirRutaArchivo(documentoAdjunto));
			*/
			//rutaArchivo = construirRutaArchivo(documentoAdjunto);
			rutaArchivo = crearRutaDocumentoAdjunto(documentoAdjunto);
		}

		// Registramos el archivo adjunto
		// Enviamos reemplazar igual a N siempre, para que no chanque los archivos.
		//TODO: Revisar aca no debe usar este metodo
		//fileNameInServer = fileStorageService.storeFile(new FileInputStream(fileTramiteAntiguo), fileNameInServer,false);
		fileNameInServer = fileStorageService.storeFileMigracion(rutaArchivo, new FileInputStream(fileTramiteAntiguo), fileNameInServer,false);
		documentoAdjunto.setNombreArchivoServer(fileNameInServer);

		//Guardamos en BD
		documentoAdjuntoJPARepository.save(documentoAdjunto);

		//Marcar el tramite derivacion adjunto como que tiene adjunto
		actualizarAdjuntosPorTramiteDerivacion(documentoAdjunto.getTramiteDerivacionId());

	}

	private String obtenerExtensionArchivo(String fileName){
		String[] arregloCadena = fileName.split("\\.");
		return arregloCadena[arregloCadena.length - 1];
	}

	public String crearRutaDocumentoAdjunto(DocumentoAdjunto documentoAdjunto) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String ruta = fileTxProperties.getBaseUploadDir()
				.concat(File.separator)
				.concat(String.valueOf(sdf.format(documentoAdjunto.getTramite().getCreatedDate())))
				.concat(File.separator)
				.concat(String.valueOf(documentoAdjunto.getTramite().getId())
						.concat(File.separator)
						.concat("adjuntos"));
		return ruta;
	}

	public InputStreamResource obtenerDocumentoAdjuntoBlob(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
		DocumentoAdjunto documentoAdjunto = obtenerDocumentoAdjunto(documentoAdjuntoRequest.getId());
		return obtenerArchivoBlob(documentoAdjunto);
	}

	public InputStreamResource obtenerArchivoBlob(DocumentoAdjunto documentoAdjunto) throws Exception {
		String rutaArchivo = crearRutaDocumentoAdjunto(documentoAdjunto);
		//Obtenemos el archivo
		return fileStorageService.loadFileAsResourceBlob(rutaArchivo, documentoAdjunto.getNombreArchivoServer());
	}

	public String crearNombreDescarga(Tramite tramite, String nombreArchivo){
		String numeroDocumento = tramite.getNumeroDocumento()==null?"NO-NUM-DOC":tramite.getNumeroDocumento();
		return "CUT_"+tramite.getNumeroTramite()+"_"+numeroDocumento+"_"+nombreArchivo;
	}

	public Map obtenerDocumentoAdjuntoDescarga(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
		DocumentoAdjunto documentoAdjunto = obtenerDocumentoAdjunto(documentoAdjuntoRequest.getId());
		Resource fileResource = obtenerArchivo(documentoAdjunto);
		Map<String, Object> param = new HashMap<>();
		param.put("file", fileResource);
		param.put("nombre", documentoAdjunto.getNombreArchivoDescarga());
		Tramite tramite = documentoAdjunto.getTramite();
		if(StringUtils.isBlank(documentoAdjunto.getNombreArchivoDescarga()) && tramite!=null){
			String nombreDescarga = crearNombreDescarga(tramite,fileResource.getFilename());
			param.put("nombre", nombreDescarga);
		}
		return param;
	}
	/*
	private String crearUsuarioCreacionNombre(DocumentoAdjuntoResponse documentoAdjuntoResponse){
		String usuarioCreacionNombre = documentoAdjuntoResponse.getUsuarioCreacionAdjuntoNombre();
		if(StringUtils.isBlank(usuarioCreacionNombre)){
			RestTemplate restTemplate = new RestTemplate();
			String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id/"+documentoAdjuntoResponse.getCreatedByUser();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", String.format("%s %s", "Bearer", securityHelper.getTokenCurrentSession()));
			HttpEntity entity = new HttpEntity<>(null, headers);
			ResponseEntity<CommonResponse> response = null;
			response = restTemplate.exchange(uri, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
			LinkedHashMap<Object, Object> usuario = (LinkedHashMap<Object, Object>) response.getBody().getData();
			usuarioCreacionNombre = usuario.get("nombreCompleto").toString();

			//Actualizamos el dato en la entidad DocumentoAdjunto
			DocumentoAdjunto documentoAdjunto = documentoAdjuntoMongoRepository.findById(documentoAdjuntoResponse.getId()).get();
			Usuario usuarioTmp = new Usuario();
			usuarioTmp.setId(documentoAdjuntoResponse.getCreatedByUser());
			documentoAdjunto.setUsuarioCreacionAdjunto(usuarioTmp);
			documentoAdjuntoMongoRepository.save(documentoAdjunto);

		}
		return usuarioCreacionNombre;
	}
	*/

	public void eliminarDocumentoAdjunto(String documentoAdjuntoId) throws Exception {

		String usuarioId = securityHelper.obtenerUserIdSession();
		DocumentoAdjunto documentoAdjunto = documentoAdjuntoJPARepository.findById(documentoAdjuntoId).get();
		String tramiteDerivacionId = documentoAdjunto.getTramiteDerivacionId();
		if(!documentoAdjunto.getCreatedByUser().equals(usuarioId)){
			List<Mensaje> mensajeList = new ArrayList<>();
			mensajeList.add(new Mensaje("E001","ERROR","El usuario que desa eliminar el archivo NO es el mismo que adjunto el archivo, verifique"));
			throw new ServiceException(mensajeList);
		}

		documentoAdjuntoJPARepository.deleteById(documentoAdjuntoId);

		actualizarAdjuntosPorTramiteDerivacion(tramiteDerivacionId);
	}

	public void actualizarAdjuntosPorTramiteDerivacion(String tramiteDerivacionId) throws Exception {
		if(!StringUtils.isBlank(tramiteDerivacionId)){
			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.obtenerTramiteDerivacionById(tramiteDerivacionId);
			DocumentoAdjuntoRequest documentoAdjuntoRequest = new DocumentoAdjuntoRequest();
			documentoAdjuntoRequest.setTramiteDerivacionId(tramiteDerivacionId);
			List<DocumentoAdjunto> listaDocumentoEscala = obtenerDocumentoAdjuntoParams(documentoAdjuntoRequest);
			tramiteDerivacion.setConAdjunto(true);
			if(CollectionUtils.isEmpty(listaDocumentoEscala)){
				tramiteDerivacion.setConAdjunto(false);
			}
			tramiteDerivacionService.save(tramiteDerivacion);
		}

	}

	public void actualizarDocumentoAdjunto(DocumentoAdjuntoBodyRequest documentoAdjuntoRequest) throws Exception {

		DocumentoAdjunto documentoAdjunto = documentoAdjuntoJPARepository.findById(documentoAdjuntoRequest.getId()).get();
		Tramite tramite = tramiteService.findById(documentoAdjuntoRequest.getTramiteId());
		String nombreArchivoDescarga = crearNombreDescarga(tramite,documentoAdjunto.getNombreArchivo());

		documentoAdjunto.setNombreArchivoDescarga(nombreArchivoDescarga);
		documentoAdjunto.setTramite(tramite);

		documentoAdjuntoJPARepository.save(documentoAdjunto);

	}

	public void migrarDocumentosAdjuntosFromFileDirectory(DocumentoAdjuntoCargaFromDirectoryBodyRequest documentoAdjuntoCargaFromDirectoryBodyRequest) throws Exception {

		String tramiteIdAnterior = documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdAnterior();

		//Creamos la ruta de donde obtener la lista de archivos
		DocumentoAdjunto documentoAdjunto = new DocumentoAdjunto();
		Tramite tramite = new Tramite();
		tramite.setId(documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdAnterior());
		tramite.setCreatedDate(new Date());
		documentoAdjunto.setTramite(tramite);
		String ruta = tramiteRuthFileStorage.setObject(documentoAdjunto).build();

		//Obtener la lista de documentos
		File file = new File(ruta);
		File[] listado = file.listFiles();
		if (listado == null || listado.length == 0) {
			throw new Exception("No hay archivos en la ruta "+ruta);
		}
		tramite = tramiteService.findById(documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdNuevo());
		Usuario usuarioCreacionAdjunto = new Usuario();
		usuarioCreacionAdjunto.setId(tramite.getCreatedByUser());
		for (int i=0; i< listado.length; i++) {
			File fileAdjunto = listado[i];
			if(fileAdjunto.isFile()){
				String nombreArchivoOriginal = fileStorageService.getFileName(fileAdjunto.getName());
				String nombreArchivo = "Adjunto " + (i+1) + "." + obtenerExtensionArchivo(nombreArchivoOriginal);
				String nombreArchivoServer = nombreArchivoOriginal;
				String nombreArchivoDescarga = crearNombreDescarga(tramite,nombreArchivo);

				Path path = fileAdjunto.toPath();
				String mimeType = Files.probeContentType(path);

				documentoAdjunto = new DocumentoAdjunto();
				documentoAdjunto.setNombreArchivo(nombreArchivo);
				documentoAdjunto.setNombreArchivoDescarga(nombreArchivoDescarga);
				documentoAdjunto.setNombreArchivoServer(nombreArchivoServer);
				documentoAdjunto.setEstado("A");
				documentoAdjunto.setExtension(mimeType);
				documentoAdjunto.setTramite(tramite);
				documentoAdjunto.setSize(Files.size(path));
				documentoAdjunto.setUsuarioCreacionAdjunto(usuarioCreacionAdjunto);
				documentoAdjuntoJPARepository.save(documentoAdjunto);
				log.info("Se registra el archivo "+nombreArchivoServer+", " + nombreArchivo + " para tramite "+tramite.getNumeroTramite());
			}
		}
	}

	public void renombrarDirectorioCargaMigracion(DocumentoAdjuntoCargaFromDirectoryBodyRequest documentoAdjuntoCargaFromDirectoryBodyRequest) throws Exception {

		log.info("Renombrando directorio: "+new ObjectMapper().writeValueAsString(documentoAdjuntoCargaFromDirectoryBodyRequest));
		String tramiteIdAnterior = documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdAnterior();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

		//Creamos la ruta de donde obtener la lista de archivos
		//Tramite tramite = tramiteService.findById(documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdAnterior());
		Tramite tramite = new Tramite();
		tramite.setCreatedDate(new Date()); //TODO como se sabe que todos los tramites son de 2023 se coloca new Date, si se migrara de otros años se tiene que hacer una consulta a bd del tramite
		tramite.setId(documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdAnterior());
		String rutaDirectorio = fileTxProperties.getBaseUploadDir()
				.concat(File.separator)
				.concat(String.valueOf(sdf.format(tramite.getCreatedDate())))
				.concat(File.separator)
				.concat(String.valueOf(tramite.getId()));
		File directorioOrigen = new File(rutaDirectorio);

		if(directorioOrigen.exists() && directorioOrigen.isDirectory()){
			tramite = new Tramite();
			tramite.setCreatedDate(new Date());
			tramite.setId(documentoAdjuntoCargaFromDirectoryBodyRequest.getTramiteIdNuevo());
			rutaDirectorio = fileTxProperties.getBaseUploadDir()
					.concat(File.separator)
					.concat(String.valueOf(sdf.format(tramite.getCreatedDate())))
					.concat(File.separator)
					.concat(String.valueOf(tramite.getId()));
			File directorioFin = new File(rutaDirectorio);

			if(!directorioFin.exists()){
				//Obtener la lista de documentos
				boolean renombramientoCorrecto = directorioOrigen.renameTo(directorioFin);
				log.info("Se cambio nombre de directorio de: "+directorioOrigen.getAbsolutePath()+" a " +directorioFin.getAbsolutePath());
			}else{
				throw new Exception("YA EXISTE RUTA: "+directorioFin.getAbsolutePath());
			}
		}else{
			throw new Exception("NO EXISTE RUTA: "+directorioOrigen.getAbsolutePath());
		}
	}


}
