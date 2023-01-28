package pe.com.amsac.tramite.bs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.request.bean.RequestSchedule;
import pe.com.amsac.tramite.bs.domain.Calendario;
import pe.com.amsac.tramite.bs.repository.CalendarioMongoRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	public void eliminarScheduler(String schedulerTaskId){
		String urlSchedule = env.getProperty("app.url.scheduler");
		urlSchedule = urlSchedule + "/" + schedulerTaskId;
		restTemplate.delete(urlSchedule);
	}

	public String scheduleRegister(RequestSchedule schedule, HttpMethod method) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RequestSchedule> entity = new HttpEntity<>(schedule, headers);
		String urlSchedule = env.getProperty("app.url.scheduler");

		ResponseEntity<String> retorno = restTemplate.exchange(urlSchedule, method, entity, String.class);
		String r = retorno.getBody();
		return r;

	}

}
