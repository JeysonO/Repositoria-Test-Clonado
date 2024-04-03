package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.TextoFirma;
import pe.com.amsac.tramite.bs.repository.TextoFirmaJPARepository;

import java.util.List;

@Service
public class TextoFirmaService {

	@Autowired
	private TextoFirmaJPARepository textoFirmaJPARepository;

	@Autowired
	private Mapper mapper;

	public List<TextoFirma> obtenerTextosFirma() throws Exception {

		return textoFirmaJPARepository.obtenerTextoFirmaHabilitado();

	}
		
	
}
