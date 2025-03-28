package com.ded.misle.core;

import java.math.BigInteger;
import java.util.HashMap;

import static com.ded.misle.core.LanguageManager.getCurrentScript;
import static com.ded.misle.core.PraspomiaNumberConverter.ConvertMode.TO_PRASPOMIA;
import static com.ded.misle.renderer.ColorManager.removeColorIndicators;

public class PraspomiaNumberConverter {
    static HashMap<String, Integer> praspomiaNumbers = new HashMap<>();
    static {
        praspomiaNumbers.put("ζ", 1);
        praspomiaNumbers.put("η", 2);
        praspomiaNumbers.put("ξ", 3);
        praspomiaNumbers.put(";", 0);
    }

    public enum ConvertMode {
        TO_PRASPOMIA,
        TO_ARABIC,
    }

    static final char[] PRASPOMIA_SYMBOLS = {'ζ', 'η', 'ξ'};

    public static String convertNumberSystem(String inputNumber, ConvertMode mode) {
        switch (mode) {
            case TO_PRASPOMIA -> {
                try {
                    BigInteger toBeConverted = new BigInteger(inputNumber);
                    return convertToPraspomia(toBeConverted);
                } catch (NumberFormatException e) {
                    return "";
                }
            }

            case TO_ARABIC -> {
                try {
                    return String.valueOf(convertToBase10(inputNumber));
                } catch (NumberFormatException e) {
                    return "";
                }
            }
        }
        return "";
    }

    private static String convertToPraspomia(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) <= 0) {
            return "";
        }

        StringBuilder praspomia = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger remainder = number.subtract(BigInteger.ONE).mod(BigInteger.valueOf(3));
            praspomia.append(PRASPOMIA_SYMBOLS[remainder.intValue()]);
            number = number.subtract(BigInteger.ONE).divide(BigInteger.valueOf(3));
        }

        return praspomia.toString();
    }

    private static BigInteger convertToBase10(String number) {
        String[] splitNumber = number.split("");
        BigInteger outputNumber = BigInteger.ZERO;

        for (int i = 0; i < number.length(); i++) {
            int value = praspomiaNumbers.get(splitNumber[i]);
            outputNumber = outputNumber.add(BigInteger.valueOf(value).multiply(BigInteger.valueOf(3).pow(i)));
        }

        return outputNumber;
    }

    public static String impureConvertNumberSystem(String text, ConvertMode mode) {
        if (getCurrentScript() == LanguageManager.Script.PRASPOMIC) {
            for (String c : removeColorIndicators(text).toLowerCase().split("[^0-9]+")) {
                text = text.replaceFirst(c,
                    convertNumberSystem(c, mode));
            }
        }
        return text;
    }
}
