package Encryption.ElGamal;

import it.unisa.dia.gas.jpbc.Element;

public  class KeyPair {
    private final Element privateKey;
    private final Element publicKey;

    public KeyPair(Element privateKey, Element publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Element getPrivateKey() {
        return privateKey;
    }

    public Element getPublicKey() {
        return publicKey;
    }
}
