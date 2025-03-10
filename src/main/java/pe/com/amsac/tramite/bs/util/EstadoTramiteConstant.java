package pe.com.amsac.tramite.bs.util;

public class EstadoTramiteConstant {
    public final static String ATENDIDO     = "ATENDIDO";
    public final static String DERIVADO     = "DERIVADO";
    public final static String SUBSANADO    = "SUBSANADO";
    public final static String SUBSANACION  = "SUBSANACION";
    public final static String RECHAZADO    = "RECHAZADO";
    public final static String RECEPCIONADO = "RECEPCIONADO";
    public final static String REGISTRADO   = "REGISTRADO";
    public final static String NOTIFICADO   = "NOTIFICADO";
    public final static String CANCELADO    = "CANCELADO";
    public final static String ELIMINADO    = "ELIMINADO"; //Se considera este estado para eliminacion logica


    public final static String PENDIENTE = "PENDIENTE"; //PIDE
    public final static String ENVIADO_PIDE             = "ENVIADO"; //PIDE
    public final static String POR_ENVIAR_PIDE          = "POR_ENVIAR_PIDE"; //PIDE
    public final static String OBSERVADO_PIDE           = "OBSERVADO"; //PIDE
    public final static String CON_ERROR_PIDE           = "CON_ERROR"; //PIDE

    //FILTROS DE ESTADO PARA REPORRTE
    public final static String DERIVADO_DESDE           = "DERIVADO_DESDE";
    public final static String DERIVADO_A               = "DERIVADO_A";
    public final static String FUERA_PLAZO              = "FUERA_PLAZO";
}
