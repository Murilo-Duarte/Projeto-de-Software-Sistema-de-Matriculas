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
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.TipoMatricula;
import br.pucminas.matriculas.model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SistemaMatriculas {
    private final List<Curso> cursos;
    private final List<Aluno> alunos;
    private final List<Professor> professores;
    private final List<Secretaria> secretarias;
    private PeriodoMatricula periodoMatricula;
    private SistemaCobrancasGateway sistemaCobrancas;

    public SistemaMatriculas() {
        this.cursos = new ArrayList<>();
        this.alunos = new ArrayList<>();
        this.professores = new ArrayList<>();
        this.secretarias = new ArrayList<>();
    }

    public Usuario realizarLogin(String email, String senha) {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(alunos);
        usuarios.addAll(professores);
        usuarios.addAll(secretarias);

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
        if (!periodoEstaAberto()) {
            throw new RegraNegocioException("Periodo de matricula fechado");
        }

        if (aluno == null) {
            throw new RegraNegocioException("Aluno invalido");
        }

        if (obrigatorias == null) {
            obrigatorias = List.of();
        }

        if (optativas == null) {
            optativas = List.of();
        }

        validarQuantidadeDisciplinas(obrigatorias, optativas);
        validarDisciplinasSelecionadas(aluno, obrigatorias, optativas);

        List<Matricula> matriculasGeradas = new ArrayList<>();

        for (Disciplina disciplina : obrigatorias) {
            matriculasGeradas.add(matricular(aluno, disciplina, TipoMatricula.OBRIGATORIA));
        }

        for (Disciplina disciplina : optativas) {
            matriculasGeradas.add(matricular(aluno, disciplina, TipoMatricula.OPTATIVA));
        }

        if (sistemaCobrancas != null) {
            sistemaCobrancas.notificarMatricula(aluno, matriculasGeradas);
        }

        return matriculasGeradas;
    }

    public void cancelarMatricula(Matricula matricula) {
        if (matricula != null) {
            if (!periodoEstaAberto()) {
                throw new RegraNegocioException("Periodo de matricula fechado");
            }

            matricula.cancelar();
            matricula.getAluno().getMatriculas().remove(matricula);
            matricula.getDisciplina().getAlunosMatriculados().remove(matricula.getAluno());

            if (sistemaCobrancas != null) {
                sistemaCobrancas.notificarCancelamento(matricula);
            }
        }
    }

    public void encerrarPeriodoMatriculas() {
        for (Curso curso : cursos) {
            for (Disciplina disciplina : curso.getDisciplinas()) {
                if (disciplina.possuiMinimoAlunos()) {
                    disciplina.ativar();
                } else {
                    disciplina.cancelar();
                }
            }
        }
    }

    public List<Aluno> listarAlunosMatriculados(Professor professor, Disciplina disciplina) {
        if (professor == null || disciplina == null || !professor.getDisciplinas().contains(disciplina)) {
            return List.of();
        }
        return disciplina.getAlunosMatriculados();
    }

    public boolean periodoEstaAberto() {
        return periodoMatricula != null && periodoMatricula.estaAberto(LocalDateTime.now());
    }

    private Matricula matricular(Aluno aluno, Disciplina disciplina, TipoMatricula tipo) {
        Matricula matricula = new Matricula(aluno, disciplina, tipo);
        matricula.confirmar();
        aluno.getMatriculas().add(matricula);
        disciplina.getAlunosMatriculados().add(aluno);
        return matricula;
    }

    private void validarQuantidadeDisciplinas(List<Disciplina> obrigatorias, List<Disciplina> optativas) {
        if (obrigatorias != null && obrigatorias.size() > 4) {
            throw new RegraNegocioException("Aluno pode escolher no maximo 4 disciplinas obrigatorias");
        }
        if (optativas != null && optativas.size() > 2) {
            throw new RegraNegocioException("Aluno pode escolher no maximo 2 disciplinas optativas");
        }
    }

    private void validarDisciplinasSelecionadas(
            Aluno aluno,
            List<Disciplina> obrigatorias,
            List<Disciplina> optativas
    ) {
        List<Disciplina> selecionadas = new ArrayList<>();
        selecionadas.addAll(obrigatorias);
        selecionadas.addAll(optativas);

        if (selecionadas.isEmpty()) {
            throw new RegraNegocioException("Selecione pelo menos uma disciplina");
        }

        for (Disciplina disciplina : selecionadas) {
            if (disciplina == null) {
                throw new RegraNegocioException("Disciplina invalida");
            }

            if (selecionadas.indexOf(disciplina) != selecionadas.lastIndexOf(disciplina)) {
                throw new RegraNegocioException("Disciplina repetida");
            }

            if (!disciplina.temVaga()) {
                throw new RegraNegocioException("Disciplina sem vaga");
            }

            if (disciplina.getAlunosMatriculados().contains(aluno)) {
                throw new RegraNegocioException("Aluno ja matriculado na disciplina");
            }

            for (Matricula matricula : aluno.getMatriculas()) {
                if (matricula.isAtiva() && matricula.getDisciplina() == disciplina) {
                    throw new RegraNegocioException("Aluno ja matriculado na disciplina");
                }
            }
        }

        for (int i = 0; i < selecionadas.size(); i++) {
            Disciplina primeira = selecionadas.get(i);
            for (int j = i + 1; j < selecionadas.size(); j++) {
                Disciplina segunda = selecionadas.get(j);
                if (primeira.getHorario() != null
                        && segunda.getHorario() != null
                        && primeira.getHorario().conflitaCom(segunda.getHorario())) {
                    throw new RegraNegocioException("Conflito de horario");
                }
            }
        }

        for (Matricula matricula : aluno.getMatriculas()) {
            if (!matricula.isAtiva() || matricula.getDisciplina().getHorario() == null) {
                continue;
            }

            for (Disciplina disciplina : selecionadas) {
                if (disciplina.getHorario() != null
                        && matricula.getDisciplina().getHorario().conflitaCom(disciplina.getHorario())) {
                    throw new RegraNegocioException("Conflito de horario");
                }
            }
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

    public List<Secretaria> getSecretarias() {
        return secretarias;
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
