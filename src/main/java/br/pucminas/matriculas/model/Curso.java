package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String codigo;
    private String nome;
    private int totalCreditos;
    private final List<Disciplina> disciplinas;

    public Curso(String codigo, String nome, int totalCreditos) {
        this.codigo = codigo;
        this.nome = nome;
        this.totalCreditos = totalCreditos;
        this.disciplinas = new ArrayList<>();
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina != null) {
            disciplinas.add(disciplina);
        }
    }

    public void removerDisciplina(Disciplina disciplina) {
        disciplinas.remove(disciplina);
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

    public int getTotalCreditos() {
        return totalCreditos;
    }

    public void setTotalCreditos(int totalCreditos) {
        this.totalCreditos = totalCreditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
