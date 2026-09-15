package br.pucminas.matriculas.model;

import java.time.LocalTime;

public class Horario {
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Horario(String diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    public boolean conflitaCom(Horario outro) {
        if (outro == null || !diaSemana.equalsIgnoreCase(outro.diaSemana)) {
            return false;
        }
        return horaInicio.isBefore(outro.horaFim) && horaFim.isAfter(outro.horaInicio);
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
}
