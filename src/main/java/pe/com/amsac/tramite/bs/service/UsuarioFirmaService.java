package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.UsuarioFirmaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.UsuarioFirmaMongoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioFirmaService {

	@Autowired
	private Mapper mapper;

	@Autowired
	private UsuarioFirmaMongoRepository usuarioFirmaMongoRepository;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private UsuarioFirmaLogoService usuarioFirmaLogoService;

	public UsuarioFirma obtenerUsuarioFirmaByUsuarioId(String usuarioId) throws Exception {

		List<UsuarioFirma> usuarioFirmaList = usuarioFirmaMongoRepository.obtenerUsuarioFirmaByUsuarioId(usuarioId);

		if(CollectionUtils.isEmpty(usuarioFirmaList)){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","Usuario no tiene registro habilitado para firmar"));
			throw new ServiceException(mensajes);
		}

		return usuarioFirmaList.get(0);

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

	public UsuarioFirma obtenerUsuarioFirmaByUsuario() throws Exception {

		String usuarioId = securityHelper.obtenerUserIdSession();

		List<UsuarioFirma> usuarioFirmaList = usuarioFirmaMongoRepository.obtenerUsuarioFirmaByUsuarioId(usuarioId);

		if(CollectionUtils.isEmpty(usuarioFirmaList)){
			List<Mensaje> mensajes = new ArrayList<>();
			mensajes.add(new Mensaje("E001","ERROR","Usuario no tiene registro habilitado para firmar"));
			throw new ServiceException(mensajes);
		}

		return usuarioFirmaList.get(0);

	}


}
