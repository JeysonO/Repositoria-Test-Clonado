package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.file.bean.TramiteRuthFileStorage;
import pe.com.amsac.tramite.api.file.bean.UploadFileResponse;
import pe.com.amsac.tramite.api.request.body.bean.DocumentoAdjuntoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.DocumentoAdjuntoResponse;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.util.FileUtils;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;

@Service
public class DocumentoAdjuntoService {

	@Autowired
	private TramiteMongoRepository tramiteMongoRepository;

	@Autowired
	private DocumentoAdjuntoMongoRepository documentoAdjuntoMongoRepository;

	@Autowired
	private TramiteRuthFileStorage tramiteRuthFileStorage;

	@Autowired
	private Mapper mapper;

	@Autowired
	private Environment env;

	@Autowired
	private FileStorageService fileStorageService;

	public DocumentoAdjuntoResponse registrarDocumentoAdjunto(DocumentoAdjuntoBodyRequest documentoAdjuntoRequest) throws Exception {
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
}
