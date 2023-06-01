package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ResourceNotFoundException;
import pe.com.amsac.tramite.api.file.bean.FileStorageException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.body.bean.ConfiguracionUsuarioBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaLogoBodyRequest;
import pe.com.amsac.tramite.bs.domain.ConfiguracionUsuario;
import pe.com.amsac.tramite.bs.domain.UsuarioFirma;
import pe.com.amsac.tramite.bs.domain.UsuarioFirmaLogo;
import pe.com.amsac.tramite.bs.repository.ConfiguracionUsuarioMongoRepository;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ConfiguracionUsuarioService {

	@Autowired
	private ConfiguracionUsuarioMongoRepository configuracionUsuarioMongoRepository;

	@Autowired
	private Mapper mapper;

	public ConfiguracionUsuario registrarConfiguracionUsuario(ConfiguracionUsuarioBodyRequest configuracionUsuarioBodyRequest ) throws Exception {

		ConfiguracionUsuario usuarioFirma = mapper.map(configuracionUsuarioBodyRequest,ConfiguracionUsuario.class);

		configuracionUsuarioMongoRepository.save(usuarioFirma);

		return usuarioFirma;
	}

	public ConfiguracionUsuario obtenerConfiguracionUsuario(String usuarioId) throws Exception {

		List<ConfiguracionUsuario> configuracionUsuarioList = configuracionUsuarioMongoRepository.obtenerConfiguracionUsuarioByUsuarioId(usuarioId);

		return CollectionUtils.isEmpty(configuracionUsuarioList)?null:configuracionUsuarioList.get(0);

	}

}
