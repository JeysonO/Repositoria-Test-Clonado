package pe.com.amsac.tramite.api.request.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RequestSchedule {

    private String group;
    private Integer priority;
    private Date creation;
    private Date startAt;
    private Date endAt;
    private String cron;
    private EventSchedule event;
}
