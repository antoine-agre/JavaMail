package Encryption.ElGamal;

import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class ElGamal {

    public static void main(String[] args) {

        PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParams);

        KeyPair keyPair = generateKeyPair(pairing);

        Element message = pairing.getGT().newRandomElement();
        CipherText cipherText = encrypt(message, keyPair.getPublicKey(), pairing);

        //Element decryptedMessage = decrypt(cipherText, keyPair.getPrivateKey(), pairing);

    }

    public static KeyPair generateKeyPair(Pairing pairing) {
        Element privateKey = pairing.getG1().newRandomElement();

        Element publicKey = pairing.getG2().newElement().setFromHash(privateKey.toBytes(), 0, privateKey.toBytes().length);

        return new KeyPair(privateKey, publicKey);
    }

    public static CipherText encrypt(Element message, Element publicKey, Pairing pairing) {
        Element encryptionKey = pairing.getZr().newRandomElement();

        Element c1 = publicKey.duplicate().mulZn(encryptionKey);
        Element c2 = message.duplicate().mulZn(encryptionKey);

        return new CipherText(c1, c2);
    }
    /*public static Element decrypt(CipherText cipherText, Element privateKey, Pairing pairing) {

        Element decryptionKey = cipherText.getC1().duplicate().mulZn(privateKey);
        Element message = cipherText.getC2().duplicate().div(decryptionKey);

        return message;
    }*/



}
