package pe.com.amsac.tramite.bs.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.api.request.body.bean.PersonaBodyRequest;
import pe.com.amsac.tramite.bs.domain.Persona;
import pe.com.amsac.tramite.bs.repository.PersonaJPARepository;

//@Service
public class PersonaService {

	//@Autowired
	//private PersonaJPARepository personaJPARepository;

	//@Autowired
	private Mapper mapper;

	public Persona registrarPersona(PersonaBodyRequest personaBodyRequest) throws Exception {
		return null;
		/*
		Persona persona = mapper.map(personaBodyRequest,Persona.class);
		persona.setEstado("A");
		personaJPARepository.save(persona);
		return persona;
		*/

	}
		
	
}
