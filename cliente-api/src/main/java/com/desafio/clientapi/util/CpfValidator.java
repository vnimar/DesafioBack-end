package com.desafio.clientapi.util;

public class CpfValidator {

    public static boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int firstDigit = (sum * 10) % 11;
        if (firstDigit == 10) firstDigit = 0;
        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = (sum * 10) % 11;
        if (secondDigit == 10) secondDigit = 0;
        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }
}
