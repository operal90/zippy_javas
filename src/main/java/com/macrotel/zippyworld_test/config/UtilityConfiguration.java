package com.macrotel.zippyworld_test.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

public class UtilityConfiguration {
    private static final String secretKey = "43877ABC5A56AE733918B7A3F850CD17";
    private static final Logger LOG = Logger.getLogger(UtilityConfiguration.class.getName());
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

    public String numberFormat(String text){
        String removeComma = text.replaceAll(",", "");
        double value = Double.parseDouble(removeComma);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
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
        else if (Objects.equals(type,"NOT")) {
            operationValue = todayDate+this.randomDigit(3);
        }
        return operationValue;
    }

    public String getOperationIdTaGarden(String type){
        String operationValue = "";
        if(Objects.equals(type, "NU")){
            operationValue = this.randomDigit(4);
        } else if (Objects.equals(type, "AN")) {
            operationValue = this.randomAlphanumeric(4);
        } else if (Objects.equals(type,"AL")) {
            operationValue = this.randomAlphabet(4);
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

    private static class JsonUtil{
        private static final Gson gson = new Gson();
        public  static String toJson(Object object){
            return gson.toJson(object);
        }
        public static <T> T fromJson(String jsonString, Class<T> clazz) {
            return gson.fromJson(jsonString, clazz);
        }
    }

    public String encryptData(Object payload){
        String encryptedValue = null;
        try{
            String jsonString = JsonUtil.toJson(payload);
            //Convert the payload to byteArray to get byte ArrayD
            byte[] D =  jsonString.getBytes(StandardCharsets.UTF_8);
            // Create a SecretKeySpec from the secret key bytes
            SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(secretKey), "AES");
            //Create a cipher instance for the AES/ECB/PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            //Encrypt the byte array
            byte[] encryptedBytes = cipher.doFinal(D);
            encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception e) {
            LOG.warning(e.getMessage());
        }
        return encryptedValue;
    }
    public String currentTimeStamp(){
        return String.valueOf((System.currentTimeMillis())/1000);
    }

    public Map<String, Object> decryptData(String encryptedData) {
        Map<String, Object> decryptedObject = null;
        try {
            // Decode the Base64-encoded string to obtain the byte array A
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(secretKey), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize the cipher with the secret key for decryption
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

            // Parse the decrypted string to a Map
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            decryptedObject = new Gson().fromJson(decryptedString, type);
        } catch (Exception e) {
            LOG.warning(e.getMessage());
        }
        return decryptedObject;
    }


}
