package pe.com.amsac.tramite.bs.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.amsac.tramite.api.config.SecurityHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Util {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private SecurityHelper securityHelper;

    public Date addMinuteToJavaUtilDate(Date date, int minutos) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutos);
        return calendar.getTime();
    }

    public String createCron(Date fecha){
        String patron = "0 %s %s %s %s ? %s";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        //segundo minuto hora dia mes ? anio
        //0 50 2 1 5 ? 2022
        String cron = String.format(patron,calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.YEAR));
        return cron;
    }

    public Map<String, String> buildHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put(AUTHORIZATION, "Bearer "+securityHelper.getTokenCurrentSession());
        return map;
    }
}
