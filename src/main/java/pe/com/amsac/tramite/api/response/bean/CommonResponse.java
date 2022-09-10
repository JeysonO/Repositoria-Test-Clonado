package pe.com.amsac.tramite.api.response.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommonResponse {

    private Meta meta;
    private Object data;

}
