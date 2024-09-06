package pe.com.amsac.tramite.bs.repository.impl;

import pe.com.amsac.tramite.api.util.*;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.dto.DetalleDashboardDTO;
import pe.com.amsac.tramite.bs.dto.ResumenReporteDashboardDTO;
import pe.com.amsac.tramite.bs.repository.CustomTramiteDerivacionJPARepository;
import pe.com.amsac.tramite.bs.util.TipoTramiteConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + " t.estado != 'ELIMINADO' ";

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

    public List<DetalleDashboardDTO> obtenerDetalleDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<DetalleDashboardDTO> detalleDashboardDTOList = new ArrayList<>();

        String selectClause = null;
        String whereClause = null;
        String orderByClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select '0' as id_tramite_derivacion, t.id_tramite, t.numero_tramite as numTramite, t.created_date as fechaCreacion, t.asunto, t.estado, di.nombre as dependenciaEmisor, CONCAT(ui.nombre,' ',ui.ape_paterno) as usuarioEmisior, ep.nombre as dependenciaDestino, ee.nombre as usuarioDestino, do.descripcion, tp.descripcion as prioridad, DATEDIFF(DAY,t.fecha_envio,ISNULL(t.fecha_recepcion,GETDATE())) as atencion, tt.tipo_tramite \n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            orderByClause = " ORDER BY t.created_date desc ";
        }else{
            String campoEstado = null;
            if(parameters.get("estado")!=null && parameters.get("estado").equals("P"))
                campoEstado = "ISNULL(td.estado_fin,'PENDIENTE')";
            else
                campoEstado = parameters.get("estadoFin")!=null && (parameters.get("estadoFin").equals("ATENDIDO") || parameters.get("estadoFin").equals("FUERA_PLAZO"))?"ISNULL(td.estado_fin,'FUERA_PLAZO')":"td.estado_inicio";

            selectClause = "select td.id_tramite_derivacion, t.id_tramite, t.numero_tramite as numTramite, t.created_date as fechaCreacion, t.asunto, "+ campoEstado +" as estado, di.nombre as dependenciaEmisor, CONCAT(ui.nombre,' ',ui.ape_paterno) as usuarioEmisior, df.nombre as dependenciaDestino, CONCAT(uf.nombre,' ',uf.ape_paterno) as usuarioDestino, do.descripcion, tp.descripcion as prioridad, CAST(\n" +
                    "             CASE\n" +
                    "                  WHEN DATEDIFF(DAY,td.fecha_maximo_atencion,ISNULL(td.fecha_fin,GETDATE())) < 0 or td.fecha_maximo_atencion is null\n" +
                    "                     THEN 0\n" +
                    "                  ELSE DATEDIFF(DAY,td.fecha_maximo_atencion,ISNULL(td.fecha_fin,GETDATE()))\n" +
                    "             END AS int) as atencion, tt.tipo_tramite \n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            orderByClause = " ORDER BY td.fecha_inicio desc ";
        }

        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause + orderByClause, parameters, false, pageNumber, pageSize);

        DetalleDashboardDTO detalleDashboardDTO = null;
        for(Object[] objeto : resultado){
            detalleDashboardDTO = new DetalleDashboardDTO();
            detalleDashboardDTO.setIdTramiteDerivacion(objeto[0].toString());
            detalleDashboardDTO.setIdTramite(objeto[1].toString());
            detalleDashboardDTO.setTramite(Integer.parseInt(objeto[2].toString()));
            detalleDashboardDTO.setFechaInicio((Date)objeto[3]);
            detalleDashboardDTO.setAsunto(objeto[4].toString());
            if(objeto[5]!=null)
                detalleDashboardDTO.setEstado(objeto[5].toString());
            detalleDashboardDTO.setDependenciaRemitente(objeto[6].toString());
            detalleDashboardDTO.setUsuarioRemitente(objeto[7].toString());
            detalleDashboardDTO.setDependenciaReceptor(objeto[8].toString());
            detalleDashboardDTO.setUsuarioReceptor(objeto[9].toString());
            detalleDashboardDTO.setTipoDocumento(objeto[10].toString());
            detalleDashboardDTO.setPrioridad(objeto[11].toString());
            detalleDashboardDTO.setDiasFueraPlazo(Integer.parseInt(objeto[12].toString()));
            detalleDashboardDTO.setTipoTramite(objeto[13].toString());
            detalleDashboardDTOList.add(detalleDashboardDTO);
        }

        return detalleDashboardDTOList;
    }

    public String buildWhereDetalleDashboard(Map<String, Object> parameters){

        String whereClause = "";
        //String columnaFecha = (parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE))?"t.created_date":"td.fecha_inicio";
        String columnaFecha = "t.created_date";
        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            if (parameters.get("estadoFin") != null) {
                whereClause = (!"".equals(whereClause) ? whereClause + " "
                        + JpaConstant.CONDITION_AND + " " : "");
                whereClause = whereClause
                        + "t.estado = :estadoFin";
            }
        }else{
            if (parameters.get("estado") != null) {
                whereClause = (!"".equals(whereClause) ? whereClause + " "
                        + JpaConstant.CONDITION_AND + " " : "");
                whereClause = whereClause
                        + "td.estado = :estado";
            }
            if (parameters.get("estadoFin") != null) {
                whereClause = (!"".equals(whereClause) ? whereClause + " "
                        + JpaConstant.CONDITION_AND + " " : "");

                if(parameters.get("estadoFin").equals("ATENDIDO") ){
                    whereClause = whereClause
                            + "td.estado_fin = :estadoFin";
                }else{
                    if(!parameters.get("estadoFin").equals("FUERA_PLAZO") ){
                        whereClause = whereClause
                                + "td.estado_inicio = :estadoFin";
                    }else{
                        whereClause = whereClause
                                + "DATEDIFF(DAY,td.fecha_maximo_atencion,ISNULL(td.fecha_fin,GETDATE())) >= 0 and td.fecha_maximo_atencion is not null";
                    }
                }
            }

        }

        if (parameters.get("tipoTramiteId") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "tt.id_tipo_tramite = :tipoTramiteId";
        }
        if (parameters.get("numeroTramite") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.numero_tramite = :numeroTramite";
        }
        if (parameters.get("asunto") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.asunto like :asunto";
            parameters.put("asunto",
                    "%" + ((String) parameters.get("asunto")).toUpperCase() + "%");
        }
        if (parameters.get("razonSocial") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.razon_social like :razonSocial";
            parameters.put("razonSocial",
                    "%" + ((String) parameters.get("razonSocial")).toUpperCase() + "%");
        }
        if (parameters.get("entidadPideId") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "t.id_entidad_pide = :entidadPideId";
        }
        if (parameters.get("dependenciaIdUsuarioInicio") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
                whereClause = whereClause
                        + "t.id_dependencia_remitente = :dependenciaIdUsuarioInicio";
            }else{
                if(parameters.get("estadoFin")!=null){
                    if(parameters.get("estadoFin").equals("ATENDIDO") || parameters.get("estadoFin").equals("FUERA_PLAZO") ){
                        whereClause = whereClause
                                + "td.id_dependencia_usuario_fin = :dependenciaIdUsuarioInicio";
                    }else{
                        whereClause = whereClause
                                + "td.id_dependencia_usuario_inicio = :dependenciaIdUsuarioInicio";
                    }
                }
                if(parameters.get("estado")!=null && parameters.get("estado").equals("P")){
                    whereClause = whereClause
                            + "td.id_dependencia_usuario_fin = :dependenciaIdUsuarioInicio";
                }
            }

        }
        if (parameters.get("usuarioInicio") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
                whereClause = whereClause
                        + "t.id_usuario_remitente = :usuarioInicio";
            }else{
                if(parameters.get("estadoFin")!=null){
                    if(parameters.get("estadoFin").equals("ATENDIDO") ){
                        whereClause = whereClause
                                + "td.id_usuario_fin = :dependenciaIdUsuarioInicio";
                    }else{
                        whereClause = whereClause
                                + "td.id_usuario_inicio = :dependenciaIdUsuarioInicio";
                    }
                }
                if(parameters.get("estado")!=null && parameters.get("estado").equals("P")){
                    whereClause = whereClause
                            + "td.id_usuario_fin = :dependenciaIdUsuarioInicio";
                }
            }

        }
        /*
        if (parameters.get("dependenciaIdUsuarioFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "td.id_dependencia_usuario_fin = :dependenciaIdUsuarioFin";
        }
        if (parameters.get("usuarioFin") != null) {
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause
                    + "td.id_usuario_fin = :usuarioFin";
        }
        */
        if (parameters.get("fechaCreacionDesde") != null) {
            Date fechaDesde = (Date)parameters.get("fechaCreacionDesde");
            String fechaDesdeCadena = new SimpleDateFormat("yyyy-MM-dd").format(fechaDesde);
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " DATEDIFF(DAY,:fechaCreacionDesde,"+columnaFecha+") >= 0 ";
            parameters.put("fechaCreacionDesde",fechaDesdeCadena);
        }

        if(parameters.get("fechaCreacionHasta") != null){
            Date fechaHasta = (Date)parameters.get("fechaCreacionHasta");
            String fechaHastaCadena = new SimpleDateFormat("yyyy-MM-dd").format(fechaHasta);
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " DATEDIFF(DAY,"+columnaFecha+",:fechaCreacionHasta) >= 0 ";
            parameters.put("fechaCreacionHasta",fechaHastaCadena);
        }

        if(parameters.get("todoAnio")!=null && parameters.get("todoAnio").toString().equals("S")){
            Date fecha = new Date();
            String fechaAnioCadena = new SimpleDateFormat("yyyy").format(fecha);
            whereClause = (!"".equals(whereClause) ? whereClause + " "
                    + JpaConstant.CONDITION_AND + " " : "");
            whereClause = whereClause + " DATEDIFF(YEAR,:fechaAnioCadena,"+columnaFecha+") = 0 ";
            parameters.put("fechaAnioCadena",fechaAnioCadena);
            parameters.remove("todoAnio");
        }

        whereClause = (!"".equals(whereClause) ? whereClause + " "
                + JpaConstant.CONDITION_AND + " " : "");
        whereClause = whereClause
                + " t.estado != 'ELIMINADO' ";

        return whereClause;
    }

    public List<ResumenReporteDashboardDTO> obtenerResumenDependenciaDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<ResumenReporteDashboardDTO> resumenReporteDashboardDTOList = new ArrayList<>();

        String selectClause = null;
        String whereClause = null;
        String groupByClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select di.nombre as dependencia, count(*) cantidad\n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by di.nombre ";
        }else{
            selectClause = "select df.nombre as dependencia, count(*) cantidad\n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by df.nombre ";
        }


        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause + groupByClause, parameters, false, pageNumber, pageSize);

        ResumenReporteDashboardDTO resumenReporteDashboardDTO = null;
        for(Object[] objeto : resultado){
            resumenReporteDashboardDTO = new ResumenReporteDashboardDTO();
            resumenReporteDashboardDTO.setKey(objeto[0].toString());
            resumenReporteDashboardDTO.setValue(Integer.parseInt(objeto[1].toString()));
            resumenReporteDashboardDTOList.add(resumenReporteDashboardDTO);
        }

        return resumenReporteDashboardDTOList;
    }

    public List<ResumenReporteDashboardDTO> obtenerResumenCantidadPorMesDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<ResumenReporteDashboardDTO> resumenReporteDashboardDTOList = new ArrayList<>();

        String selectClause = null;
        String whereClause = null;
        String groupByClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select month(t.created_date) as mes, count(*) as cantidad\n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by month(t.created_date) ";
        }else{
            selectClause = "select month(t.created_date) as mes, count(*) as cantidad\n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by month(t.created_date) ";
        }


        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause + groupByClause, parameters, false, pageNumber, pageSize);

        ResumenReporteDashboardDTO resumenReporteDashboardDTO = null;
        for(Object[] objeto : resultado){
            resumenReporteDashboardDTO = new ResumenReporteDashboardDTO();
            resumenReporteDashboardDTO.setKey(Integer.parseInt(objeto[0].toString()));
            resumenReporteDashboardDTO.setValue(Integer.parseInt(objeto[1].toString()));
            resumenReporteDashboardDTOList.add(resumenReporteDashboardDTO);
        }

        return resumenReporteDashboardDTOList;
    }

    public Integer obtenerResumenCantidadTotalDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        Integer cantidadTotalDashboard = 0;
        String selectClause = null;
        String whereClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select count(*) as cantidad\n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
        }else{
            selectClause = "select count(*) as cantidad from ( \n" +
                    "select distinct t.id_tramite \n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters) + ") a";
        }

        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause, parameters, false, pageNumber, pageSize);

        ResumenReporteDashboardDTO resumenReporteDashboardDTO = null;
        for(Object objeto : resultado){
            cantidadTotalDashboard = (Integer.parseInt(objeto.toString()));
        }

        return cantidadTotalDashboard;
    }

    public List<ResumenReporteDashboardDTO> obtenerResumenPorEstadoDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<ResumenReporteDashboardDTO> resumenReporteDashboardDTOList = new ArrayList<>();

        String selectClause = null;
        String whereClause = null;
        String groupByClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select t.estado as estado, count(*) as cantidad\n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " GROUP by t.estado ";
        }else{
            String campoEstado = null;
            if(parameters.get("estado")!=null && parameters.get("estado").equals("P"))
                campoEstado = "ISNULL(td.estado_fin,'PENDIENTE')";
            else
                campoEstado = parameters.get("estadoFin")!=null && (parameters.get("estadoFin").equals("ATENDIDO") || parameters.get("estadoFin").equals("FUERA_PLAZO"))?"ISNULL(td.estado_fin,'PENDIENTE')":"td.estado_inicio";

            //selectClause = "select t.estado as estado, count(*) as cantidad\n" +
            selectClause = "select "+campoEstado+" as estado, count(*) as cantidad\n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " GROUP by "+campoEstado;
        }


        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause + groupByClause, parameters, false, pageNumber, pageSize);

        ResumenReporteDashboardDTO resumenReporteDashboardDTO = null;
        for(Object[] objeto : resultado){
            resumenReporteDashboardDTO = new ResumenReporteDashboardDTO();
            resumenReporteDashboardDTO.setKey(objeto[0].toString());
            resumenReporteDashboardDTO.setValue(Integer.parseInt(objeto[1].toString()));
            resumenReporteDashboardDTOList.add(resumenReporteDashboardDTO);
        }

        return resumenReporteDashboardDTOList;
    }

    public List<ResumenReporteDashboardDTO> obtenerResumenPorUsuarioDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<ResumenReporteDashboardDTO> resumenReporteDashboardDTOList = new ArrayList<>();

        String selectClause = null;
        String whereClause = null;
        String groupByClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select CONCAT(ui.nombre,' ', ui.ape_paterno) as usuario, count(*) as cantidad\n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by CONCAT(ui.nombre,' ', ui.ape_paterno) ";
        }else{
            selectClause = "select CONCAT(uf.nombre,' ', uf.ape_paterno) as usuario, count(*) as cantidad\n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
            groupByClause = " group by CONCAT(uf.nombre,' ', uf.ape_paterno) ";
        }

        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause + groupByClause, parameters, false, pageNumber, pageSize);

        ResumenReporteDashboardDTO resumenReporteDashboardDTO = null;
        for(Object[] objeto : resultado){
            resumenReporteDashboardDTO = new ResumenReporteDashboardDTO();
            resumenReporteDashboardDTO.setKey(objeto[0].toString());
            resumenReporteDashboardDTO.setValue(Integer.parseInt(objeto[1].toString()));
            resumenReporteDashboardDTOList.add(resumenReporteDashboardDTO);
        }

        return resumenReporteDashboardDTOList;
    }

    public Integer obtenerDetalleRecordCountDashboard(
            Map<String, Object> parameters, String orderBy, String groupBy, int pageNumber,
            int pageSize) throws InternalErrorException {

        List<DetalleDashboardDTO> detalleDashboardDTOList = new ArrayList<>();
        Integer cantidadTotalDashboard = 0;

        String selectClause = null;
        String whereClause = null;

        if(parameters.get("tipoTramite").equals(TipoTramiteConstant.DESPACHO_PIDE)){
            selectClause = "select count(*) as cantidad \n" +
                    "from tramite t \n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = t.id_dependencia_remitente\n" +
                    "inner join entidad_pide ep    on ep.id_entidad_pide = t.id_entidad_pide\n" +
                    "inner join usuario ui        on ui.id_usuario = t.id_usuario_remitente\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad\n" +
                    "inner join tramite_entidad_externa ee on ee.id_tramite = t.id_tramite ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
        }else{
            selectClause = "select count(*) as cantidad \n" +
                    "from tramite_derivacion td \n" +
                    "inner join tramite t         on t.id_tramite = td.id_tramite\n" +
                    "inner join tipo_tramite tt   on tt.id_tipo_tramite = t.id_tipo_tramite\n" +
                    "inner join dependencia di    on di.id_dependencia = td.id_dependencia_usuario_inicio\n" +
                    "inner join dependencia df    on df.id_dependencia = td.id_dependencia_usuario_fin\n" +
                    "inner join usuario ui        on ui.id_usuario = td.id_usuario_inicio\n" +
                    "inner join usuario uf        on uf.id_usuario = td.id_usuario_fin\n" +
                    "inner join tipo_documento_tramite do   on do.id_tipo_documento = t.id_tipo_documento\n" +
                    "inner join tramite_prioridad tp   on tp.id_tramite_prioridad = t.id_tramite_prioridad ";
            whereClause = " where " + buildWhereDetalleDashboard(parameters);
        }

        List<Object[]> resultado = super.executeNativeQuery(selectClause + whereClause, parameters, false, 0, 0);

        for(Object objeto : resultado){
            cantidadTotalDashboard = (Integer.parseInt(objeto.toString()));
        }

        return cantidadTotalDashboard;
    }

}