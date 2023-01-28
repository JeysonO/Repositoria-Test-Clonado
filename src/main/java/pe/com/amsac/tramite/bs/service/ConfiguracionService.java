package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.Calendario;
import pe.com.amsac.tramite.bs.domain.Configuracion;
import pe.com.amsac.tramite.bs.repository.CalendarioMongoRepository;
import pe.com.amsac.tramite.bs.repository.ConfiguracionMongoRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ConfiguracionService {

	@Autowired
	private ConfiguracionMongoRepository configuracionMongoRepository;

	public boolean estamosDentroHorarioDeAtencion() throws Exception {
		//Se usa fecha en numero por practicidad
		Configuracion configuracion = configuracionMongoRepository.findAll().get(0);
		String horaMaximaRecepcionTramite = configuracion.getHoraMaximaRecepcionTramite();

		String fechaHoraMaximaAtencion = new SimpleDateFormat("dd/MM/yyyy").format(new Date()).concat(" ").concat(horaMaximaRecepcionTramite);
		Date diaFechaHoraMaximaAtencion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHoraMaximaAtencion);

		if(diaFechaHoraMaximaAtencion.after(new Date()))
			return true;

		return false;
	}

	public Configuracion obtenerConfiguracion() throws Exception {
		return configuracionMongoRepository.findAll().get(0);
	}

}
