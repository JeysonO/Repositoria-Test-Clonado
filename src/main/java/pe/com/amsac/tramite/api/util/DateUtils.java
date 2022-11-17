package pe.com.amsac.tramite.api.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    private static final int[] DAYS_OF_MONTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    protected DateUtils() {
        throw new UnsupportedOperationException();
    }

    public static Date getSystemDate() {
        return new Date(System.currentTimeMillis());
    }

    public static Short getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        Short year = new Short((short)calendar.get(1));
        return year;
    }

    public static Short getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        Short month = new Short((short)(calendar.get(2) + 1));
        return month;
    }

    public static Short getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        Short dayOfMonth = new Short((short)calendar.get(5));
        return dayOfMonth;
    }

    public static Short getCurrentHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        Short hourOfDay = new Short((short)calendar.get(11));
        return hourOfDay;
    }

    public static Short getCurrentMinuteOfHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        Short minuteOfHourOfDay = new Short((short)calendar.get(12));
        return minuteOfHourOfDay;
    }

    public static Short getCurrentSecondOfHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        Short secondOfHourOfDay = new Short((short)calendar.get(13));
        return secondOfHourOfDay;
    }

    public static Short getCurrentMillisecondOfHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        Short millisecondOfHourOfDay = new Short((short)calendar.get(14));
        return millisecondOfHourOfDay;
    }

    public static long getIntervalInSeconds(Date fromDate, Date thruDate) {
        long interval = 0L;
        if (fromDate != null && thruDate != null && thruDate.after(fromDate)) {
            interval = thruDate.getTime() - fromDate.getTime();
            interval /= 1000L;
        }

        return interval;
    }

    public static Date getBeforeDate(Date date, int days, int hours, int minutes, int seconds) {
        return addToDate(date, -days, -hours, -minutes, -seconds);
    }

    public static Date getAfterDate(Date date, int days, int hours, int minutes, int seconds) {
        return addToDate(date, days, hours, minutes, seconds);
    }

    public static String formatDate(Date value, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(value);
    }

    public static Date strToDate(String value, String format) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        try {
            date = simpleDateFormat.parse(value);
        } catch (ParseException var5) {
        }

        return date;
    }

    public static boolean isDateValid(int day, int month, int year) {
        if (month >= 1 && month <= 12) {
            return day <= getLastDayOfMonth(month, year);
        } else {
            return false;
        }
    }

    public static int getLastDayOfMonth(int month, int year) {
        int day;
        if (month == 2) {
            if (isBisiesto(year)) {
                day = 29;
            } else {
                day = 28;
            }
        } else {
            day = DAYS_OF_MONTH[month - 1];
        }

        return day;
    }

    public static int calculateAge(Date birthDate) {
        boolean age = false;
        byte factor = 0;

        try {
            Calendar birth = new GregorianCalendar();
            birth.setTime(birthDate);
            Calendar today = new GregorianCalendar();
            today.setTime(getSystemDate());
            if (today.get(2) <= birth.get(2)) {
                if (today.get(2) == birth.get(2)) {
                    if (today.get(5) > birth.get(5)) {
                        factor = -1;
                    }
                } else {
                    factor = -1;
                }
            }

            return today.get(1) - birth.get(1) + factor;
        } catch (Exception var5) {
            return -1;
        }
    }

    private static Date addToDate(Date date, int days, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (days != 0) {
            calendar.add(5, days);
        }

        if (hours != 0) {
            calendar.add(10, hours);
        }

        if (minutes != 0) {
            calendar.add(12, minutes);
        }

        if (seconds != 0) {
            calendar.add(13, seconds);
        }

        return calendar.getTime();
    }

    private static boolean isBisiesto(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    public static FechaFormat getDiferenciaFechas(Date fechaInicial, Date fechaFinal) {
        String tiempoTotalEvento = calcularDiferencia(fechaInicial, fechaFinal);
        FechaFormat diferenciaFecha = new FechaFormat(Integer.valueOf(tiempoTotalEvento.split(":")[0]), Integer.valueOf(tiempoTotalEvento.split(":")[1]), Integer.valueOf(tiempoTotalEvento.split(":")[2]), Integer.valueOf(tiempoTotalEvento.split(":")[3]));
        return diferenciaFecha;
    }

    public static String calcularDiferencia(Date fechaInicial, Date fechaFinal) {
        String tiempoTotal = "00:00:00:00";
        if (fechaInicial != null && fechaFinal != null) {
            long milliseconds = fechaFinal.getTime() - fechaInicial.getTime();
            Integer seconds = (int)milliseconds / 1000;
            Integer hours = seconds / 3600;
            Integer minutes = seconds % 3600 / 60;
            Integer days = hours / 24;
            seconds = seconds / 3600 / 60 % 60;
            hours = hours - days * 24;
            tiempoTotal = StringUtils.leftPad(days.toString(), 2, '0').concat(":").concat(StringUtils.leftPad(hours.toString(), 2, '0')).concat(":").concat(StringUtils.leftPad(minutes.toString(), 2, '0')).concat(":").concat(StringUtils.leftPad(seconds.toString(), 2, '0'));
        }

        return tiempoTotal;
    }

    public static String calcularDiferencia(long milliseconds) {
        String tiempoTotal = "00:00:00:00";
        Integer seconds = (int)milliseconds / 1000;
        Integer hours = seconds / 3600;
        Integer minutes = seconds % 3600 / 60;
        Integer days = hours / 24;
        seconds = seconds / 3600 / 60 % 60;
        hours = hours - days * 24;
        tiempoTotal = StringUtils.leftPad(days.toString(), 2, '0').concat(":").concat(StringUtils.leftPad(hours.toString(), 2, '0')).concat(":").concat(StringUtils.leftPad(minutes.toString(), 2, '0')).concat(":").concat(StringUtils.leftPad(seconds.toString(), 2, '0'));
        return tiempoTotal;
    }

    public static FechaFormat obtenerFormatoFecha(long milliseconds) {
        Integer seconds = (int)milliseconds / 1000;
        Integer hours = seconds / 3600;
        Integer minutes = seconds % 3600 / 60;
        Integer days = hours / 24;
        seconds = seconds / 3600 / 60 % 60;
        hours = hours - days * 24;
        return new FechaFormat(days, hours, minutes, seconds);
    }

    public static String obtenerMesEnglish(String mes) {
        String mesEnglish = "";
        byte var3 = -1;
        switch(mes.hashCode()) {
            case 1537:
                if (mes.equals("01")) {
                    var3 = 0;
                }
                break;
            case 1538:
                if (mes.equals("02")) {
                    var3 = 1;
                }
                break;
            case 1539:
                if (mes.equals("03")) {
                    var3 = 2;
                }
                break;
            case 1540:
                if (mes.equals("04")) {
                    var3 = 3;
                }
                break;
            case 1541:
                if (mes.equals("05")) {
                    var3 = 4;
                }
                break;
            case 1542:
                if (mes.equals("06")) {
                    var3 = 5;
                }
                break;
            case 1543:
                if (mes.equals("07")) {
                    var3 = 6;
                }
                break;
            case 1544:
                if (mes.equals("08")) {
                    var3 = 7;
                }
                break;
            case 1545:
                if (mes.equals("09")) {
                    var3 = 8;
                }
            case 1546:
            case 1547:
            case 1548:
            case 1549:
            case 1550:
            case 1551:
            case 1552:
            case 1553:
            case 1554:
            case 1555:
            case 1556:
            case 1557:
            case 1558:
            case 1559:
            case 1560:
            case 1561:
            case 1562:
            case 1563:
            case 1564:
            case 1565:
            case 1566:
            default:
                break;
            case 1567:
                if (mes.equals("10")) {
                    var3 = 9;
                }
                break;
            case 1568:
                if (mes.equals("11")) {
                    var3 = 10;
                }
                break;
            case 1569:
                if (mes.equals("12")) {
                    var3 = 11;
                }
        }

        switch(var3) {
            case 0:
                mesEnglish = "JANUARY";
                break;
            case 1:
                mesEnglish = "FEBRUARY";
                break;
            case 2:
                mesEnglish = "MARCH";
                break;
            case 3:
                mesEnglish = "APRIL";
                break;
            case 4:
                mesEnglish = "MAY";
                break;
            case 5:
                mesEnglish = "JUNE";
                break;
            case 6:
                mesEnglish = "JULY";
                break;
            case 7:
                mesEnglish = "AUGUST";
                break;
            case 8:
                mesEnglish = "SEPTEMBER";
                break;
            case 9:
                mesEnglish = "OCTOBER";
                break;
            case 10:
                mesEnglish = "NOVEMBER";
                break;
            case 11:
                mesEnglish = "DECEMBER";
        }

        return mesEnglish;
    }
}
