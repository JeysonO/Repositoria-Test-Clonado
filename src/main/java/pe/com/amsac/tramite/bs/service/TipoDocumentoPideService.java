package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.TipoDocumentoPide;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoPideJPARepository;

import java.util.List;

@Service
@Slf4j
public class TipoDocumentoPideService {

	@Autowired
	private TipoDocumentoPideJPARepository tipoDocumentoPideJPARepository;

	@Autowired
	private Mapper mapper;


	public List<TipoDocumentoPide> obtenerTipoDocumentoPideByEstado(String estado) throws Exception{

		return tipoDocumentoPideJPARepository.findByEstado(estado);
	}
		
	
}
