package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pe.com.amsac.tramite.api.request.body.bean.ConfiguracionUsuarioBodyRequest;
import pe.com.amsac.tramite.bs.domain.ConfiguracionUsuario;
import pe.com.amsac.tramite.bs.repository.ConfiguracionUsuarioJPARepository;

import java.util.List;

@Slf4j
@Service
public class ConfiguracionUsuarioService {

	@Autowired
	private ConfiguracionUsuarioJPARepository configuracionUsuarioJPARepository;

	@Autowired
	private Mapper mapper;

	public ConfiguracionUsuario registrarConfiguracionUsuario(ConfiguracionUsuarioBodyRequest configuracionUsuarioBodyRequest ) throws Exception {

		ConfiguracionUsuario usuarioFirma = mapper.map(configuracionUsuarioBodyRequest,ConfiguracionUsuario.class);

		configuracionUsuarioJPARepository.save(usuarioFirma);

		return usuarioFirma;
	}

	public ConfiguracionUsuario obtenerConfiguracionUsuario(String usuarioId) throws Exception {

		List<ConfiguracionUsuario> configuracionUsuarioList = configuracionUsuarioJPARepository.obtenerConfiguracionUsuarioByUsuarioId(usuarioId);

		return CollectionUtils.isEmpty(configuracionUsuarioList)?null:configuracionUsuarioList.get(0);

	}

}
