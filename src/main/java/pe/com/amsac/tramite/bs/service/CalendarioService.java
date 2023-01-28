package pe.com.amsac.tramite.bs.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Mensaje;
import pe.com.amsac.tramite.api.response.bean.TramiteReporteResponse;
import pe.com.amsac.tramite.api.response.bean.TramiteResponse;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.CalendarioMongoRepository;
import pe.com.amsac.tramite.bs.repository.TipoDocumentoMongoRepository;
import pe.com.amsac.tramite.bs.repository.TramiteMongoRepository;
import pe.com.amsac.tramite.bs.repository.UsuarioMongoRepository;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
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
		List<Calendario> listaFechaFeriado = calendarioMongoRepository.findFechaNumber(fechaEnNumero);
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
