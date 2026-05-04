package com.desafio.clientapi.controller;

import com.desafio.clientapi.dto.*;
import com.desafio.clientapi.model.*;
import com.desafio.clientapi.service.ClienteService;
import com.desafio.clientapi.util.MascaraUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Leitura (user e adm)
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listar() {
        List<ClienteResponseDTO> lista = clienteService.listarTodos()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> ResponseEntity.ok(toResponseDTO(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Permissoes do adm

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> criar(@RequestBody ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        Cliente salvo = clienteService.salvar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id, @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        Cliente atualizado = clienteService.atualizar(id, cliente);
        return ResponseEntity.ok(toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Conlsutar CEP
    @GetMapping("/cep/{cep}")
    public ResponseEntity<EnderecoDTO> consultarCep(@PathVariable String cep) {
        Endereco endereco = clienteService.consultarCep(cep);
        EnderecoDTO dto = new EnderecoDTO();
        dto.setCep(MascaraUtil.aplicarMascaraCep(endereco.getCep()));
        dto.setLogradouro(endereco.getLogradouro());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setUf(endereco.getUf());
        dto.setComplemento(endereco.getComplemento());
        return ResponseEntity.ok(dto);
    }

    // Conversão
    private Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(MascaraUtil.removerMascaraCpf(dto.getCpf()));

        Endereco endereco = new Endereco();
        EnderecoDTO endDTO = dto.getEndereco();
        endereco.setCep(MascaraUtil.removerMascaraCep(endDTO.getCep()));
        endereco.setLogradouro(endDTO.getLogradouro());
        endereco.setBairro(endDTO.getBairro());
        endereco.setCidade(endDTO.getCidade());
        endereco.setUf(endDTO.getUf());
        endereco.setComplemento(endDTO.getComplemento());
        cliente.setEndereco(endereco);

        List<Telefone> telefones = dto.getTelefones().stream().map(telDto -> {
            Telefone tel = new Telefone();
            tel.setTipo(telDto.getTipo());
            tel.setNumero(MascaraUtil.removerMascaraTelefone(telDto.getNumero()));
            return tel;
        }).collect(Collectors.toList());
        cliente.setTelefones(telefones);

        List<Email> emails = dto.getEmails().stream().map(emailDto -> {
            Email email = new Email();
            email.setEmail(emailDto.getEmail());
            return email;
        }).collect(Collectors.toList());
        cliente.setEmails(emails);

        return cliente;
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setCpf(MascaraUtil.aplicarMascaraCpf(cliente.getCpf()));

        EnderecoDTO endDTO = new EnderecoDTO();
        if (cliente.getEndereco() != null) {
            endDTO.setCep(MascaraUtil.aplicarMascaraCep(cliente.getEndereco().getCep()));
            endDTO.setLogradouro(cliente.getEndereco().getLogradouro());
            endDTO.setBairro(cliente.getEndereco().getBairro());
            endDTO.setCidade(cliente.getEndereco().getCidade());
            endDTO.setUf(cliente.getEndereco().getUf());
            endDTO.setComplemento(cliente.getEndereco().getComplemento());
        }
        dto.setEndereco(endDTO);

        List<TelefoneDTO> telsDTO = cliente.getTelefones().stream().map(tel -> {
            TelefoneDTO tDto = new TelefoneDTO();
            tDto.setTipo(tel.getTipo());
            tDto.setNumero(MascaraUtil.aplicarMascaraTelefone(tel.getNumero()));
            return tDto;
        }).collect(Collectors.toList());
        dto.setTelefones(telsDTO);

        List<EmailDTO> emailsDTO = cliente.getEmails().stream().map(e -> {
            EmailDTO eDto = new EmailDTO();
            eDto.setEmail(e.getEmail());
            return eDto;
        }).collect(Collectors.toList());
        dto.setEmails(emailsDTO);

        return dto;
    }
}
