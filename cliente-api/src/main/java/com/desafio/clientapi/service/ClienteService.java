package com.desafio.clientapi.service;

import com.desafio.clientapi.exception.ClienteNaoEncontradoException;
import com.desafio.clientapi.exception.CpfDuplicadoException;
import com.desafio.clientapi.exception.ValidacaoException;
import com.desafio.clientapi.model.*;
import com.desafio.clientapi.repository.ClienteRepository;
import com.desafio.clientapi.util.CpfValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    // CRUD

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        validarCliente(cliente);
        associarFilhos(cliente);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));

        existente.setNome(clienteAtualizado.getNome());
        existente.setCpf(clienteAtualizado.getCpf());
        existente.setEndereco(clienteAtualizado.getEndereco());
        existente.getTelefones().clear();
        existente.getEmails().clear();

        if (clienteAtualizado.getTelefones() != null) {
            for (Telefone telefone : clienteAtualizado.getTelefones()) {
                telefone.setCliente(existente);
                existente.getTelefones().add(telefone);
            }
        }
        if (clienteAtualizado.getEmails() != null) {
            for (Email email : clienteAtualizado.getEmails()) {
                email.setCliente(existente);
                existente.getEmails().add(email);
            }
        }

        validarCliente(existente);
        return clienteRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    // ViaCEP

    public Endereco consultarCep(String cep) {
        String cepLimpo = cep.replaceAll("\\D", "");
        if (cepLimpo.length() != 8) {
            throw new ValidacaoException("CEP inválido! Deve conter 8 dígitos.");
        }

        ViaCepResponse response = restTemplate.getForObject(VIACEP_URL, ViaCepResponse.class, cepLimpo);

        if (response == null || response.getLogradouro() == null) {
            throw new ValidacaoException("CEP não encontrado.");
        }

        Endereco endereco = new Endereco();
        endereco.setCep(cepLimpo);
        endereco.setLogradouro(response.getLogradouro());
        endereco.setBairro(response.getBairro());
        endereco.setCidade(response.getLocalidade());
        endereco.setUf(response.getUf());
        endereco.setComplemento(response.getComplemento());
        return endereco;
    }

    // Validacoes

    private void validarCliente(Cliente cliente) {
        // Nome
        if (cliente.getNome() == null || cliente.getNome().trim().length() < 3 || cliente.getNome().trim().length() > 100) {
            throw new ValidacaoException("Nome deve ter entre 3 e 100 caracteres.");
        }
        if (!cliente.getNome().matches("[\\p{L}0-9 ]+")) {
            throw new ValidacaoException("Nome permite apenas letras, espaços e números.");
        }

        // CPF
        if (cliente.getCpf() == null || !cliente.getCpf().matches("\\d{11}")) {
            throw new ValidacaoException("CPF deve conter exatamente 11 dígitos (sem máscara).");
        }
        if (!CpfValidator.isValid(cliente.getCpf())) {
            throw new ValidacaoException("CPF inválido.");
        }

        Optional<Cliente> existente = clienteRepository.findByCpf(cliente.getCpf());
        if (existente.isPresent() && !existente.get().getId().equals(cliente.getId())) {
            throw new CpfDuplicadoException("CPF já cadastrado.");
        }

        // Endereco
        if (cliente.getEndereco() == null ||
                cliente.getEndereco().getCep() == null ||
                cliente.getEndereco().getCep().trim().isEmpty() ||
                cliente.getEndereco().getLogradouro() == null ||
                cliente.getEndereco().getLogradouro().trim().isEmpty() ||
                cliente.getEndereco().getBairro() == null ||
                cliente.getEndereco().getBairro().trim().isEmpty() ||
                cliente.getEndereco().getCidade() == null ||
                cliente.getEndereco().getCidade().trim().isEmpty() ||
                cliente.getEndereco().getUf() == null ||
                cliente.getEndereco().getUf().trim().isEmpty()) {
            throw new ValidacaoException("Endereço incompleto. Preencha CEP, logradouro, bairro, cidade e UF.");
        }

        // Telefones
        if (cliente.getTelefones() == null || cliente.getTelefones().isEmpty()) {
            throw new ValidacaoException("Pelo menos um telefone deve ser informado.");
        }
        for (Telefone t : cliente.getTelefones()) {
            if (t.getTipo() == null || t.getNumero() == null || t.getNumero().trim().isEmpty()) {
                throw new ValidacaoException("Telefone deve ter tipo e número.");
            }
            if (!t.getNumero().matches("\\d+")) {
                throw new ValidacaoException("Número de telefone deve conter apenas dígitos.");
            }
        }

        // Emails
        if (cliente.getEmails() == null || cliente.getEmails().isEmpty()) {
            throw new ValidacaoException("Pelo menos um e-mail deve ser informado.");
        }
        for (Email e : cliente.getEmails()) {
            if (e.getEmail() == null || !e.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new ValidacaoException("E-mail inválido: " + e.getEmail());
            }
        }
    }

    private void associarFilhos(Cliente cliente) {
        if (cliente.getTelefones() != null) {
            cliente.getTelefones().forEach(t -> t.setCliente(cliente));
        }
        if (cliente.getEmails() != null) {
            cliente.getEmails().forEach(e -> e.setCliente(cliente));
        }
    }
}