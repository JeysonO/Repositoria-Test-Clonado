package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.file.bean.TramitePathFileStorage;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteBodyRequest;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;
import pe.com.amsac.tramite.bs.domain.FirmaDocumento;
import pe.com.amsac.tramite.bs.domain.Usuario;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoMongoRepository;
import pe.com.amsac.tramite.bs.repository.FirmaDocumentoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaMongoRepository;
import pe.com.amsac.tramite.bs.util.TipoDocumentoFirmaConstant;

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioFirmaService {

	@Autowired
	private UsuarioFirmaMongoRepository usuarioFirmaMongoRepository;

	public List<UsuarioFirma> obtenerUsuarioFirmaByUsuario(String usuario) throws Exception {

		return usuarioFirmaMongoRepository.findByUsuario(usuario);

	}


}
