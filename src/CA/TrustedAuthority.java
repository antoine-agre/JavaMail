package CA;

import it.unisa.dia.gas.jpbc.Element;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class TrustedAuthority {
    private HashMap<String,Element> privateKeys = new HashMap<String,Element>();
    private Element masterKey;
    public BonehAndFranklin parameters;

    public TrustedAuthority(BonehAndFranklin parameters) {
        this.parameters = parameters;
        this.masterKey = parameters.getPairing().getZr().newRandomElement();
    }

    protected void generatePrivateKey(String email, Element msk) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(email.getBytes());
        Element id =  parameters.getPairing().getZr().newElementFromHash(digest, 0, digest.length);

        Element privateKey = msk.mul(id.duplicate()).getImmutable();
        privateKeys.put(email, privateKey);
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        BonehAndFranklin parameters = new BonehAndFranklin();
        TrustedAuthority trustedAuthority = new TrustedAuthority(parameters);
        trustedAuthority.generatePrivateKey("aaa@mmm.com",trustedAuthority.masterKey);
    }
}