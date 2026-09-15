package br.pucminas.matriculas.model;

public class Secretaria extends Usuario {
    private String setor;

    public Secretaria(String id, String nome, String email, String senha, String setor) {
        super(id, nome, email, senha);
        this.setor = setor;
    }

    public CurriculoSemestre gerarCurriculo(String semestre) {
        return new CurriculoSemestre(semestre);
    }

    public void cadastrarCurso(Curso curso) {
        // Stub modelado para a Sprint 2.
    }

    public void cadastrarDisciplina(Disciplina disciplina) {
        // Stub modelado para a Sprint 2.
    }

    public void alocarProfessor(Professor professor, Disciplina disciplina) {
        if (disciplina != null) {
            disciplina.setProfessor(professor);
        }
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
}
