package com.desafio.clientapi.util;

public class MascaraUtil {

    public static String aplicarMascaraCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static String removerMascaraCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    public static String aplicarMascaraCep(String cep) {
        if (cep == null || cep.length() != 8) return cep;
        return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    public static String removerMascaraCep(String cep) {
        return cep == null ? null : cep.replaceAll("\\D", "");
    }

    public static String aplicarMascaraTelefone(String numero) {
        if (numero == null) return null;
        String digitos = numero.replaceAll("\\D", "");
        if (digitos.length() == 11) {
            return String.format("(%s) %s-%s",
                    digitos.substring(0,2), digitos.substring(2,7), digitos.substring(7));
        } else if (digitos.length() == 10) {
            return String.format("(%s) %s-%s",
                    digitos.substring(0,2), digitos.substring(2,6), digitos.substring(6));
        }
        return numero;
    }

    public static String removerMascaraTelefone(String telefone) {
        return telefone == null ? null : telefone.replaceAll("\\D", "");
    }
}
