package br.pucminas.matriculas.model;

import java.time.LocalDateTime;

public class Matricula {
    private Aluno aluno;
    private Disciplina disciplina;
    private TipoMatricula tipo;
    private LocalDateTime data;
    private boolean ativa;

    public Matricula(Aluno aluno, Disciplina disciplina, TipoMatricula tipo) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.data = LocalDateTime.now();
        this.ativa = false;
    }

    public void confirmar() {
        this.ativa = true;
    }

    public void cancelar() {
        this.ativa = false;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
