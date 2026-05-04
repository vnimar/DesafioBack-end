package com.desafio.clientapi.dto;

import com.desafio.clientapi.model.TipoTelefone;

public class TelefoneDTO {

    private TipoTelefone tipo;
    private String numero; // com mascara

    public TipoTelefone getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
