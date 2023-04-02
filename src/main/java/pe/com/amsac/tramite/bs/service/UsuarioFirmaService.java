package pe.com.amsac.tramite.bs.service;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.file.bean.TramitePathFileStorage;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.DocumentoAdjuntoMongoRepository;
import pe.com.amsac.tramite.bs.repository.FirmaDocumentoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaMongoRepository;
import pe.com.amsac.tramite.bs.util.TipoDocumentoFirmaConstant;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioFirmaService {

	@Autowired
	private Mapper mapper;

	@Autowired
	private UsuarioFirmaMongoRepository usuarioFirmaMongoRepository;

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	public UsuarioFirma obtenerUsuarioFirmaByUsuarioId(String usuarioId) throws Exception {

		return usuarioFirmaMongoRepository.obtenerUsuarioFirmaByUsuarioId(usuarioId).get(0);

	}

	public UsuarioFirma registrarUsuarioFirma(UsuarioFirmaBodyRequest usuarioFirmaBodyrequest) throws Exception {

		//Se hacen validaciones de los datos del usuario a registrar
		List<Mensaje> mensajesError = validarDatosUsuarioFirma(usuarioFirmaBodyrequest);
		if(!org.apache.commons.collections.CollectionUtils.isEmpty(mensajesError)){
			throw new ServiceException(mensajesError);
		}

		UsuarioFirma usuarioFirma = mapper.map(usuarioFirmaBodyrequest,UsuarioFirma.class);
		usuarioFirmaMongoRepository.save(usuarioFirma);
		return usuarioFirma;

	}

	public List<UsuarioFirma> obtenerUsuarioFirma() throws Exception {

		return usuarioFirmaMongoRepository.findByEstado("A");

	}

	public void eliminarUsuarioFirmaById(String usuarioFirmaId) throws Exception {

		//Obtenemos los logos para este usuario firma, con la finalidad de eliminar cada registro
		List<UsuarioFirmaLogo> usuarioFirmaLogoList = usuarioFirmaLogoService.obtenerUsuarioFirmaLogoByUsuarioFirmaId(usuarioFirmaId);
		if(!CollectionUtils.isEmpty(usuarioFirmaLogoList)){
			for(UsuarioFirmaLogo usuarioFirmaLogo:usuarioFirmaLogoList){
				usuarioFirmaLogoService.eliminarUsuarioFirmaLogoById(usuarioFirmaLogo.getId());
			}
		}

		usuarioFirmaMongoRepository.deleteById(usuarioFirmaId);

	}

	public List<Mensaje> validarDatosUsuarioFirma(UsuarioFirmaBodyRequest usuarioFirmaBodyrequest){

		List<Mensaje> mensajes = new ArrayList<>();
		if(!CollectionUtils.isEmpty(usuarioFirmaMongoRepository.obtenerUsuarioFirmaByUsuarioId(usuarioFirmaBodyrequest.getUsuarioId()))){
			mensajes.add(new Mensaje("E001","ERROR","Ya existe otro registro de firma para el usuario ingresado"));
		}

		return mensajes;

	}


}
