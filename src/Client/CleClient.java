package Client;

import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;

public class CleClient implements Serializable {

    byte[] c1;

    byte[] c2;

    byte[] P;


    byte[] Ppub;

    public CleClient(byte[] c1, byte[] c2, byte[] p, byte[] ppub) {
        this.c1 = c1;
        this.c2 = c2;
        P = p;
        Ppub = ppub;
    }


    public byte[] getC1() {
        return c1;
    }

    public void setC1(byte[] c1) {
        this.c1 = c1;
    }

    public byte[] getC2() {
        return c2;
    }

    public void setC2(byte[] c2) {
        this.c2 = c2;
    }

    public byte[] getP() {
        return P;
    }

    public void setP(byte[] p) {
        P = p;
    }

    public byte[] getPpub() {
        return Ppub;
    }

    public void setPpub(byte[] ppub) {
        Ppub = ppub;
    }
}
