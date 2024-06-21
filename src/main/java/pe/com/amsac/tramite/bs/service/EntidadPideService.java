package pe.com.amsac.tramite.bs.service;

import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.EntidadPide;
import pe.com.amsac.tramite.bs.repository.EntidadPideJPARepository;

import java.util.List;

@Service
@Slf4j
public class EntidadPideService {

	@Autowired
	private EntidadPideJPARepository entidadPideJPARepository;

	@Autowired
	private Mapper mapper;


	public List<EntidadPide> obtenerEntidadPideByEstado(String estado) throws Exception{

		return entidadPideJPARepository.findByEstado(estado);
	}
		
	
}
