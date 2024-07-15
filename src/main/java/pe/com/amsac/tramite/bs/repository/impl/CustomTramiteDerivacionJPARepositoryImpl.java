package pe.com.amsac.tramite.bs.repository.impl;

import org.hibernate.Criteria;
import pe.com.amsac.tramite.api.util.*;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.CustomTramiteDerivacionJPARepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class CustomTramiteDerivacionJPARepositoryImpl extends
        CustomJPARepository<TramiteDerivacion, String> implements
        CustomTramiteDerivacionJPARepository {


    public List<TramiteDerivacion> findByParams(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        String selectClause = "select td " + buildSelectClause();
        String whereClause = buildWhereClause(parameters);
        String orderByClause = buildOrderByClause(orderBy);

        return super.findByQuery(selectClause, whereClause, null,
                orderByClause, parameters, false, pageNumber, pageSize);
    }

    private String buildSelectClause() throws InternalErrorException {
        String selectClause = "from TramiteDerivacion td "
                + " left join fetch td.tramite t "
                + " left join fetch t.tipoTramite tt "
                + " left join fetch td.usuarioInicio ui "
                + " left join fetch td.usuarioFin uf "
                + " left join fetch td.dependenciaUsuarioInicio di "
                + " left join fetch td.dependenciaUsuarioFin df "
                + " left join fetch td.cargoUsuarioInicio ci "
                + " left join fetch td.cargoUsuarioFin cf ";

        return selectClause;
    }

    public String buildWhereClause(Map<String, Object> parameters)
            throws InternalErrorException {

        String whereClause = "";

        if (parameters.get("tramiteId") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.id = :tramiteId";
        }
        if (parameters.get("numeroTramite") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.numeroTramite = :numeroTramite";
        }
        if (parameters.get("origenDocumento") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.origenDocumento = :origenDocumento";
        }
        if (parameters.get("asunto") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "upper(t.asunto) like :asunto";
            parameters.put("asunto",
                    "%" + ((String) parameters.get("asunto")).toUpperCase() + "%");
        }
        if (parameters.get("razonSocial") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "upper(t.razonSocial) like :razonSocial";
            parameters.put("asunto",
                    "%" + ((String) parameters.get("razonSocial")).toUpperCase() + "%");
        }

        //fechaDerivacionDesde
        if (parameters.get("fechaDerivacionDesde") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " td.createdDate >= :fechaDerivacionDesde ";
        }

        if(parameters.get("fechaDerivacionHasta") != null){
            Date fechaHasta = (Date)parameters.get("fechaDerivacionHasta");
            String fechaHastaCadena = new SimpleDateFormat("dd/MM/yyyy").format(fechaHasta);
            fechaHastaCadena = fechaHastaCadena + " " + "23:59:59";
            try {
                fechaHasta = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(fechaHastaCadena);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            parameters.put("fechaDerivacionHasta",fechaHasta);
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " td.createdDate <= :fechaDerivacionHasta ";
        }
        if (parameters.get("usuarioInicio") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "ui.id = :usuarioInicio";
        }
        if (parameters.get("usuarioFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "uf.id = :usuarioFin";
        }

        if (parameters.get("dependenciaIdUsuarioInicio") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "di.id = :dependenciaIdUsuarioInicio";
        }
        if (parameters.get("dependenciaIdUsuarioFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "df.id = :dependenciaIdUsuarioFin";
        }
        if (parameters.get("estado") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "td.estado = :estado";
        }
        if (parameters.get("notEstadoFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "td.estadoFin != :notEstadoFin";
        }

        if (parameters.get("cargoIdUsuarioInicio") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + " ci.id = :cargoIdUsuarioInicio ";
        }
        if (parameters.get("cargoIdUsuarioFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + " cf.id = :cargoIdUsuarioFin ";
        }

        if (parameters.get("fueraPlazofechaMaximaAtencion") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " td.fechaMaximaAtencion < :fueraPlazofechaMaximaAtencion ";
        }

        if (parameters.get("cargoIdInicioOFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + " (ci.id = :cargoIdInicioOFin or cf.id = :cargoIdInicioOFin )";
        }
        if (parameters.get("tipoTramiteId") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + " tt.id = :tipoTramiteId ";
        }

        return whereClause;
    }

    public String buildOrderByClause(String orderBy)
            throws InternalErrorException {

        if (BeanUtils.isNullOrEmpty(orderBy)) {
            return null;
        }
        String orderByClause = "";
        String[] orderByArray = orderBy.split(CharacterConstant.COMMA);
        for (String orderByElement : orderByArray) {
            if ("fechaInicio".equals(orderByElement)) {
                orderByClause = (!"".equals(orderByClause) ? orderByClause
                        + " " + CharacterConstant.COMMA + " " : "");
                orderByClause = orderByClause + "td." + orderByElement + " desc";
            }
        }
        return orderByClause;
    }
}