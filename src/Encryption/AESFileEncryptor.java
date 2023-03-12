package Encryption;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class AESFileEncryptor {
    public static void fileEncrypt(File inputFile, File outputFile, String secretKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] encryptedBytes = AES.encrypt(new String(inputBytes), secretKey.getBytes());
            outputStream.write(encryptedBytes);
        }
    }

    public static void fileDecrypt(File inputFile, File outputFile, String secretKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] decryptedBytes = AES.decrypt(inputBytes, secretKey.getBytes()).getBytes();
            outputStream.write(decryptedBytes);
        }
    }

    public static void main(String[] args) {

        System.out.print("Enter secret key: ");
        String secretKey = "issa";

        System.out.print("Enter path of file to encrypt: ");
        File inputFile = new File("xxxxxxx");

        System.out.print("Enter path of encrypted file: ");
        File encryptedFile = new File("xxxxxxxxxxxxxx");

        System.out.print("Enter path of decrypted file: ");
        File decryptedFile = new File("/home/issa/Courses/AdvCrypto/JavaMail/src/Encryption/file3.txt");

        try {
            fileEncrypt(inputFile, encryptedFile, secretKey);
            fileDecrypt(encryptedFile, decryptedFile, secretKey);

            System.out.println("File encrypted and decrypted successfully.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
