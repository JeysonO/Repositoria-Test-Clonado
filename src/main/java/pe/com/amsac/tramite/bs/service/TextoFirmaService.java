package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.AlertaBodyRequest;
import pe.com.amsac.tramite.bs.domain.Alerta;
import pe.com.amsac.tramite.bs.domain.TextoFirma;
import pe.com.amsac.tramite.bs.repository.AlertaMongoRepository;
import pe.com.amsac.tramite.bs.repository.TextoFirmaMongoRepository;

import java.util.List;

@Service
public class TextoFirmaService {

	@Autowired
	private TextoFirmaMongoRepository textoFirmaMongoRepository;

	@Autowired
	private Mapper mapper;

	public List<TextoFirma> obtenerTextosFirma() throws Exception {

		return textoFirmaMongoRepository.obtenerTextoFirmaHabilitado();

	}
		
	
}
