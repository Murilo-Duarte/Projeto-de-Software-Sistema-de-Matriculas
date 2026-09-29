## Histórias de Usuário (User Stories)

### US01: Gerenciamento do Currículo e Cadastros
> **Como** secretaria da universidade,
> **Quero** gerar o currículo para cada semestre e manter as informações sobre as disciplinas, professores e alunos,
> **Para que** a universidade possa organizar a oferta acadêmica do semestre.
- [ ] O sistema deve permitir o cadastro, edição e exclusão de cursos, disciplinas, professores e alunos.

---

### US02: Realizar Matrícula
> **Como** aluno,
> **Quero** acessar o sistema durante o período de matrículas para me inscrever nas disciplinas ofertadas,
> **Para que** eu possa cursar o semestre letivo.
**Critérios de Aceite:**
- [ ] O aluno deve poder escolher até 2 disciplinas alternativas (optativas).
- [ ] O sistema deve bloquear inscrições em uma disciplina caso ela atinja o limite máximo de 60 alunos matriculados.

---

### US03: Cancelar Matrícula
> **Como** aluno,

- [ ] O cancelamento só pode ser realizado durante os períodos abertos para efetuar matrículas.
- [ ] O sistema deve atualizar o número de vagas disponíveis na disciplina após o cancelamento.

---

### US04: Consultar Alunos Matriculados
> **Como** professor,
> **Para que** eu possa acompanhar as minhas turmas do semestre.
**Critérios de Aceite:**
- [ ] O professor só deve visualizar a lista de alunos das disciplinas às quais ele foi previamente vinculado.

---

### US05: Encerramento do Período e Ativação de Disciplinas
> **Como** sistema de matrículas,

- [ ] O sistema deve manter ativa apenas as disciplinas que tiverem, pelo menos, 3 alunos inscritos no final do período.
- [ ] O sistema deve cancelar automaticamente as disciplinas que não atingirem o quórum mínimo de 3 alunos.

---

### US06: Integração Financeira
> **Como** sistema de matrículas,

- [ ] A notificação de cobrança deve ser disparada automaticamente após a confirmação da matrícula no semestre.

---

### US07: Autenticação de Usuários
> **Como** usuário do sistema (aluno, professor ou secretaria),
> **Quero** realizar login utilizando uma senha,

- [ ] Todos os perfis de usuário devem possuir senhas exigidas na validação do respectivo login.
- [ ] O sistema deve restringir o acesso apenas a usuários autenticados corretamente.

### US08: Definição do Período de Matrículas
> **Como** secretaria da universidade,
> **Quero** estipular as datas de início e fim do período de matrículas,
> **Para que** o sistema libere e bloqueie o acesso dos alunos automaticamente de acordo com o calendário letivo.
- [ ] O sistema deve permitir configurar uma data e hora de abertura e encerramento.

---

### US09: Cadastro de Cursos
> **Como** secretaria da universidade,
> **Quero** cadastrar novos cursos informando nome e número total de créditos,
> **Para que** as disciplinas possam ser vinculadas corretamente às suas respectivas matrizes curriculares.
- [ ] O sistema não deve permitir o cadastro de dois cursos com o mesmo nome.
- [ ] O sistema deve validar se o número de créditos inserido é um valor numérico positivo.

---

### US10: Alocação de Professores
> **Como** secretaria da universidade,
> **Quero** vincular professores às disciplinas oferecidas no semestre,
> **Para que** as turmas tenham responsáveis definidos antes da abertura das inscrições.

**Critérios de Aceite:**
- [ ] Cada disciplina deve ter pelo menos um professor alocado antes de ser exibida para os alunos.
- [ ] Um professor pode ser alocado em mais de uma disciplina no mesmo semestre.

---

### US11: Detalhamento da Disciplina
> **Como** aluno,
> **Quero** visualizar o professor responsável, o número de créditos e a carga horária de uma disciplina,
> **Para que** eu possa tomar decisões assertivas na montagem da minha grade.

**Critérios de Aceite:**
- [ ] A tela de seleção de turmas deve exibir os detalhes da disciplina ao ser clicada.
- [ ] O sistema deve indicar visualmente o número de vagas restantes na turma (limite de 60).

---

### US12: Prevenção de Conflito de Horários
> **Como** sistema de matrículas,
> **Quero** bloquear a inscrição em disciplinas que ocorram no mesmo dia e horário,
> **Para que** o aluno não possua choques de horário na grade letiva.

**Critérios de Aceite:**
- [ ] O sistema deve exibir uma mensagem de erro ("Conflito de Horário") se o aluno selecionar duas disciplinas simultâneas.
- [ ] A matrícula conflitante não deve ser processada.

---

### US13: Relatório de Ocupação de Turmas
> **Como** secretaria da universidade,
> **Quero** gerar um painel visual (ou relatório) com o número atual de inscritos em cada disciplina,
> **Para que** eu possa monitorar quais turmas estão lotadas e quais correm risco de cancelamento.

**Critérios de Aceite:**
- [ ] O relatório deve listar as disciplinas ordenadas pela quantidade de inscritos.
- [ ] O sistema deve destacar disciplinas com menos de 3 alunos inscritos durante o período de matrículas.

---

### US14: Ajuste Financeiro por Cancelamento
> **Como** sistema de matrículas,
> **Quero** notificar o sistema de cobranças caso um aluno cancele uma matrícula dentro do prazo,
> **Para que** a fatura ou os boletos gerados para o semestre sejam recalculados sem cobrar pela matéria cancelada.

**Critérios de Aceite:**
- [ ] O cancelamento bem-sucedido de uma disciplina deve disparar automaticamente um evento de atualização para o sistema de cobranças.

---

### US15: Persistência de Dados (Requisito Não Funcional)
> **Como** sistema de matrículas,
> **Quero** salvar todas as informações cadastrais e de matrículas em arquivos locais,
> **Para que** os dados inseridos no protótipo Java não sejam perdidos ao fechar a aplicação.

**Critérios de Aceite:**
- [ ] Ao iniciar, o sistema deve carregar os dados armazenados nos arquivos locais.
- [ ] Cada inclusão, alteração ou exclusão deve ser escrita e salva nos arquivos de persistência correspondentes.
