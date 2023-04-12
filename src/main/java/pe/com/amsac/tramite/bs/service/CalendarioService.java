package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.CalendarioMongoRepository;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CalendarioService {

	@Autowired
	private CalendarioMongoRepository calendarioMongoRepository;

	public boolean esFeriado(Integer fechaEnNumero) throws Exception {
		//Se usa fecha en numero por practicidad
		List<Calendario> listaFechaFeriado = calendarioMongoRepository.findByFechaNumber(fechaEnNumero);
		Predicate<Calendario> predicate = x -> x.getFeriado().equals("S");

		return listaFechaFeriado.stream().filter(predicate).collect(Collectors.toList()).size()>0?true:false ;

	}

	public Date obtenerSiguienteDiaHabil() throws Exception {
		//Fecha de hoy
		Date fecha = new Date();
		Calendar calendar = Calendar.getInstance();
		boolean seEncontroFecha = false;
		int cantidadMaximaDeBusqueda = 10;
		int numeroBusqueda = 0;
		while(!seEncontroFecha && numeroBusqueda<=cantidadMaximaDeBusqueda){
			calendar = Calendar.getInstance();
			calendar.setTime(fecha);
			calendar.add(Calendar.DATE, 1);
			fecha = calendar.getTime();

			//Convertimos al fecha en Entero
			Integer fechaEnEntero = Integer.getInteger(new SimpleDateFormat("yyyyMMdd").format(fecha));

			if(!esFeriado(fechaEnEntero))
				seEncontroFecha = true;
			else
				++numeroBusqueda;
		}

		return fecha;
	}

}
