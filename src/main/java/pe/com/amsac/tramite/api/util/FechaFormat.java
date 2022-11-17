package pe.com.amsac.tramite.api.util;

import java.math.BigDecimal;

public class FechaFormat {
    private Integer dia;
    private Integer hora;
    private Integer minuto;
    private Integer segundo;
    private Integer totalHoras;
    private Integer totalMinutos;
    private Integer totalSegundos;
    private BigDecimal totalHorasMinutos;

    public FechaFormat(Integer dia, Integer hora, Integer minuto, Integer segundo) {
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.totalHoras = dia * 24 + hora;
        this.totalMinutos = this.totalHoras * 60 + minuto;
        this.totalSegundos = this.totalMinutos * 60 + segundo;
        this.totalHorasMinutos = new BigDecimal((double)this.totalMinutos / 60.0D);
    }

    public Integer getDia() {
        return this.dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getHora() {
        return this.hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public Integer getMinuto() {
        return this.minuto;
    }

    public void setMinuto(Integer minuto) {
        this.minuto = minuto;
    }

    public Integer getSegundo() {
        return this.segundo;
    }

    public void setSegundo(Integer segundo) {
        this.segundo = segundo;
    }

    public Integer getTotalHoras() {
        return this.totalHoras;
    }

    public void setTotalHoras(Integer totalHoras) {
        this.totalHoras = totalHoras;
    }

    public Integer getTotalMinutos() {
        return this.totalMinutos;
    }

    public void setTotalMinutos(Integer totalMinutos) {
        this.totalMinutos = totalMinutos;
    }

    public Integer getTotalSegundos() {
        return this.totalSegundos;
    }

    public void setTotalSegundos(Integer totalSegundos) {
        this.totalSegundos = totalSegundos;
    }

    public BigDecimal getTotalHorasMinutos() {
        return this.totalHorasMinutos;
    }

    public void setTotalHorasMinutos(BigDecimal totalHorasMinutos) {
        this.totalHorasMinutos = totalHorasMinutos;
    }
}
