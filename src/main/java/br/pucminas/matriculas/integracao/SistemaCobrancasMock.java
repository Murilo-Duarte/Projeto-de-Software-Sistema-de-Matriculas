package br.pucminas.matriculas.integracao;

import java.util.List;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Matricula;

public class SistemaCobrancasMock implements SistemaCobrancasGateway {
    @Override
    public void notificarMatricula(Aluno aluno, List<Matricula> matriculas) {
        System.out.println("Cobranca simulada: matricula realizada para " + aluno.getNome()
                + " (" + matriculas.size() + " disciplina(s))");
    }

    @Override
    public void notificarCancelamento(Matricula matricula) {
        System.out.println("Cobranca simulada: cancelamento realizado da disciplina "
                + matricula.getDisciplina().getCodigo());
    }
}