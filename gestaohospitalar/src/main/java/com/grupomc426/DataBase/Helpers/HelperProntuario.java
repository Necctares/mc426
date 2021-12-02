package com.grupomc426.DataBase.Helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grupomc426.DataBase.ACAO;
import com.grupomc426.DataBase.ProntuarioDB;
import com.grupomc426.Targets.Atendimento.Exame;
import com.grupomc426.Targets.Atendimento.Prontuario;
import com.grupomc426.Targets.Produtos.Medicamento;
import com.grupomc426.Targets.Usuarios.Medico;
import com.grupomc426.Targets.Usuarios.Pessoa;
import com.grupomc426.Targets.Usuarios.Usuario;

public class HelperProntuario implements HelperDB {
    private ProntuarioDB db;
    private static HelperProntuario instance = null;
    // TODO
    // Mock para substituir temporariamente o DB
    private Map<String, String> mockDatabase = new HashMap<String, String>();

    // Constantes
    private final int MAX_CARACTER_CPF = 11;
    private final int MAX_CARACTER_NOME = 100;
    private final int MAX_CARACTER_TELEFONE = 11;
    private final int MAX_CARACTER_ANONASCIMENTO = 4;
    private final int MAX_CARACTER_SENHA = 30;
    private final int MIN_CARACTER_SENHA = 8;

    private HelperProntuario() {
        db = new ProntuarioDB();
        mockDatabase.put("11111111111", "12345678");
        mockDatabase.put("01234567890", "87654321");
    }

    public static HelperProntuario getDB() {
        if (instance == null) {
            instance = new HelperProntuario();
        }
        return instance;
    }

    public boolean isDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < 48 || ch > 57)
                return false;
        }
        return true;
    }

    public boolean operacaoCadastro(ACAO operacao, Usuario usuario) throws IllegalArgumentException {
        if (operacao == ACAO.ADICIONAR) {
            if (ehUsuarioValido(usuario)) {
                String cadastro = "('" + usuario.getNome() + "', '" + usuario.getTelefone() + "', '" + usuario.getCPF()
                        + "', " + usuario.getAnoNascimento() + ")";
                db.adicionarPessoa(cadastro);
                cadastro = "('" + usuario.getID() + "', '" + usuario.getSenha() + "', '" + usuario.getEhFuncionario()
                        + ")";
                db.adicionarUsuario(cadastro);
                return true;
            }
        } else if (operacao == ACAO.REMOVER) {
            if (isDigit(usuario.getCPF()) && isDigit(usuario.getTelefone())) {
                db.removerPessoa(usuario.getCPF());
                db.removerUsuario(usuario.getID());
                return true;
            }
        }
        throw new IllegalArgumentException("Falha ao conectar ao Banco de Dados!");
    }

    public boolean adicionarMedicamento(int prontuarioID, Medicamento medicamento) {
        Map<String, String> mapaMedicamento = new HashMap<String, String>();
        mapaMedicamento.put("id", Integer.toString(medicamento.getID()));
        mapaMedicamento.put("numUso", Integer.toString(medicamento.getNumUso()));
        return db.adicionarMedicamento(prontuarioID, mapaMedicamento);
    }

    public List<Medicamento> pegarMedicamento(int prontuarioID) {
        List<Medicamento> lista = db.pegarMedicamento(prontuarioID);
        if (lista.size() > 0) {
            return lista;
        } else {
            return null;
        }
    }

    public boolean removerMedicamento(int objID) {
        return db.removerMedicamento(objID);
    }

    public boolean adicionarExame(Exame exame) {
        Map<String, String> mapaExame = new HashMap<String, String>();
        mapaExame.put("titulo", exame.getTitulo());
        mapaExame.put("id", exame.getid());
        mapaExame.put("medico", exame.getMedico().getCrm());
        mapaExame.put("paciente", exame.getPaciente().getCPF());
        mapaExame.put("anotacoes", exame.getAnotacoes());
        mapaExame.put("resultado", exame.getResultado());
        mapaExame.put("foiAssinado", Boolean.toString(exame.getFoiAssinado()));
        mapaExame.put("assinatura", exame.getAssinatura());
        mapaExame.put("horario", exame.getHorario().toString());
        return db.adicionarExame(mapaExame);
    }

    public boolean assinarExame(String exameId, String assinatura) {
        return db.assinarExame(exameId, assinatura);
    }

    public Prontuario obterProntuario(String cpf) {
        String prontuario = db.obterProntuario(cpf);
        Prontuario novoProntuario = null;
        // TODO
        return novoProntuario;
    }

    public Pessoa obterPessoa(String cpf) {
        return db.obterPessoa(cpf);
    }

    public Medico obterMedico(String crm){
        return db.obterMedico(crm);
    }

    @Override
    public boolean tentarLogin(Usuario usuario) throws IllegalArgumentException {
        // TODO Implementar Parte do DB
        if (usuarioExiste(usuario) && senhaEstaCorreta(usuario))
            return true;
        else
            return false;
    }

    private boolean usuarioExiste(Usuario usuario) throws IllegalArgumentException {
        // MOCK
        if (mockDatabase.containsKey(usuario.getID())) {
            return true;
        } else {
            throw new IllegalArgumentException("Usuário não cadastrado!");
        }
    }

    private boolean senhaEstaCorreta(Usuario usuario) throws IllegalArgumentException {
        // MOCK
        String senha = mockDatabase.get(usuario.getID());
        // String senha = db.obterSenha(usuario.getID());
        if (usuario.getSenha().equals(senha))
            return true;
        throw new IllegalArgumentException("Senha incorreta!");
    }

    private boolean ehUsuarioValido(Usuario usuario) throws IllegalArgumentException {
        if (ehNomeValido(usuario.getNome()) && ehTelefoneValido(usuario.getTelefone()) && ehCPFValido(usuario.getCPF())
                && ehAnoDeNascimentoValido(usuario.getAnoNascimento()) && ehSenhaValida(usuario.getSenha())) {
            return true;
        }
        return false;
    }

    private boolean ehNomeValido(String nome) throws IllegalArgumentException {
        if (nome.matches("[a-zA-Z ]+") && nome.length() <= MAX_CARACTER_NOME) {
            return true;
        }
        throw new IllegalArgumentException("Nome inválido! Use apenas letras e espaço.");
    }

    private boolean ehTelefoneValido(String telefone) throws IllegalArgumentException {
        if (isDigit(telefone) && telefone.length() == MAX_CARACTER_TELEFONE) {
            return true;
        }
        throw new IllegalArgumentException(
                "Telefone inválido! Inclua seu DDD e número de telefone, e digite apenas números.");
    }

    private boolean ehCPFValido(String CPF) throws IllegalArgumentException {
        if (isDigit(CPF) && CPF.length() == MAX_CARACTER_CPF) {
            return true;
        }
        throw new IllegalArgumentException("CPF inválido! Digite apenas os 11 números do seu CPF.");
    }

    private boolean ehAnoDeNascimentoValido(String anoNascimento) throws IllegalArgumentException {
        if (isDigit(anoNascimento) && anoNascimento.length() == MAX_CARACTER_ANONASCIMENTO) {
            return true;
        }
        throw new IllegalArgumentException("Ano inválido! Digite apenas seu ano de nascimento.");
    }

    private boolean ehSenhaValida(String senha) throws IllegalArgumentException {
        if (senha.matches("^[a-zA-Z0-9]*$") && senha.length() <= MAX_CARACTER_SENHA
                && senha.length() >= MIN_CARACTER_SENHA) {
            return true;
        }
        throw new IllegalArgumentException(
                "Senha inválida! Escolha uma senha com pelo menos 8 caracteres e até no máximo 30, podendo usar letras ou números.");
    }
}