package pe.com.amsac.tramite.bs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ResourceNotFoundException;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.file.bean.FileStorageService;
import pe.com.amsac.tramite.api.request.bean.*;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.response.bean.*;
import pe.com.amsac.tramite.api.util.CustomMultipartFile;
import pe.com.amsac.tramite.api.util.InternalErrorException;
import pe.com.amsac.tramite.bs.domain.*;
import pe.com.amsac.tramite.bs.repository.*;
import pe.com.amsac.tramite.bs.util.*;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPConnector;
import pe.com.amsac.tramite.pide.soap.tramite.request.*;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class TramiteCommandHandlerService {

	private final TramiteService tramiteService;

	private final FirmaDocumentoService firmaDocumentoService;

	public Map registrarTramitePideHandler(TramitePideBodyRequest tramitePideBodyRequest, MultipartFile filePrincipal, List<MultipartFile> fileAnexos, DatosFirmaDocumentoRequest datosFirmaDocumentoRequest) throws Exception {

		Tramite tramitePide =  tramiteService.registrarTramitePide(tramitePideBodyRequest, filePrincipal, fileAnexos, datosFirmaDocumentoRequest);

		Map resultadEnvio = new HashMap();

		try{
			if(datosFirmaDocumentoRequest!=null){
				tramiteService.enviarDocumentoParaFirma(datosFirmaDocumentoRequest, filePrincipal, tramitePide);
				tramiteService.actualizarDatosDocumentoFirmadoDigitalmente(tramitePide.getId());
			}

			String estadoTramite = tramitePide.getEstado();

			//try{
			//resultadEnvio = enviarTramiteAPide(tramitePide.getId());
			resultadEnvio = tramiteService.enviarTramiteAPide(tramitePide, tramitePideBodyRequest, filePrincipal, fileAnexos);
			//Vemos el indicados de resultado, si es ok entonces solocamos el estado enviado pide, sino con error pide.
			estadoTramite = EstadoTramiteConstant.ENVIADO_PIDE;
			if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR))
				estadoTramite = EstadoTramiteConstant.CON_ERROR_PIDE;
			if(resultadEnvio.get("resultado").equals(EstadoResultadoEnvioPideConstant.ERROR_SERVICIO)){
				estadoTramite = EstadoTramiteConstant.POR_ENVIAR_PIDE;
				resultadEnvio.put("mensaje", Map.of("vcodres", "001", "vdesres", "Hubo un error en la transmisión, se volverá a intentar de forma automática en unos momentos y se informará por correo de los resultados"));
			}
			/*
			}catch (Exception ex){
				estadoTramite = EstadoTramiteConstant.POR_ENVIAR_PIDE;
				resultadEnvio.put("mensaje", Map.of("vcodres", "001", "vdesres", "Hubo un error en la transmisión, se volverá a intentar de forma automática en unos momentos y se informará por correo de los resultados"));
				log.error("ERROR", ex);
			}
			*/

			Tramite tramite = tramiteService.findById(tramitePide.getId());
			tramite.setEstado(estadoTramite);
			tramite.setIntentosEnvio(tramite.getIntentosEnvio()==null?1:tramite.getIntentosEnvio()+1);
			tramiteService.save(tramite);

			resultadEnvio.put("tramiteId",tramite.getId());
			resultadEnvio.put("resultado",estadoTramite);
		}catch (Exception ex){
			//Hacemos la compensacion de la transaccion de registro de tramite, borramos
			log.error("ERROR", ex);
		}

		return resultadEnvio;

	}

	public Tramite recepcionarTramitePide(TramiteBodyRequest tramiteBodyRequest) throws Exception {

		//Registramos tramite
		Tramite tramitePide =  tramiteService.recepcionarTramitePide(tramiteBodyRequest);

		try{
			//Generamos el acuse y se firma
			Map param = tramiteService.generarReporteAcuseTramiteInteroperabilidad(tramitePide,tramiteBodyRequest.getDependenciaInternaDestinoTramitePide());
			tramiteBodyRequest.setId(tramitePide.getId());
			tramiteService.firmarDocumentoAcuse(param, tramiteBodyRequest.getPinFirma(), tramiteBodyRequest.getId());
			//Nos aseguramos que se haya firmado el documento para continuar, sino lanzamos excepcion
			tramiteService.actualizarAcuseComoDocumentoDelTramite(tramitePide.getId());
		}catch (Exception ex){
			//Si hay error, eliminamos logicamente el tramite creado
			tramiteService.eliminarTramite(tramitePide.getId());
			throw ex;
		}


		return tramitePide;

	}

	public Map generarAcuseObservacionFirmado(AcuseReciboObservacionPideRequest acuseReciboObservacionPideRequest) throws Exception {

		//Generamos el acuse
		Map<String, Object> parameters = new ObjectMapper().convertValue(acuseReciboObservacionPideRequest, new TypeReference<Map<String, Object>>() {});
		Map<String, Object> param =  tramiteService.generarReporteAcuseTramiteInteroperabilidadObservacion(parameters);

		//Ahora se firma el documento
		String idTransaccionFirma = tramiteService.firmarDocumentoAcuseObservado(param, acuseReciboObservacionPideRequest.getPinFirma());

		log.info("Se genero el idTransaccionFirma para acuse observado: "+idTransaccionFirma);

		//Obtener el archivo firmado
		boolean encontrado = false;
		Resource resource = null;
		Integer cantidadIntentos = 0;
		Integer cantidadIntentosMaximo = 5;
		while(!encontrado){
			Thread.sleep(1000l);
			if(cantidadIntentos<=cantidadIntentosMaximo)
				throw new ServiceException("NO SE OBTUVO EL ARCHIVO ACUSE FIRMADO");
			try{
				++cantidadIntentos;
				resource = firmaDocumentoService.obtenerDocumentoExternoFirmado(idTransaccionFirma);
				encontrado = true;
			}catch (ResourceNotFoundException ex){
				log.info("ARCHIVO NO ENCONTRADO "+idTransaccionFirma);
			}catch (Exception ex){
				throw ex;
			}
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("file",resource);
		resultMap.put("nombreArchivo",resource.getFilename());

		return resultMap;
	}

}
