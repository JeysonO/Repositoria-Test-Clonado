package pe.com.amsac.tramite.api.request.bean;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Builder
public class DocumentoAdjuntoAcusePideRequest {

    private String cuo;
    private String cuoref;
    private String numregstd;
    private String anioregstd;
    private Date fecregstd;
    private String uniorgstd;
    private String usuregstd;
    private MultipartFile file;
    private String obs;
    private String flgest;

}
