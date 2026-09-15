package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {
    private String matricula;
    private Curso curso;
    private final List<Matricula> matriculas;
    private final List<Disciplina> historico;

    public Aluno(String id, String nome, String email, String senha, String matricula, Curso curso) {
        super(id, nome, email, senha);
        this.matricula = matricula;
        this.curso = curso;
        this.matriculas = new ArrayList<>();
        this.historico = new ArrayList<>();
    }

    public void solicitarMatricula(List<Disciplina> disciplinasObrigatorias, List<Disciplina> disciplinasOptativas) {
        // Stub modelado para a Sprint 2.
    }

    public void cancelarMatricula(Matricula matricula) {
        // Stub modelado para a Sprint 2.
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public List<Disciplina> getHistorico() {
        return historico;
    }
}
