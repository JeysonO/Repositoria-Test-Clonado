package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.KeycloakProperties;
import pe.com.amsac.tramite.api.request.bean.DocumentoAdjuntoRequest;
import pe.com.amsac.tramite.api.request.body.bean.EventAlertaBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.FirmaDocumentoTramiteExternoBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.service.FirmaDocumentoService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RestController
@RequestMapping("/firma")
public class FirmaDigitalController {

	@Autowired
	private FirmaDocumentoService firmaDocumentoService;

    @PostMapping("/firmar-documento-tramite")
    public ResponseEntity<CommonResponse> firmarDocumentoTramite(@Valid @RequestBody FirmaDocumentoTramiteBodyRequest firmaDocumentoTramiteBodyRequest) throws Exception {

		log.info(">> firmarDocumentoTramite");

		firmaDocumentoService.firmarDocumentoTramite(firmaDocumentoTramiteBodyRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

    }

	@PostMapping("/firmar-documento-externo")
	public ResponseEntity<CommonResponse> firmarDocumentoExterno(
			@RequestParam(value = "textoFirma", required = false) String textoFirma,
			@RequestParam(value = "positionId", required = false) String positionId,
			@RequestParam(value = "pin", required = false) String pin,
			@RequestParam(value = "imagenFirmaDigitalId", required = false) String imagenFirmaDigitalId,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

		log.info(">> firmarDocumentoExterno");

		FirmaDocumentoTramiteExternoBodyRequest firmaDocumentoTramiteExternoBodyRequest = FirmaDocumentoTramiteExternoBodyRequest.builder()
				.textoFirma(textoFirma)
				.positionId(positionId)
				.pin(pin)
				.imagenFirmaDigitalId(imagenFirmaDigitalId)
				.email(email)
				.file(file)
				.build();

		firmaDocumentoService.firmarDocumentoExterno(firmaDocumentoTramiteExternoBodyRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, HttpStatus.OK);

	}

	@GetMapping("/download/external/{firmaDocumentoId}")
	public ResponseEntity<Resource> descargarDocumentoFirmadoExterno(@PathVariable String firmaDocumentoId, HttpServletRequest request) throws Exception {

		log.info(">> firmarDocumentoExterno");

		try {

			Resource resource = firmaDocumentoService.obtenerDocumentoExternoFirmado(firmaDocumentoId);

			String contentType = null;
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
				log.info("No se puede determinar el MimeType.");
			}

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);

		} catch (Exception e) {
			throw e;
		}

	}

}
