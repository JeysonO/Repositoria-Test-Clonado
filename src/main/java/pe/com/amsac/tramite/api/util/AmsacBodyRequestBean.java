package pe.com.amsac.tramite.api.util;

import java.io.Serializable;

public class AmsacBodyRequestBean<T extends Serializable> {

	private T id;

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

}
