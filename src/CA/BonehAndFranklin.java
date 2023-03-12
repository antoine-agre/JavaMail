package CA;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class BonehAndFranklin {
    public Pairing pairing;
    public Element g, P;
    public HashMap<String,Element> publicKeys;
    BonehAndFranklin() {
        this.pairing = PairingFactory.getPairing("params/curves/a.properties");

        this.g = pairing.getG1().newRandomElement();
        this.P = pairing.getG2().newRandomElement();
    }

    protected Pairing getPairing() {
        return this.pairing;
    }

    protected Element getG() {
        return this.g;
    }
}
