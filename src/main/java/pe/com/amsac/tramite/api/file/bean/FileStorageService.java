package pe.com.amsac.tramite.api.file.bean;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Component
@RequestScope
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private Path fileStorageLocation;
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public FileStorageService() {
    }

    public void setFileStorageLocation(String fileStorageLocatino) {
        this.fileStorageLocation = Paths.get(fileStorageLocatino).toAbsolutePath().normalize();
    }

    public String getFileName(String fileNameOriginal) {
        String fileName = StringUtils.cleanPath(fileNameOriginal);
        if (fileName.contains("..")) {
            throw new FileStorageException("Nombre de archivo no tiene formato valido" + fileName);
        } else {
            return fileName;
        }
    }

    public String getFileExtention(String fileNameOriginal) {
        return StringUtils.getFilenameExtension(fileNameOriginal).toLowerCase();
    }

    public String storeFile(MultipartFile file, boolean reemplazar) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Nombre de archivo no tiene formato valido" + fileName);
            } else {
                System.out.println("Ruta completa archivo guardar: " + this.fileStorageLocation.toString().concat(File.separator).concat(fileName));
                String fullFilePath = this.fileStorageLocation.toString().concat(File.separator).concat(fileName);
                File targetFile = new File(fullFilePath);
                if (reemplazar) {
                    this.copyInputStreamToFile(file.getInputStream(), targetFile);
                } else {
                    boolean existe = targetFile.exists();
                    if (existe) {
                        String[] arregloCadena = fileName.split("\\.");
                        String extension = arregloCadena[arregloCadena.length - 1];
                        String soloNombreArchivo = this.obtenerNombreArchivo(arregloCadena);
                        soloNombreArchivo = soloNombreArchivo + "-[" + DateUtils.formatDate(new Date(), "HHmmssSSS") + "]";
                        fileName = soloNombreArchivo + "." + extension;
                        fullFilePath = this.fileStorageLocation.toString().concat(File.separator).concat(fileName);
                        targetFile = new File(fullFilePath);
                    }

                    this.copyInputStreamToFile(file.getInputStream(), targetFile);
                }

                return fileName;
            }
        } catch (IOException var10) {
            throw new FileStorageException("No se puede registrar el archivo " + fileName + ". Vuelva a intentar!");
        }
    }

    public String storeFile(InputStream file, String nombreOriginal, boolean reemplazar) {
        String fileName = StringUtils.cleanPath(nombreOriginal);

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Nombre de archivo no tiene formato valido" + fileName);
            } else {
                System.out.println("Ruta completa archivo guardar: " + this.fileStorageLocation.toString().concat(File.separator).concat(fileName));
                String fullFilePath = this.fileStorageLocation.toString().concat(File.separator).concat(fileName);
                File targetFile = new File(fullFilePath);
                if (reemplazar) {
                    this.copyInputStreamToFile(file, targetFile);
                } else {
                    boolean existe = targetFile.exists();
                    if (existe) {
                        String[] arregloCadena = fileName.split("\\.");
                        String extension = arregloCadena[arregloCadena.length - 1];
                        String soloNombreArchivo = this.obtenerNombreArchivo(arregloCadena);
                        soloNombreArchivo = soloNombreArchivo + "-[" + DateUtils.formatDate(new Date(), "HHmmssSSS") + "]";
                        fileName = soloNombreArchivo + "." + extension;
                        fullFilePath = this.fileStorageLocation.toString().concat(File.separator).concat(fileName);
                        targetFile = new File(fullFilePath);
                    }

                    this.copyInputStreamToFile(file, targetFile);
                }

                return fileName;
            }
        } catch (IOException var11) {
            throw new FileStorageException("No se puede registrar el archivo " + fileName + ". Vuelva a intentar!");
        }
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

    public Resource loadFileAsResource(String rutaArchivo, String fileName) throws Exception {
        try {
            log.info("Dentro de loadFileAsResource");
            String fulFilePath = rutaArchivo.concat(File.separator).concat(fileName);
            //log.info("Ruta completa archivo a cargar: " + rutaArchivo.concat(File.separator).concat(fileName));
            //String fulFilePath = rutaArchivo.concat(File.separator).concat(fileName);
            log.info("fulFilePath: " + fulFilePath);
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

    public Resource loadFileAsResource(String fileName) throws Exception {
        try {
            log.info("Dentro de loadFileAsResource");
            log.info("Ruta completa archivo a cargar: " + this.fileStorageLocation.toString().concat(File.separator).concat(fileName));
            String fulFilePath = this.fileStorageLocation.toString().concat(File.separator).concat(fileName);
            log.info("fulFilePath: " + fulFilePath);
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

    public boolean createDirectory(String ruta) {
        Path rutaTemporal = Paths.get(ruta).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rutaTemporal);
            return true;
        } catch (Exception var4) {
            throw new FileStorageException("No se puede crear la ruta donde el archivo sera creado.");
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

    public String storeFile(String ruta, InputStream file, String nombreOriginal, boolean reemplazar) {
        String fileName = StringUtils.cleanPath(nombreOriginal);

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Nombre de archivo no tiene formato valido" + fileName);
            } else {
                System.out.println("Ruta completa archivo guardar: " + ruta.concat(File.separator).concat(fileName));
                String fullFilePath = ruta.concat(File.separator).concat(fileName);
                File targetFile = new File(fullFilePath);
                if (reemplazar) {
                    this.copyInputStreamToFile(file, targetFile);
                } else {
                    boolean existe = targetFile.exists();
                    if (existe) {
                        String[] arregloCadena = fileName.split("\\.");
                        String extension = arregloCadena[arregloCadena.length - 1];
                        String soloNombreArchivo = this.obtenerNombreArchivo(arregloCadena);
                        soloNombreArchivo = soloNombreArchivo + "-[" + DateUtils.formatDate(new Date(), "HHmmssSSS") + "]";
                        fileName = soloNombreArchivo + "." + extension;
                        fullFilePath = ruta.concat(File.separator).concat(fileName);
                        targetFile = new File(fullFilePath);
                    }

                    this.copyInputStreamToFile(file, targetFile);
                }

                return fileName;
            }
        } catch (IOException var11) {
            throw new FileStorageException("No se puede registrar el archivo " + fileName + ". Vuelva a intentar!");
        }
    }

    public InputStreamResource loadFileAsResourceBlob(String rutaArchivo, String fileName) throws Exception {
        try {

            String fulFilePath = rutaArchivo.concat(File.separator).concat(fileName);
            //String fulFilePath = "/Users/ealvino/Downloads/28e1c3f5-0dd2-4a1c-af36-0b7b1378facb.pdf";
            File file = new File(fulFilePath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Archivo no encontrato " + fileName);
            }
        } catch (Exception var4) {
            throw new ResourceNotFoundException("Archivo no encontrado " + fileName);
        }
    }


}
