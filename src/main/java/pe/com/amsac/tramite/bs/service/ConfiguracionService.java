package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.Configuracion;
import pe.com.amsac.tramite.bs.repository.ConfiguracionJPARepository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ConfiguracionService {

	@Autowired
	private ConfiguracionJPARepository configuracionJPARepository;

	public boolean estamosDentroHorarioDeAtencion() throws Exception {
		//Se usa fecha en numero por practicidad
		Configuracion configuracion = configuracionJPARepository.findAll().get(0);
		String horaMaximaRecepcionTramite = configuracion.getHoraMaximaRecepcionTramite();

		String fechaHoraMaximaAtencion = new SimpleDateFormat("dd/MM/yyyy").format(new Date()).concat(" ").concat(horaMaximaRecepcionTramite);
		Date diaFechaHoraMaximaAtencion = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHoraMaximaAtencion);

		if(diaFechaHoraMaximaAtencion.after(new Date()))
			return true;

		return false;
	}

	public Configuracion obtenerConfiguracion() throws Exception {
		return configuracionJPARepository.findAll().get(0);
	}

}
