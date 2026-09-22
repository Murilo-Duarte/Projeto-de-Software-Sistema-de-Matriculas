package br.pucminas.matriculas.persistencia;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Horario;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.StatusDisciplina;
import br.pucminas.matriculas.model.TipoMatricula;
import br.pucminas.matriculas.service.SistemaMatriculas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenciaArquivo {
    private final Path arquivo;

    public PersistenciaArquivo(String caminhoArquivo) {
        this.arquivo = Path.of(caminhoArquivo);
    }

    public boolean carregar(SistemaMatriculas sistema) {
        if (!Files.exists(arquivo)) {
            return false;
        }

        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);

            if (linhas.isEmpty()) {
                return false;
            }

            sistema.getCursos().clear();
            sistema.getAlunos().clear();
            sistema.getProfessores().clear();
            sistema.getSecretarias().clear();

            Map<String, Curso> cursosPorCodigo = new HashMap<>();
            Map<String, Aluno> alunosPorMatricula = new HashMap<>();
            Map<String, Professor> professoresPorRegistro = new HashMap<>();
            Map<String, Disciplina> disciplinasPorCodigo = new HashMap<>();
            List<String[]> disciplinasPendentes = new ArrayList<>();
            List<String[]> matriculasPendentes = new ArrayList<>();

            for (String linha : linhas) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split("\\|", -1);
                String tipo = campos[0];

                switch (tipo) {
                    case "PERIODO" -> carregarPeriodo(sistema, campos);
                    case "CURSO" -> carregarCurso(sistema, cursosPorCodigo, campos);
                    case "SECRETARIA" -> carregarSecretaria(sistema, campos);
                    case "PROFESSOR" -> carregarProfessor(sistema, professoresPorRegistro, campos);
                    case "ALUNO" -> carregarAluno(sistema, cursosPorCodigo, alunosPorMatricula, campos);
                    case "DISCIPLINA" -> disciplinasPendentes.add(campos);
                    case "MATRICULA" -> matriculasPendentes.add(campos);
                    default -> {
                    }
                }
            }

            for (String[] campos : disciplinasPendentes) {
                carregarDisciplina(cursosPorCodigo, professoresPorRegistro, disciplinasPorCodigo, campos);
            }

            for (String[] campos : matriculasPendentes) {
                carregarMatricula(alunosPorMatricula, disciplinasPorCodigo, campos);
            }

            return true;
        } catch (IOException | RuntimeException excecao) {
            return false;
        }
    }

    public void salvar(SistemaMatriculas sistema) {
        try {
            Path pasta = arquivo.getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }

            List<String> linhas = new ArrayList<>();

            if (sistema.getPeriodoMatricula() != null) {
                linhas.add("PERIODO"
                        + "|" + sistema.getPeriodoMatricula().getInicio()
                        + "|" + sistema.getPeriodoMatricula().getFim());
            }

            for (Secretaria secretaria : sistema.getSecretarias()) {
                linhas.add("SECRETARIA"
                        + "|" + limpar(secretaria.getId())
                        + "|" + limpar(secretaria.getNome())
                        + "|" + limpar(secretaria.getEmail())
                        + "|" + limpar(secretaria.getSenha())
                        + "|" + limpar(secretaria.getSetor()));
            }

            for (Curso curso : sistema.getCursos()) {
                linhas.add("CURSO"
                        + "|" + limpar(curso.getCodigo())
                        + "|" + limpar(curso.getNome())
                        + "|" + curso.getTotalCreditos());
            }

            for (Professor professor : sistema.getProfessores()) {
                linhas.add("PROFESSOR"
                        + "|" + limpar(professor.getId())
                        + "|" + limpar(professor.getNome())
                        + "|" + limpar(professor.getEmail())
                        + "|" + limpar(professor.getSenha())
                        + "|" + limpar(professor.getRegistro()));
            }

            for (Aluno aluno : sistema.getAlunos()) {
                String codigoCurso = aluno.getCurso() == null ? "" : aluno.getCurso().getCodigo();
                linhas.add("ALUNO"
                        + "|" + limpar(aluno.getId())
                        + "|" + limpar(aluno.getNome())
                        + "|" + limpar(aluno.getEmail())
                        + "|" + limpar(aluno.getSenha())
                        + "|" + limpar(aluno.getMatricula())
                        + "|" + limpar(codigoCurso));
            }

            for (Curso curso : sistema.getCursos()) {
                for (Disciplina disciplina : curso.getDisciplinas()) {
                    Horario horario = disciplina.getHorario();
                    String dia = horario == null ? "" : horario.getDiaSemana();
                    String inicio = horario == null ? "" : horario.getHoraInicio().toString();
                    String fim = horario == null ? "" : horario.getHoraFim().toString();
                    String registroProfessor = disciplina.getProfessor() == null ? "" : disciplina.getProfessor().getRegistro();

                    linhas.add("DISCIPLINA"
                            + "|" + limpar(curso.getCodigo())
                            + "|" + limpar(disciplina.getCodigo())
                            + "|" + limpar(disciplina.getNome())
                            + "|" + disciplina.getCreditos()
                            + "|" + disciplina.getCargaHoraria()
                            + "|" + limpar(dia)
                            + "|" + limpar(inicio)
                            + "|" + limpar(fim)
                            + "|" + limpar(registroProfessor)
                            + "|" + disciplina.getStatus());
                }
            }

            for (Aluno aluno : sistema.getAlunos()) {
                for (Matricula matricula : aluno.getMatriculas()) {
                    linhas.add("MATRICULA"
                            + "|" + limpar(aluno.getMatricula())
                            + "|" + limpar(matricula.getDisciplina().getCodigo())
                            + "|" + matricula.getTipo()
                            + "|" + matricula.getData()
                            + "|" + matricula.isAtiva());
                }
            }

            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException excecao) {
            throw new IllegalStateException("Erro ao salvar dados", excecao);
        }
    }

    public Path getArquivo() {
        return arquivo;
    }

    private void carregarPeriodo(SistemaMatriculas sistema, String[] campos) {
        if (campos.length < 3 || campos[1].isBlank() || campos[2].isBlank()) {
            return;
        }

        sistema.setPeriodoMatricula(new PeriodoMatricula(
                LocalDateTime.parse(campos[1]),
                LocalDateTime.parse(campos[2])
        ));
    }

    private void carregarCurso(SistemaMatriculas sistema, Map<String, Curso> cursosPorCodigo, String[] campos) {
        if (campos.length < 4) {
            return;
        }

        Curso curso = new Curso(campos[1], campos[2], Integer.parseInt(campos[3]));
        sistema.getCursos().add(curso);
        cursosPorCodigo.put(curso.getCodigo(), curso);
    }

    private void carregarSecretaria(SistemaMatriculas sistema, String[] campos) {
        if (campos.length < 6) {
            return;
        }

        sistema.getSecretarias().add(new Secretaria(campos[1], campos[2], campos[3], campos[4], campos[5]));
    }

    private void carregarProfessor(
            SistemaMatriculas sistema,
            Map<String, Professor> professoresPorRegistro,
            String[] campos
    ) {
        if (campos.length < 6) {
            return;
        }

        Professor professor = new Professor(campos[1], campos[2], campos[3], campos[4], campos[5]);
        sistema.getProfessores().add(professor);
        professoresPorRegistro.put(professor.getRegistro(), professor);
    }

    private void carregarAluno(
            SistemaMatriculas sistema,
            Map<String, Curso> cursosPorCodigo,
            Map<String, Aluno> alunosPorMatricula,
            String[] campos
    ) {
        if (campos.length < 7) {
            return;
        }

        Curso curso = cursosPorCodigo.get(campos[6]);
        Aluno aluno = new Aluno(campos[1], campos[2], campos[3], campos[4], campos[5], curso);
        sistema.getAlunos().add(aluno);
        alunosPorMatricula.put(aluno.getMatricula(), aluno);
    }

    private void carregarDisciplina(
            Map<String, Curso> cursosPorCodigo,
            Map<String, Professor> professoresPorRegistro,
            Map<String, Disciplina> disciplinasPorCodigo,
            String[] campos
    ) {
        if (campos.length < 11) {
            return;
        }

        Curso curso = cursosPorCodigo.get(campos[1]);
        if (curso == null) {
            return;
        }

        Disciplina disciplina = new Disciplina(campos[2], campos[3], Integer.parseInt(campos[4]), Integer.parseInt(campos[5]));

        if (!campos[6].isBlank() && !campos[7].isBlank() && !campos[8].isBlank()) {
            disciplina.setHorario(new Horario(campos[6], LocalTime.parse(campos[7]), LocalTime.parse(campos[8])));
        }

        Professor professor = professoresPorRegistro.get(campos[9]);
        if (professor != null) {
            disciplina.setProfessor(professor);
            professor.getDisciplinas().add(disciplina);
        }

        disciplina.setStatus(StatusDisciplina.valueOf(campos[10]));
        curso.adicionarDisciplina(disciplina);
        disciplinasPorCodigo.put(disciplina.getCodigo(), disciplina);
    }

    private void carregarMatricula(
            Map<String, Aluno> alunosPorMatricula,
            Map<String, Disciplina> disciplinasPorCodigo,
            String[] campos
    ) {
        if (campos.length < 6) {
            return;
        }

        Aluno aluno = alunosPorMatricula.get(campos[1]);
        Disciplina disciplina = disciplinasPorCodigo.get(campos[2]);

        if (aluno == null || disciplina == null) {
            return;
        }

        Matricula matricula = new Matricula(aluno, disciplina, TipoMatricula.valueOf(campos[3]));
        matricula.setData(LocalDateTime.parse(campos[4]));

        if (Boolean.parseBoolean(campos[5])) {
            matricula.confirmar();
            disciplina.getAlunosMatriculados().add(aluno);
        } else {
            matricula.cancelar();
        }

        aluno.getMatriculas().add(matricula);
    }

    private String limpar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("|", " ");
    }
}
