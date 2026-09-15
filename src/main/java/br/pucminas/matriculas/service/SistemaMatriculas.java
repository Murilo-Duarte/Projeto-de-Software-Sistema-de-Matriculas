package br.pucminas.matriculas.service;

import br.pucminas.matriculas.exception.RegraNegocioException;
import br.pucminas.matriculas.integracao.SistemaCobrancasGateway;
import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.CurriculoSemestre;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.TipoMatricula;
import br.pucminas.matriculas.model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SistemaMatriculas {
    private final List<Curso> cursos;
    private final List<Aluno> alunos;
    private final List<Professor> professores;
    private PeriodoMatricula periodoMatricula;
    private SistemaCobrancasGateway sistemaCobrancas;

    public SistemaMatriculas() {
        this.cursos = new ArrayList<>();
        this.alunos = new ArrayList<>();
        this.professores = new ArrayList<>();
    }

    public Usuario realizarLogin(String email, String senha) {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(alunos);
        usuarios.addAll(professores);

        return usuarios.stream()
                .filter(usuario -> usuario.getEmail().equals(email) && usuario.autenticar(senha))
                .findFirst()
                .orElse(null);
    }

    public CurriculoSemestre gerarCurriculoSemestre(String semestre) {
        return new CurriculoSemestre(semestre);
    }

    public List<Matricula> efetuarMatricula(
            Aluno aluno,
            List<Disciplina> obrigatorias,
            List<Disciplina> optativas
    ) {
        validarQuantidadeDisciplinas(obrigatorias, optativas);
        List<Matricula> matriculasGeradas = new ArrayList<>();

        for (Disciplina disciplina : obrigatorias) {
            matriculasGeradas.add(new Matricula(aluno, disciplina, TipoMatricula.OBRIGATORIA));
        }

        for (Disciplina disciplina : optativas) {
            matriculasGeradas.add(new Matricula(aluno, disciplina, TipoMatricula.OPTATIVA));
        }

        if (sistemaCobrancas != null) {
            sistemaCobrancas.notificarMatricula(aluno, matriculasGeradas);
        }

        return matriculasGeradas;
    }

    public void cancelarMatricula(Matricula matricula) {
        if (matricula != null) {
            matricula.cancelar();
            if (sistemaCobrancas != null) {
                sistemaCobrancas.notificarCancelamento(matricula);
            }
        }
    }

    public void encerrarPeriodoMatriculas() {
        // Stub modelado para a Sprint 2.
    }

    public List<Aluno> listarAlunosMatriculados(Professor professor, Disciplina disciplina) {
        if (disciplina == null) {
            return List.of();
        }
        return disciplina.getAlunosMatriculados();
    }

    public boolean periodoEstaAberto() {
        return periodoMatricula != null && periodoMatricula.estaAberto(LocalDateTime.now());
    }

    private void validarQuantidadeDisciplinas(List<Disciplina> obrigatorias, List<Disciplina> optativas) {
        if (obrigatorias != null && obrigatorias.size() > 4) {
            throw new RegraNegocioException("Aluno pode escolher no maximo 4 disciplinas obrigatorias.");
        }
        if (optativas != null && optativas.size() > 2) {
            throw new RegraNegocioException("Aluno pode escolher no maximo 2 disciplinas optativas.");
        }
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public PeriodoMatricula getPeriodoMatricula() {
        return periodoMatricula;
    }

    public void setPeriodoMatricula(PeriodoMatricula periodoMatricula) {
        this.periodoMatricula = periodoMatricula;
    }

    public SistemaCobrancasGateway getSistemaCobrancas() {
        return sistemaCobrancas;
    }

    public void setSistemaCobrancas(SistemaCobrancasGateway sistemaCobrancas) {
        this.sistemaCobrancas = sistemaCobrancas;
    }
}
