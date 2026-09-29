package br.pucminas.matriculas.service;

import java.time.LocalDateTime;
import java.util.List;

import br.pucminas.matriculas.exception.RegraNegocioException;
import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;

// Executavel diretamente com java, sem dependencias de testes externas.
public class RegrasCadastroMatriculaTest {
    public static void main(String[] args) {
        SistemaMatriculas sistema = new SistemaMatriculas();
        Curso curso = new Curso("C1", "Sistemas de Informacao", 3000);
        sistema.cadastrarCurso(curso);
        rejeitar(() -> sistema.cadastrarCurso(new Curso("C2", "Sistemas de Informacao", 3000)),
                "Ja existe um curso com esse nome");
        rejeitar(() -> sistema.cadastrarCurso(new Curso("C2", "  sistemas de INFORMACAO  ", 3000)),
                "Ja existe um curso com esse nome");
        rejeitar(() -> sistema.cadastrarCurso(new Curso("c1", "Outro curso", 3000)),
                "Curso ja cadastrado");
        verificar(sistema.getCursos().size() == 1, "Cadastro rejeitado alterou cursos");
        sistema.cadastrarCurso(new Curso("C2", "Engenharia", 3000));
        verificar(sistema.getCursos().size() == 2, "Curso distinto nao cadastrado");

        sistema.setPeriodoMatricula(new PeriodoMatricula(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)));
        Aluno aluno = new Aluno("A1", "Aluno", "aluno@sistema", "123", "A1", curso);
        Disciplina comProfessor = new Disciplina("D1", "Projeto", 4, 80);
        Disciplina semProfessor = new Disciplina("D2", "Banco", 4, 80);
        Professor professor = new Professor("P1", "Professor", "prof@sistema", "123", "P1");
        comProfessor.setProfessor(professor);
        curso.adicionarDisciplina(comProfessor);
        curso.adicionarDisciplina(semProfessor);

        rejeitar(() -> sistema.efetuarMatricula(aluno, List.of(semProfessor), List.of()),
                "Disciplina sem professor alocado");
        rejeitar(() -> sistema.efetuarMatricula(aluno, List.of(comProfessor), List.of(semProfessor)),
                "Disciplina sem professor alocado");
        verificar(aluno.getMatriculas().isEmpty(), "Matricula parcial criada apos rejeicao");
        verificar(comProfessor.getAlunosMatriculados().isEmpty(), "Turma alterada apos rejeicao");
        verificar(semProfessor.getAlunosMatriculados().isEmpty(), "Aluno incluido sem professor");
        verificar(sistema.efetuarMatricula(aluno, List.of(comProfessor), List.of()).size() == 1,
                "Matricula com professor falhou");
        semProfessor.setProfessor(professor);
        verificar(sistema.efetuarMatricula(aluno, List.of(), List.of(semProfessor)).size() == 1,
                "Matricula apos alocacao de professor falhou");
        System.out.println("OK: cursos duplicados e matriculas com/sem professor");
    }

    private static void rejeitar(Runnable acao, String mensagem) {
        try {
            acao.run();
        } catch (RegraNegocioException excecao) {
            verificar(mensagem.equals(excecao.getMessage()), "Erro inesperado: " + excecao.getMessage());
            return;
        }
        throw new AssertionError("Operacao deveria ter sido rejeitada: " + mensagem);
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
