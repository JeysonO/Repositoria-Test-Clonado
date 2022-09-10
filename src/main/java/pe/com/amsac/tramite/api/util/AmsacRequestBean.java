package pe.com.amsac.tramite.api.util;

public class AmsacRequestBean {

	protected Integer pageNumber = 0;
	protected Integer pageSize = 0;
	protected String bySort;

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getBySort() {
		return bySort;
	}

	public void setBySort(String bySort) {
		this.bySort = bySort;
	}

}
