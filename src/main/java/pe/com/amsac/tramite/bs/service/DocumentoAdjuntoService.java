package pe.com.amsac.tramite.bs.service;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.file.bean.TramitePathFileStorage;
import pe.com.amsac.tramite.api.file.bean.UploadFileResponse;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.DocumentoAdjuntoResponse;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.util.FileUtils;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class DocumentoAdjuntoService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private DocumentoAdjuntoMongoRepository documentoAdjuntoMongoRepository;

	@Autowired
	private TramitePathFileStorage tramiteRuthFileStorage;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FileStorageService fileStorageService;

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
		}
		return documentoAdjuntoResponse;
	}

	public List<DocumentoAdjunto> obtenerDocumentoAdjuntoParams(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
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
		return documentoAdjuntoList;
	}

	public DocumentoAdjuntoResponse registrarDocumentoAdjunto(DocumentoAdjuntoBodyRequest documentoAdjuntoRequest) throws Exception {

		//Validar suma de adjuntos
		List<Mensaje> mensajesError = validarAdjuntos(documentoAdjuntoRequest);
		if(!CollectionUtils.isEmpty(mensajesError)){
			throw new ServiceException(mensajesError);
		}

		String fileName = fileStorageService.getFileName(documentoAdjuntoRequest.getFile().getOriginalFilename());

		DocumentoAdjunto documentoAdjunto = mapper.map(documentoAdjuntoRequest, DocumentoAdjunto.class);
		documentoAdjunto.setEstado("A");
		documentoAdjunto.setNombreArchivo(fileName);
		documentoAdjunto.setExtension(documentoAdjuntoRequest.getFile().getContentType());

		//Primero registramos el archivo en disco

		Tramite tramiteTmp = null;

		if(documentoAdjunto.getTramite()!=null
				&& documentoAdjunto.getTramite().getId()!=null) {
			// Se crea el archivo para guardarlo
			tramiteTmp = tramiteMongoRepository.findById(documentoAdjunto.getTramite().getId()).get();

			// Seteamos la ruta
			fileStorageService.setFileStorageLocation(
					construirRutaArchivo(tramiteTmp, documentoAdjunto.getTipoAdjunto()));
		}else {
			// Seteamos la ruta
			fileStorageService.setFileStorageLocation(
					construirRutaArchivo(documentoAdjunto));
		}

		// Registramos el archivo adjunto
		// Enviamos reemplazar igual a N siempre, para que no chanque los archivos.
		fileName = fileStorageService.storeFile(documentoAdjuntoRequest.getFile().getInputStream(), fileName,false);
		documentoAdjunto.setNombreArchivo(fileName);

		//Guardamos en BD
		documentoAdjuntoMongoRepository.save(documentoAdjunto);

		DocumentoAdjuntoResponse response = mapper.map(documentoAdjunto, DocumentoAdjuntoResponse.class);
		response.setUploadFileResponse(createUploadFileResponse(documentoAdjuntoRequest.getFile(), documentoAdjunto));

		return response;
	}

	private String construirRutaArchivo(Tramite tramite, String tipoAdjunto) {
		DocumentoAdjunto documentoAdjunto = new DocumentoAdjunto();
		documentoAdjunto.setTramite(tramite);
		documentoAdjunto.setTipoAdjunto(tipoAdjunto);
		return construirRutaArchivo(documentoAdjunto);
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

		UploadFileResponse uploadFileResponse = new UploadFileResponse(file.getOriginalFilename(), fileDownloadUri,
				file.getContentType(), FileUtils.getFileSize(file.getSize()));
		return uploadFileResponse;
	}

	public Resource obtenerDocumentoAdjunto(DocumentoAdjuntoRequest documentoAdjuntoRequest) throws Exception {
		DocumentoAdjunto documentoAdjunto = obtenerDocumentoAdjunto(documentoAdjuntoRequest.getId());
		return obtenerArchivo(documentoAdjunto);
	}

	public Resource obtenerArchivo(DocumentoAdjunto documentoAdjunto) throws Exception {
			fileStorageService.setFileStorageLocation(tramiteRuthFileStorage.setObject(documentoAdjunto).build());

		return fileStorageService.loadFileAsResource(documentoAdjunto.getNombreArchivo());
	}

	public DocumentoAdjunto obtenerDocumentoAdjunto(String documentoAdjuntoId) throws Exception {
		return documentoAdjuntoMongoRepository.findById(documentoAdjuntoId).get();
	}

	private UploadFileResponse createUploadFileResponse(Resource file, DocumentoAdjunto documentoAdjunto)
			throws IOException {
		String fileDownloadUri = "/documentos-adjuntos"
				.concat("/downloadFile/" + documentoAdjunto.getId());
		UploadFileResponse uploadFileResponse = new UploadFileResponse(file.getFilename(), fileDownloadUri,
				documentoAdjunto.getExtension(), FileUtils.getFileSize(file.contentLength()));
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
}
