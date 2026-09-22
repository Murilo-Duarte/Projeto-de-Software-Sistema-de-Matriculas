package br.pucminas.matriculas;

import br.pucminas.matriculas.exception.RegraNegocioException;
import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Curso;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Horario;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.Usuario;
import br.pucminas.matriculas.persistencia.PersistenciaArquivo;
import br.pucminas.matriculas.service.SistemaMatriculas;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Aplicacao {
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner entrada;
    private final SistemaMatriculas sistema;
    private final PersistenciaArquivo persistencia;

    public Aplicacao() {
        this.entrada = new Scanner(System.in);
        this.sistema = new SistemaMatriculas();
        this.persistencia = new PersistenciaArquivo("dados/sistema.txt");

        if (!persistencia.carregar(sistema)) {
            carregarDadosIniciais();
            salvarDados();
        }
    }

    public static void main(String[] args) {
        new Aplicacao().executar();
    }

    private void executar() {
        boolean executando = true;

        while (executando) {
            mostrarTitulo("Sistema de Matriculas");
            System.out.println("1 Login");
            System.out.println("2 Sair");

            String opcao = lerTexto("Opcao");

            switch (opcao) {
                case "1" -> realizarLogin();
                case "2" -> {
                    System.out.println("Sistema encerrado");
                    executando = false;
                }
                default -> mostrarErro("Opcao invalida");
            }
        }
    }

    private void realizarLogin() {
        String email = lerTexto("Email");
        String senha = lerTexto("Senha");

        Usuario usuario = sistema.realizarLogin(email, senha);

        if (usuario == null) {
            mostrarErro("Email ou senha invalido");
            return;
        }

        System.out.println("Login realizado");

        if (usuario instanceof Secretaria secretaria) {
            menuSecretaria(secretaria);
        } else if (usuario instanceof Aluno aluno) {
            menuAluno(aluno);
        } else if (usuario instanceof Professor professor) {
            menuProfessor(professor);
        }
    }

    private void menuSecretaria(Secretaria secretaria) {
        boolean logado = true;

        while (logado) {
            mostrarTitulo("Menu Secretaria");
            System.out.println("1 Listar dados");
            System.out.println("2 Cadastrar curso");
            System.out.println("3 Cadastrar disciplina");
            System.out.println("4 Cadastrar aluno");
            System.out.println("5 Cadastrar professor");
            System.out.println("6 Alocar professor");
            System.out.println("7 Definir periodo de matricula");
            System.out.println("8 Encerrar periodo");
            System.out.println("9 Relatorio de turmas");
            System.out.println("0 Logout");

            String opcao = lerTexto("Opcao");

            try {
                switch (opcao) {
                    case "1" -> listarDados();
                    case "2" -> cadastrarCurso();
                    case "3" -> cadastrarDisciplina();
                    case "4" -> cadastrarAluno();
                    case "5" -> cadastrarProfessor();
                    case "6" -> alocarProfessor(secretaria);
                    case "7" -> definirPeriodoMatricula();
                    case "8" -> encerrarPeriodo();
                    case "9" -> relatorioTurmas();
                    case "0" -> logado = false;
                    default -> mostrarErro("Opcao invalida");
                }
            } catch (RegraNegocioException excecao) {
                mostrarErro(excecao.getMessage());
            }
        }
    }

    private void menuAluno(Aluno aluno) {
        boolean logado = true;

        while (logado) {
            mostrarTitulo("Menu Aluno");
            System.out.println("1 Listar disciplinas");
            System.out.println("2 Efetuar matricula");
            System.out.println("3 Minhas matriculas");
            System.out.println("4 Cancelar matricula");
            System.out.println("0 Logout");

            String opcao = lerTexto("Opcao");

            try {
                switch (opcao) {
                    case "1" -> listarDisciplinas();
                    case "2" -> efetuarMatricula(aluno);
                    case "3" -> listarMatriculasAluno(aluno);
                    case "4" -> cancelarMatricula(aluno);
                    case "0" -> logado = false;
                    default -> mostrarErro("Opcao invalida");
                }
            } catch (RegraNegocioException excecao) {
                mostrarErro(excecao.getMessage());
            }
        }
    }

    private void menuProfessor(Professor professor) {
        boolean logado = true;

        while (logado) {
            mostrarTitulo("Menu Professor");
            System.out.println("1 Minhas disciplinas");
            System.out.println("2 Consultar alunos");
            System.out.println("0 Logout");

            String opcao = lerTexto("Opcao");

            switch (opcao) {
                case "1" -> listarDisciplinasProfessor(professor);
                case "2" -> consultarAlunosProfessor(professor);
                case "0" -> logado = false;
                default -> mostrarErro("Opcao invalida");
            }
        }
    }

    private void listarDados() {
        mostrarTitulo("Cursos");
        if (sistema.getCursos().isEmpty()) {
            System.out.println("Nenhum curso cadastrado");
        }
        for (Curso curso : sistema.getCursos()) {
            System.out.println(curso.getCodigo() + " " + curso.getNome() + " creditos " + curso.getTotalCreditos());
        }

        mostrarTitulo("Alunos");
        if (sistema.getAlunos().isEmpty()) {
            System.out.println("Nenhum aluno cadastrado");
        }
        for (Aluno aluno : sistema.getAlunos()) {
            System.out.println(aluno.getMatricula() + " " + aluno.getNome() + " " + aluno.getEmail());
        }

        mostrarTitulo("Professores");
        if (sistema.getProfessores().isEmpty()) {
            System.out.println("Nenhum professor cadastrado");
        }
        for (Professor professor : sistema.getProfessores()) {
            System.out.println(professor.getRegistro() + " " + professor.getNome() + " " + professor.getEmail());
        }

        listarDisciplinas();
    }

    private void cadastrarCurso() {
        String codigo = lerTextoObrigatorio("Codigo do curso");
        String nome = lerTextoObrigatorio("Nome do curso");
        int creditos = lerInteiroPositivo("Total de creditos");

        if (buscarCursoPorCodigo(codigo) != null) {
            mostrarErro("Curso ja cadastrado");
            return;
        }

        sistema.getCursos().add(new Curso(codigo, nome, creditos));
        salvarDados();
        System.out.println("Curso cadastrado");
    }

    private void cadastrarDisciplina() {
        String codigoCurso = lerTextoObrigatorio("Codigo do curso");
        Curso curso = buscarCursoPorCodigo(codigoCurso);

        if (curso == null) {
            mostrarErro("Curso nao encontrado");
            return;
        }

        String codigo = lerTextoObrigatorio("Codigo da disciplina");
        if (buscarDisciplinaPorCodigo(codigo) != null) {
            mostrarErro("Disciplina ja cadastrada");
            return;
        }

        String nome = lerTextoObrigatorio("Nome da disciplina");
        int creditos = lerInteiroPositivo("Creditos");
        int cargaHoraria = lerInteiroPositivo("Carga horaria");
        String diaSemana = lerTextoObrigatorio("Dia da semana");
        LocalTime inicio = lerHorario("Horario inicio HH:mm");
        LocalTime fim = lerHorario("Horario fim HH:mm");

        if (!fim.isAfter(inicio)) {
            mostrarErro("Horario final deve ser maior que horario inicial");
            return;
        }

        Disciplina disciplina = new Disciplina(codigo, nome, creditos, cargaHoraria);
        disciplina.setHorario(new Horario(diaSemana, inicio, fim));
        curso.adicionarDisciplina(disciplina);

        salvarDados();
        System.out.println("Disciplina cadastrada");
    }

    private void cadastrarAluno() {
        String codigoCurso = lerTextoObrigatorio("Codigo do curso");
        Curso curso = buscarCursoPorCodigo(codigoCurso);

        if (curso == null) {
            mostrarErro("Curso nao encontrado");
            return;
        }

        String matricula = lerTextoObrigatorio("Matricula");
        if (buscarAlunoPorMatricula(matricula) != null) {
            mostrarErro("Aluno ja cadastrado");
            return;
        }

        String nome = lerTextoObrigatorio("Nome");
        String email = lerTextoObrigatorio("Email");
        String senha = lerTextoObrigatorio("Senha");

        sistema.getAlunos().add(new Aluno("A" + matricula, nome, email, senha, matricula, curso));
        salvarDados();
        System.out.println("Aluno cadastrado");
    }

    private void cadastrarProfessor() {
        String registro = lerTextoObrigatorio("Registro");
        if (buscarProfessorPorRegistro(registro) != null) {
            mostrarErro("Professor ja cadastrado");
            return;
        }

        String nome = lerTextoObrigatorio("Nome");
        String email = lerTextoObrigatorio("Email");
        String senha = lerTextoObrigatorio("Senha");

        sistema.getProfessores().add(new Professor("P" + registro, nome, email, senha, registro));
        salvarDados();
        System.out.println("Professor cadastrado");
    }

    private void alocarProfessor(Secretaria secretaria) {
        String registro = lerTextoObrigatorio("Registro do professor");
        Professor professor = buscarProfessorPorRegistro(registro);

        if (professor == null) {
            mostrarErro("Professor nao encontrado");
            return;
        }

        String codigoDisciplina = lerTextoObrigatorio("Codigo da disciplina");
        Disciplina disciplina = buscarDisciplinaPorCodigo(codigoDisciplina);

        if (disciplina == null) {
            mostrarErro("Disciplina nao encontrada");
            return;
        }

        secretaria.alocarProfessor(professor, disciplina);
        if (!professor.getDisciplinas().contains(disciplina)) {
            professor.getDisciplinas().add(disciplina);
        }

        salvarDados();
        System.out.println("Professor alocado");
    }

    private void definirPeriodoMatricula() {
        LocalDateTime inicio = lerDataHora("Inicio yyyy-MM-dd HH:mm");
        LocalDateTime fim = lerDataHora("Fim yyyy-MM-dd HH:mm");

        if (!fim.isAfter(inicio)) {
            mostrarErro("Fim deve ser maior que inicio");
            return;
        }

        sistema.setPeriodoMatricula(new PeriodoMatricula(inicio, fim));
        salvarDados();
        System.out.println("Periodo definido");
    }

    private void encerrarPeriodo() {
        sistema.encerrarPeriodoMatriculas();
        salvarDados();
        System.out.println("Periodo encerrado");
    }

    private void relatorioTurmas() {
        mostrarTitulo("Relatorio de turmas");

        List<Disciplina> disciplinas = listarTodasDisciplinas();
        disciplinas.sort(Comparator.comparingInt((Disciplina disciplina) -> disciplina.getAlunosMatriculados().size()).reversed());

        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada");
            return;
        }

        for (Disciplina disciplina : disciplinas) {
            String risco = disciplina.getAlunosMatriculados().size() < Disciplina.MINIMO_ALUNOS ? " risco de cancelamento" : "";
            System.out.println(disciplina.getCodigo() + " " + disciplina.getNome()
                    + " alunos " + disciplina.getAlunosMatriculados().size()
                    + " status " + disciplina.getStatus()
                    + risco);
        }
    }

    private void listarDisciplinas() {
        mostrarTitulo("Disciplinas");

        List<Disciplina> disciplinas = listarTodasDisciplinas();
        if (disciplinas.isEmpty()) {
            System.out.println("Nenhuma disciplina cadastrada");
            return;
        }

        for (Disciplina disciplina : disciplinas) {
            String professor = disciplina.getProfessor() == null ? "sem professor" : disciplina.getProfessor().getNome();
            String horario = disciplina.getHorario() == null
                    ? "sem horario"
                    : disciplina.getHorario().getDiaSemana() + " " + disciplina.getHorario().getHoraInicio() + " ate " + disciplina.getHorario().getHoraFim();
            int vagas = Disciplina.MAXIMO_ALUNOS - disciplina.getAlunosMatriculados().size();

            System.out.println(disciplina.getCodigo() + " " + disciplina.getNome()
                    + " creditos " + disciplina.getCreditos()
                    + " carga " + disciplina.getCargaHoraria()
                    + " professor " + professor
                    + " vagas " + vagas
                    + " horario " + horario);
        }
    }

    private void efetuarMatricula(Aluno aluno) {
        if (!sistema.periodoEstaAberto()) {
            mostrarErro("Periodo de matricula fechado");
            return;
        }

        listarDisciplinas();

        List<Disciplina> obrigatorias = selecionarDisciplinas("Codigos obrigatorias separados por virgula");
        List<Disciplina> optativas = selecionarDisciplinas("Codigos optativas separados por virgula");

        List<Matricula> matriculas = sistema.efetuarMatricula(aluno, obrigatorias, optativas);

        salvarDados();
        System.out.println("Matricula efetuada");
        for (Matricula matricula : matriculas) {
            System.out.println(matricula.getDisciplina().getCodigo() + " " + matricula.getDisciplina().getNome());
        }
    }

    private void listarMatriculasAluno(Aluno aluno) {
        mostrarTitulo("Minhas matriculas");

        if (aluno.getMatriculas().isEmpty()) {
            System.out.println("Nenhuma matricula encontrada");
            return;
        }

        for (int i = 0; i < aluno.getMatriculas().size(); i++) {
            Matricula matricula = aluno.getMatriculas().get(i);
            System.out.println((i + 1) + " " + matricula.getDisciplina().getCodigo()
                    + " " + matricula.getDisciplina().getNome()
                    + " ativa " + matricula.isAtiva());
        }
    }

    private void cancelarMatricula(Aluno aluno) {
        if (!sistema.periodoEstaAberto()) {
            mostrarErro("Periodo de matricula fechado");
            return;
        }

        listarMatriculasAluno(aluno);

        if (aluno.getMatriculas().isEmpty()) {
            return;
        }

        int indice = lerInteiroPositivo("Numero da matricula") - 1;
        if (indice < 0 || indice >= aluno.getMatriculas().size()) {
            mostrarErro("Matricula nao encontrada");
            return;
        }

        sistema.cancelarMatricula(aluno.getMatriculas().get(indice));
        salvarDados();
        System.out.println("Matricula cancelada");
    }

    private void listarDisciplinasProfessor(Professor professor) {
        mostrarTitulo("Minhas disciplinas");

        if (professor.getDisciplinas().isEmpty()) {
            System.out.println("Nenhuma disciplina vinculada");
            return;
        }

        for (Disciplina disciplina : professor.getDisciplinas()) {
            System.out.println(disciplina.getCodigo() + " " + disciplina.getNome()
                    + " alunos " + disciplina.getAlunosMatriculados().size());
        }
    }

    private void consultarAlunosProfessor(Professor professor) {
        listarDisciplinasProfessor(professor);

        if (professor.getDisciplinas().isEmpty()) {
            return;
        }

        String codigoDisciplina = lerTextoObrigatorio("Codigo da disciplina");
        Disciplina disciplina = buscarDisciplinaPorCodigo(codigoDisciplina);

        if (disciplina == null) {
            mostrarErro("Disciplina nao encontrada");
            return;
        }

        List<Aluno> alunos = sistema.listarAlunosMatriculados(professor, disciplina);

        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno encontrado");
            return;
        }

        for (Aluno aluno : alunos) {
            System.out.println(aluno.getMatricula() + " " + aluno.getNome());
        }
    }

    private List<Disciplina> selecionarDisciplinas(String mensagem) {
        String entradaUsuario = lerTexto(mensagem);
        List<Disciplina> selecionadas = new ArrayList<>();

        if (entradaUsuario.isBlank()) {
            return selecionadas;
        }

        String[] codigos = entradaUsuario.split(",");
        for (String codigo : codigos) {
            Disciplina disciplina = buscarDisciplinaPorCodigo(codigo.trim());
            if (disciplina == null) {
                throw new RegraNegocioException("Disciplina nao encontrada");
            }
            selecionadas.add(disciplina);
        }

        return selecionadas;
    }

    private Curso buscarCursoPorCodigo(String codigo) {
        for (Curso curso : sistema.getCursos()) {
            if (curso.getCodigo().equalsIgnoreCase(codigo)) {
                return curso;
            }
        }
        return null;
    }

    private Disciplina buscarDisciplinaPorCodigo(String codigo) {
        for (Curso curso : sistema.getCursos()) {
            for (Disciplina disciplina : curso.getDisciplinas()) {
                if (disciplina.getCodigo().equalsIgnoreCase(codigo)) {
                    return disciplina;
                }
            }
        }
        return null;
    }

    private Aluno buscarAlunoPorMatricula(String matricula) {
        for (Aluno aluno : sistema.getAlunos()) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula)) {
                return aluno;
            }
        }
        return null;
    }

    private Professor buscarProfessorPorRegistro(String registro) {
        for (Professor professor : sistema.getProfessores()) {
            if (professor.getRegistro().equalsIgnoreCase(registro)) {
                return professor;
            }
        }
        return null;
    }

    private List<Disciplina> listarTodasDisciplinas() {
        List<Disciplina> disciplinas = new ArrayList<>();
        for (Curso curso : sistema.getCursos()) {
            disciplinas.addAll(curso.getDisciplinas());
        }
        return disciplinas;
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem + ": ");
        return entrada.nextLine().trim();
    }

    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (!texto.isBlank()) {
                return texto;
            }
            mostrarErro("Campo obrigatorio");
        }
    }

    private int lerInteiroPositivo(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                int valor = Integer.parseInt(texto);
                if (valor > 0) {
                    return valor;
                }
                mostrarErro("Valor deve ser positivo");
            } catch (NumberFormatException excecao) {
                mostrarErro("Valor numerico invalido");
            }
        }
    }

    private LocalTime lerHorario(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return LocalTime.parse(texto);
            } catch (DateTimeParseException excecao) {
                mostrarErro("Horario invalido use HH:mm");
            }
        }
    }

    private LocalDateTime lerDataHora(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return LocalDateTime.parse(texto, FORMATO_DATA_HORA);
            } catch (DateTimeParseException excecao) {
                mostrarErro("Data invalida use yyyy-MM-dd HH:mm");
            }
        }
    }

    private void mostrarTitulo(String titulo) {
        System.out.println();
        System.out.println(titulo);
    }

    private void mostrarErro(String mensagem) {
        System.out.println("Erro " + mensagem);
    }

    private void salvarDados() {
        try {
            persistencia.salvar(sistema);
        } catch (IllegalStateException excecao) {
            mostrarErro("Nao foi possivel salvar dados");
        }
    }

    private void carregarDadosIniciais() {
        Curso curso = new Curso("C1", "Sistemas de Informacao", 3000);

        Disciplina projeto = new Disciplina("D1", "Projeto de Software", 4, 80);
        projeto.setHorario(new Horario("Segunda", LocalTime.of(19, 0), LocalTime.of(20, 40)));

        Disciplina banco = new Disciplina("D2", "Banco de Dados", 4, 80);
        banco.setHorario(new Horario("Terca", LocalTime.of(19, 0), LocalTime.of(20, 40)));

        Disciplina algoritmos = new Disciplina("D3", "Algoritmos", 4, 80);
        algoritmos.setHorario(new Horario("Quarta", LocalTime.of(19, 0), LocalTime.of(20, 40)));

        curso.adicionarDisciplina(projeto);
        curso.adicionarDisciplina(banco);
        curso.adicionarDisciplina(algoritmos);

        Professor professor = new Professor("P1", "Professor Demo", "professor@sistema", "123", "P1");
        professor.getDisciplinas().add(projeto);
        projeto.setProfessor(professor);

        Aluno aluno = new Aluno("A1", "Aluno Demo", "aluno@sistema", "123", "A1", curso);
        Secretaria secretaria = new Secretaria("S1", "Secretaria Demo", "secretaria@sistema", "123", "Academico");

        sistema.getCursos().add(curso);
        sistema.getProfessores().add(professor);
        sistema.getAlunos().add(aluno);
        sistema.getSecretarias().add(secretaria);
        sistema.setPeriodoMatricula(new PeriodoMatricula(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7)));
    }
}
