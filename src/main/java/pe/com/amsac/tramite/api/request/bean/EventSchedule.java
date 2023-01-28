package pe.com.amsac.tramite.api.request.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EventSchedule {
    private String resource;
    private String httpMethod;
    private Map<String, String> headers;
    private String body;
}
