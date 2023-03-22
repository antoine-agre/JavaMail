package Cryptography.ElGamal;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Base64;

public class ElGamal {

    public static void main(String[] args) {

        PairingParameters pairingParams = PairingFactory.getPairingParameters("/home/issa/Courses/Advanced Cryptography/JavaMail/src/params/curves/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParams);
        Element generator = pairing.getG1().newRandomElement();

        KeyPair keyPair = generateKeyPair(pairing,generator);
        Element message = pairing.getG1().newRandomElement();



        CipherText cipherText = encrypt(message, keyPair.publicKey(), pairing,generator);

        Element decryptedMessage = decrypt(cipherText, keyPair.privateKey());
        System.out.println("Message: " + Base64.getEncoder().encodeToString(message.toBytes()));
        System.out.println("C1: " + Base64.getEncoder().encodeToString(cipherText.c1().toBytes()));
        System.out.println("C2: " + Base64.getEncoder().encodeToString(cipherText.c2().toBytes()));
        System.out.println("Messagz décrypté: " + Base64.getEncoder().encodeToString(decryptedMessage.toBytes()));


        if (message.isEqual(decryptedMessage)) {
            System.out.println("Décryptage réussi!");
        } else {
            System.out.println("Décrytage incorrect.");
        }

    }

    public static KeyPair generateKeyPair(Pairing pairing,Element generator) {

        Element privateKey = pairing.getZr().newRandomElement();
        Element publicKey = generator.duplicate().mulZn(privateKey);

        return new KeyPair(privateKey, publicKey);
    }

    public static CipherText encrypt(Element message, Element publicKey, Pairing pairing, Element generator) {
        Element r=pairing.getZr().newRandomElement();
        Element c2=publicKey.duplicate().mulZn(r);
        c2.add(message);
        Element c1=generator.duplicate().mulZn(r);


        return new CipherText(c1, c1);
    }
    public static Element decrypt(CipherText cipherText, Element privateKey) {


        Element m1 = cipherText.c1().duplicate().mulZn(privateKey);
        Element message = cipherText.c2().duplicate().sub(m1);

        return message;
    }



}
