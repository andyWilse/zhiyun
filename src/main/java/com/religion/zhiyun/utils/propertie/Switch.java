package com.religion.zhiyun.utils.propertie;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Switch {

    /************************************************ 短信提醒开关 *************************************************/
    @Getter
    private static Boolean  alert;
    @Value("${schedule.switch.alert}")
    public void setAlert(Boolean alert) {
        Switch.alert = alert;
    }

    /************************************************ 雪亮截图开关 *************************************************/
    @Getter
    private static Boolean  cut;
    @Value("${schedule.switch.cut}")
    public void setCut(Boolean cut) {
        Switch.cut = cut;
    }

    /************************************************ MAS短信开关 *************************************************/
    @Getter
    private static Boolean  mas;
    @Value("${schedule.switch.mas}")
    public void setMas(Boolean mas) {
        Switch.mas = mas;
    }

}
