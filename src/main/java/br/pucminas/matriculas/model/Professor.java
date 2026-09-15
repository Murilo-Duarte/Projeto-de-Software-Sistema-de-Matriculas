package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {
    private String registro;
    private final List<Disciplina> disciplinas;

    public Professor(String id, String nome, String email, String senha, String registro) {
        super(id, nome, email, senha);
        this.registro = registro;
        this.disciplinas = new ArrayList<>();
    }

    public List<Aluno> consultarAlunosMatriculados(Disciplina disciplina) {
        if (disciplina == null) {
            return List.of();
        }
        return disciplina.getAlunosMatriculados();
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
