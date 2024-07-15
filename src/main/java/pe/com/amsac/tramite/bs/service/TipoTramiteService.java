package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.bean.TipoDocumentoRequest;
import pe.com.amsac.tramite.api.request.body.bean.TipoDocumentoBodyRequest;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoTramite;
import pe.com.amsac.tramite.bs.domain.TipoTramite;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoJPARepository;
import pe.com.amsac.tramite.bs.repository.TipoTramiteJPARepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TipoTramiteService {

	@Autowired
	private TipoTramiteJPARepository tipoTramiteJPARepository;

	public List<TipoTramite> obtenerTiposTramiteActivos() throws Exception {

		return tipoTramiteJPARepository.findByEstado("A");

	}

	
}
