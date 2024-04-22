package com.macrotel.zippyworld_test.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class UtilityConfiguration {
    public String capitalizeString(String text){
        if(text == null || text.isEmpty()){
            return text;
        }
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }

    public String randomAlphanumeric(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    public String randomAlphabet(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String otpCode(int length){
        String capitalLetter  = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String smallLetter = "abcdefghjkmnpqrstuvwxyz";
        String numbers = "123456789";

        String mainCharacters =capitalLetter+smallLetter+numbers;
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(mainCharacters.length());
            char randomChar = mainCharacters.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String randomDigit(int length){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }
        return sb.toString();
    }

    public String encryptPassword(String userPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(userPassword.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return userPassword;
    }

    public String currencyFormat(String text){
        String removeComma = text.replaceAll(",", "");
        double value = Double.parseDouble(removeComma);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(value);
    }
    public String removeComma(String value){
        return value.replaceAll(",","");
    }

    public String extractUsername(String emailAddress) {
        int atIndex = emailAddress.indexOf('@');
        if (atIndex != -1) {
            return emailAddress.substring(0, atIndex);
        } else {
            return emailAddress;
        }
    }

    public String referenceId(){
        String todayDate = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")));
        return todayDate+this.randomDigit(3);
    }

    public String shaEncryption(String password) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] encodedHash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return hexString.toString().toUpperCase();
    }

    public String getOperationId(String type){
        String operationValue = "";
        String todayDate = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")));
        if(Objects.equals(type, "NU")){
            operationValue = todayDate+this.randomDigit(10);
        } else if (Objects.equals(type, "AN")) {
            operationValue = todayDate+this.randomAlphanumeric(2);
        } else if (Objects.equals(type,"AL")) {
            operationValue = todayDate+this.randomAlphabet(2);
        }
        return operationValue;
    }

    public Double formattedAmount(String amount){
        double removeCommaAmount = Double.parseDouble(this.removeComma(amount));
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        return Double.parseDouble(decimalFormat.format(removeCommaAmount));
    }

    public Double twoDecimalFormattedAmount(String amount){
        double removeCommaAmount = Double.parseDouble(this.removeComma(amount));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(removeCommaAmount));
    }
    public Double zeroDecimalFormattedAmount(String amount){
        double removeCommaAmount = Double.parseDouble(this.removeComma(amount));
        int truncatedAmount = (int) removeCommaAmount;
        return (double) truncatedAmount;
    }
}
