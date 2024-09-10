package pe.com.amsac.tramite.pide.soap.entidad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPConnector;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPEntidadConnector;

@Configuration
public class ConfigEntidadSoap {

    @Autowired
    private Environment env;

    @Bean
    public Jaxb2Marshaller marshallerEntidad() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("pe.com.amsac.tramite.pide.soap.entidad.request");
        return marshaller;
    }

    @Bean
    public SOAPEntidadConnector soapEntidadConnector(Jaxb2Marshaller marshallerEntidad) {
        SOAPEntidadConnector client = new SOAPEntidadConnector();
        client.setDefaultUri(env.getProperty("app.url.entidadServer"));
        client.setMarshaller(marshallerEntidad);
        client.setUnmarshaller(marshallerEntidad);
        return client;
    }
}
