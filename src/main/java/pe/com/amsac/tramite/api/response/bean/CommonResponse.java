package pe.com.amsac.tramite.api.response.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommonResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("data")
    private Object data;

}
