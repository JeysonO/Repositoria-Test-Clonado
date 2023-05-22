package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ResourceNotFoundException;
import pe.com.amsac.tramite.api.file.bean.FileStorageException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaLogoBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaLogoMongoRepository;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UsuarioFirmaLogoService {

	@Autowired
	private UsuarioFirmaLogoMongoRepository usuarioFirmaLogoMongoRepository;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private UsuarioFirmaService usuarioFirmaService;

	@Autowired
	private Environment environment;

	@Autowired
	private FileStorageService fileStorageService;

	public List<UsuarioFirmaLogo> obtenerUsuarioFirmaLogoByUsuarioFirmaId(String usuarioFirmaId) throws Exception {

		return usuarioFirmaLogoMongoRepository.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaId);

	}

	public UsuarioFirmaLogo registrarUsuarioFirmaLogo(String usuarioFirmaId, String descripcion, MultipartFile file, boolean esFavorito) throws Exception {

		UsuarioFirma usuarioFirma = new UsuarioFirma();
		usuarioFirma.setId(usuarioFirmaId);

		UsuarioFirmaLogo usuarioFirmaLogo = new UsuarioFirmaLogo();
		usuarioFirmaLogo.setSize(obtenerDimensionImagen(file.getResource()));
		usuarioFirmaLogo.setEstado("A");
		usuarioFirmaLogo.setUsuarioFirma(usuarioFirma);
		usuarioFirmaLogo.setDescripcion(descripcion);
		usuarioFirmaLogo.setEsFavorito(esFavorito);

		//Registramos el archivo en disco
		String fileName = file.getOriginalFilename();
		String fileNameInServer = UUID.randomUUID().toString() + "." + obtenerExtensionArchivo(fileName);
		//String rutaArchivo = environment.getProperty("app.ruta.logo-firma") + usuarioFirmaId + File.separator + fileNameInServer;
		String rutaArchivo = environment.getProperty("app.ruta.logo-firma") + usuarioFirmaId;
		storeFile(file, rutaArchivo, fileNameInServer);

		usuarioFirmaLogo.setNombreArchivo(fileNameInServer);
		usuarioFirmaLogoMongoRepository.save(usuarioFirmaLogo);

		return usuarioFirmaLogo;
	}

	public UsuarioFirmaLogo obtenerUsuarioFirmaLogoById(String usuarioFirmaLogoId) throws Exception {

		return usuarioFirmaLogoMongoRepository.findById(usuarioFirmaLogoId).get();

	}

	public void eliminarUsuarioFirmaLogoById(String usuarioFirmaLogoId){
		usuarioFirmaLogoMongoRepository.deleteById(usuarioFirmaLogoId);
	}

	private String obtenerDimensionImagen(Resource resource) throws IOException {
		String obtenerDimensionImagen = null;
		try(ImageInputStream in = ImageIO.createImageInputStream(resource.getInputStream())){// resource.getInputStream()){//ImageIO.createImageInputStream(resource)){
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					obtenerDimensionImagen = String.valueOf(reader.getWidth(0))+","+String.valueOf(reader.getHeight(0));
					//return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
		}
		return obtenerDimensionImagen;
	}

	public void storeFile(MultipartFile file, String rutaArchivo, String nombreArchivo) {

		try {
			createDirectory(rutaArchivo);
			String rutaCompletaArchivo = rutaArchivo + File.separator + nombreArchivo;
			System.out.println("Ruta completa archivo guardar: " + rutaCompletaArchivo);
			File targetFile = new File(rutaCompletaArchivo);
			this.copyInputStreamToFile(file.getInputStream(), targetFile);

		} catch (IOException var10) {
			throw new FileStorageException("No se puede registrar el archivo " + rutaArchivo + ". Vuelva a intentar!");
		}
	}

	public Resource loadFileAsResource(UsuarioFirmaLogo usuarioFirmaLogo) throws Exception {
		String fulFilePath = null;
		try {

			String fileNameInServer = usuarioFirmaLogo.getNombreArchivo();
			fulFilePath = environment.getProperty("app.ruta.logo-firma") + usuarioFirmaLogo.getUsuarioFirma().getId() + File.separator + fileNameInServer;
			log.info("fulFilePath: " + fulFilePath);
			Resource resource = new UrlResource("file:" + fulFilePath);
			if (resource.exists()) {
				return resource;
			} else {
				throw new ResourceNotFoundException("Archivo no encontrato " + fulFilePath);
			}
		} catch (MalformedURLException var4) {
			throw new ResourceNotFoundException("Archivo no encontrado " + fulFilePath);
		}
	}

	private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
		System.out.println("Convirtiendo InputStream To File");
		FileOutputStream outputStream = new FileOutputStream(file, false);

		try {
			byte[] bytes = new byte[8192];

			int read;
			while((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (Throwable var7) {
			try {
				outputStream.close();
			} catch (Throwable var6) {
				var7.addSuppressed(var6);
			}

			throw var7;
		}

		outputStream.close();
	}

	private String obtenerExtensionArchivo(String fileName){
		String[] arregloCadena = fileName.split("\\.");
		return arregloCadena[arregloCadena.length - 1];
	}

	public boolean createDirectory(String ruta) {
		Path rutaTemporal = Paths.get(ruta).toAbsolutePath().normalize();

		try {
			Files.createDirectories(rutaTemporal);
			return true;
		} catch (Exception var4) {
			throw new FileStorageException("No se puede crear la ruta donde el archivo sera creado.");
		}
	}

	public List<UsuarioFirmaLogo> obtenerUsuarioFirmaLogoByUsuario() throws Exception {
		String usuarioId = securityHelper.obtenerUserIdSession();

		UsuarioFirma usuarioFirma =  usuarioFirmaService.obtenerUsuarioFirmaByUsuarioId(usuarioId);

		return usuarioFirmaLogoMongoRepository.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirma.getId());

	}

	public InputStreamResource obtenerUsuarioImagenLogoBlob(String usuarioFirmaLogoId) throws Exception {
		UsuarioFirmaLogo usuarioFirmaLogo = usuarioFirmaLogoMongoRepository.findById(usuarioFirmaLogoId).get();
		String usuarioFirmaId = usuarioFirmaLogo.getUsuarioFirma().getId();

		String rutaArchivo = environment.getProperty("app.ruta.logo-firma") + usuarioFirmaId;

		//Obtenemos el archivo
		return fileStorageService.loadFileAsResourceBlob(rutaArchivo, usuarioFirmaLogo.getNombreArchivo());
	}

	public UsuarioFirmaLogo actualizarUsuarioFirmaLogo(UsuarioFirmaLogoBodyRequest usuarioFirmaLogoBodyRequest) throws Exception {

		UsuarioFirmaLogo usuarioFirmaLogo = usuarioFirmaLogoMongoRepository.findById(usuarioFirmaLogoBodyRequest.getId()).get();
		usuarioFirmaLogo.setEsFavorito(usuarioFirmaLogoBodyRequest.isEsFavorito());

		usuarioFirmaLogoMongoRepository.save(usuarioFirmaLogo);

		return usuarioFirmaLogo;
	}

}
