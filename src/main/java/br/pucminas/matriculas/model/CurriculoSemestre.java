package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class CurriculoSemestre {
    private String semestre;
    private final List<Curso> cursos;
    private final List<Disciplina> disciplinasOfertadas;

    public CurriculoSemestre(String semestre) {
        this.semestre = semestre;
        this.cursos = new ArrayList<>();
        this.disciplinasOfertadas = new ArrayList<>();
    }

    public void adicionarCurso(Curso curso) {
        if (curso != null) {
            cursos.add(curso);
        }
    }

    public void ofertarDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplinasOfertadas.add(disciplina);
        }
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Disciplina> getDisciplinasOfertadas() {
        return disciplinasOfertadas;
    }
}
