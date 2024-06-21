package pe.com.amsac.tramite.pide.soap.tramite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pe.com.amsac.tramite.pide.soap.endpoint.SOAPConnector;

@Configuration
public class Config {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this is the package name specified in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("pe.com.amsac.tramite.pide.soap.tramite.request");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        SOAPConnector client = new SOAPConnector();
        //client.setDefaultUri("http://161.132.150.248/wsentidad/Entidad");
        client.setDefaultUri("http://161.132.150.248/wsiopidetramite/IOTramite");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
