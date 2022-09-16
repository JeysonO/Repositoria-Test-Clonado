package pe.com.amsac.tramite.api.file.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
@Data
public class FileTxProperties extends FileStorageProperties {
    private String baseProgramacionDir;
}
