package pe.com.amsac.tramite.api.request;

import lombok.Data;

import java.util.Date;

@Data
public class BaseRequest{

	protected int pageNumber;
	protected int pageSize;

}
