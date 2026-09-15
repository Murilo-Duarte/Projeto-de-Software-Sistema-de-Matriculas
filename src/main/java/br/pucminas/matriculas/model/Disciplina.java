package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {
    public static final int MINIMO_ALUNOS = 3;
    public static final int MAXIMO_ALUNOS = 60;

    private String codigo;
    private String nome;
    private int creditos;
    private int cargaHoraria;
    private Professor professor;
    private Horario horario;
    private StatusDisciplina status;
    private final List<Aluno> alunosMatriculados;

    public Disciplina(String codigo, String nome, int creditos, int cargaHoraria) {
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.cargaHoraria = cargaHoraria;
        this.status = StatusDisciplina.PLANEJADA;
        this.alunosMatriculados = new ArrayList<>();
    }

    public boolean temVaga() {
        return alunosMatriculados.size() < MAXIMO_ALUNOS;
    }

    public boolean possuiMinimoAlunos() {
        return alunosMatriculados.size() >= MINIMO_ALUNOS;
    }

    public void ativar() {
        this.status = StatusDisciplina.ATIVA;
    }

    public void cancelar() {
        this.status = StatusDisciplina.CANCELADA;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
    }

    public List<Aluno> getAlunosMatriculados() {
        return alunosMatriculados;
    }
}
