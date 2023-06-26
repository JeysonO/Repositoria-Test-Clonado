package pe.com.amsac.tramite.api.file.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import pe.com.amsac.tramite.bs.domain.DocumentoAdjunto;

import java.io.File;
import java.text.SimpleDateFormat;

@Component
//@RequestScope
public class TramitePathFileStorage extends CreatePathFileStorage<DocumentoAdjunto> {
    @Autowired
    private FileTxProperties fileTxProperties;

    private DocumentoAdjunto documentoAdjunto;

    @Override
    public String build() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String ruta = fileTxProperties.getBaseUploadDir()
                .concat(File.separator)
                .concat(String.valueOf(sdf.format(documentoAdjunto.getTramite().getCreatedDate())))
                .concat(File.separator)
                .concat(String.valueOf(documentoAdjunto.getTramite().getId())
                        .concat(File.separator)
                        .concat("adjuntos"));
        return ruta;
    }

    public TramitePathFileStorage setObject(DocumentoAdjunto documentoAdjunto) {
        this.documentoAdjunto = documentoAdjunto;
        return this;
    }
}
