package pe.com.amsac.tramite;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;
import pe.com.amsac.tramite.api.config.SpringSecurityAuditorAware;
import pe.com.amsac.tramite.api.util.StringConverter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
//@ComponentScan(basePackages = {"pe.com.bitall.framework.api.*", "pe.com.bitall.security.*"})
@ComponentScan(basePackages = {"pe.com.amsac.tramite.*"})
@EnableSwagger2
//@EnableFeignClients(basePackages = {"pe.com.bitall.tc.mail.*"})
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
public class AmsacTramiteApplication {


	public static void main(String[] args) {
		SpringApplication.run(AmsacTramiteApplication.class, args);
	}
	
	@Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
                .apis(RequestHandlerSelectors.basePackage("pe.com.amsac.tramite.api.controller"))
                .paths(PathSelectors.any())
            .build()
            .apiInfo(new ApiInfoBuilder().version("1.0").title("Tramite API").description("Documentacion de las API de Seguridad y Gestión de Usuarios v1.0").build());
    }
	
	@Bean
	UiConfiguration uiConfig() {
	    return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
	            UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
	}

	@Bean
	public RestTemplate getBeanRstTemplate(RestTemplateBuilder builder )
	{
		RestTemplate restTemplate =
				builder
						// Agregar interceptor de solicitudes
						//.interceptors(new ClienteResTemplateInterceptor())
						.build();
		// convertidor de mensajes
		//restTemplate.getMessageConverters().add(new RestMessageInterceptor());
		return restTemplate;
	}

	@Bean
	public Mapper beanMapper() {
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<CustomConverter> customConverterList = new ArrayList<CustomConverter>();
		customConverterList.add(new StringConverter());
		mapper.setCustomConverters(customConverterList);
		return mapper;
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		return new SpringSecurityAuditorAware();
	}

	/*
	@Bean
	public MongoClient mongo() {
		ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/amsac-tramite");
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), "amsac-tramite");
	}
	*/

}

