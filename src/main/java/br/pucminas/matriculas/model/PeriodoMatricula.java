package br.pucminas.matriculas.model;

import java.time.LocalDateTime;

public class PeriodoMatricula {
    private LocalDateTime inicio;
    private LocalDateTime fim;

    public PeriodoMatricula(LocalDateTime inicio, LocalDateTime fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean estaAberto(LocalDateTime dataHora) {
        return dataHora != null
                && !dataHora.isBefore(inicio)
                && !dataHora.isAfter(fim);
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }
}
