package pe.com.amsac.tramite.bs.repository;

import org.springframework.stereotype.Repository;
import pe.com.amsac.tramite.api.util.CustomRepository;
import pe.com.amsac.tramite.api.util.InternalErrorException;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.dto.DetalleDashboardDTO;
import pe.com.amsac.tramite.bs.dto.ResumenReporteDashboardDTO;

import java.util.List;
import java.util.Map;


@Repository
public interface CustomTramiteDerivacionJPARepository extends CustomRepository<TramiteDerivacion, String> {

    public List<DetalleDashboardDTO> obtenerDetalleDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public List<ResumenReporteDashboardDTO> obtenerResumenDependenciaDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public List<ResumenReporteDashboardDTO> obtenerResumenCantidadPorMesDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public Integer obtenerResumenCantidadTotalDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public List<ResumenReporteDashboardDTO> obtenerResumenPorEstadoDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public List<ResumenReporteDashboardDTO> obtenerResumenPorUsuarioDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;

    public Integer obtenerDetalleRecordCountDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException;
	
}