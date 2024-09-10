package pe.com.amsac.tramite.bs.repository.impl;

import pe.com.amsac.tramite.api.util.*;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.repository.CustomTramiteDerivacionJPARepository;
import pe.com.amsac.tramite.bs.repository.CustomTramiteJPARepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class CustomTramiteJPARepositoryImpl extends
        CustomJPARepository<Tramite, String> implements
        CustomTramiteJPARepository {


    public List<Tramite> findByParams(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        String selectClause = "select t " + buildSelectClause();
        String whereClause = buildWhereClause(parameters);
        String orderByClause = buildOrderByClause(orderBy);

        return super.findByQuery(selectClause, whereClause, null,
                orderByClause, parameters, false, pageNumber, pageSize);
    }

    private String buildSelectClause() throws InternalErrorException {
        String selectClause = "from Tramite t "
                + " left join fetch t.dependenciaUsuarioCreacion dc "
                + " left join fetch t.cargoUsuarioCreacion cc "
                + " left join fetch t.dependenciaRemitente dr "
                + " left join fetch t.usuarioRemitente ur "
                + " left join fetch t.cargoRemitente cr "
                + " left join fetch t.entidadPide ep "
                + " left join fetch t.tipoDocumento td "
                + " left join fetch t.tipoTramite tt ";


        return selectClause;
    }

    public String buildWhereClause(Map<String, Object> parameters)
            throws InternalErrorException {

        String whereClause = "";

        if (parameters.get("fechaCreacionDesde") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.createdDate >= :fechaCreacionDesde ";
        }
        if(parameters.get("fechaCreacionHasta") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.createdDate <= :fechaCreacionHasta ";
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
        if(parameters.get("createdByUser") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.createdByUser = :createdByUser ";
        }
        if(parameters.get("dependenciaUsuarioCreacion") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " dc.id = :dependenciaUsuarioCreacion ";
        }
        if(parameters.get("numeroTramite") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.numeroTramite = :numeroTramite ";
        }
        if(parameters.get("numeroDocumento") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.numeroDocumento = :numeroDocumento ";
        }
        if(parameters.get("siglas") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.siglas = :siglas ";
        }
        if(parameters.get("cargoUsuarioCreacion") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " cc.id = :cargoUsuarioCreacion ";
        }
        if(parameters.get("origen") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.origen = :origen ";
        }
        if(parameters.get("id") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.id = :id ";
        }
        if(parameters.get("cuo") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.cuo = :cuo ";
        }
        if(parameters.get("tipoTramite") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " tt.tipoTramite = :tipoTramite ";
        }
        if(parameters.get("tipoTramiteId") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " tt.id = :tipoTramiteId ";
        }
        if(parameters.get("estado") != null){
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.estado = :estado ";
        }else{
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " t.estado <> 'ELIMINADO' ";
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
            if ("numeroTramite".equals(orderByElement)) {
                orderByClause = (!"".equals(orderByClause) ? orderByClause
                        + " " + CharacterConstant.COMMA + " " : "");
                orderByClause = orderByClause + "t." + orderByElement + " desc";
            }
            if ("createdDate".equals(orderByElement)) {
                orderByClause = (!"".equals(orderByClause) ? orderByClause
                        + " " + CharacterConstant.COMMA + " " : "");
                orderByClause = orderByClause + "t." + orderByElement + " desc";
            }

        }
        return orderByClause;
    }
}