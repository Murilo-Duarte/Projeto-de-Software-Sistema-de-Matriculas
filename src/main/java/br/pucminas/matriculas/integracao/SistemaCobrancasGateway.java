package br.pucminas.matriculas.integracao;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Matricula;

import java.util.List;

public interface SistemaCobrancasGateway {
    void notificarMatricula(Aluno aluno, List<Matricula> matriculas);

    void notificarCancelamento(Matricula matricula);
}
