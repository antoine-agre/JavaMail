import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.security.MessageDigest;
import java.lang.Math;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

public class IBEscheme {
    static protected Pairing pairing = PairingFactory.getPairing("//Users/auger/Desktop/ICY 4A/Crypto avancée/IMINE/Cours/jpbc-2.0.0/params/curves/a.properties");
    static protected Field Zr;
    static protected Field G;
    static protected Field GT;
    protected Element P;
    protected Element Ppub;
    protected Element private_key_master;
    HashMap<String, Element>  Key_couples;
    ArrayList<String> IDs;

    IBEscheme(){
        this.G = pairing.getG1();
        this.GT = pairing.getGT();
        this.Zr = pairing.getZr();
        this.private_key_master = Zr.newRandomElement();
        this.G = pairing.getG1();
        this.P = G.newRandomElement();
        this.Ppub = P.duplicate().mulZn(private_key_master);
        this.Key_couples = new HashMap ();
        this.IDs = new ArrayList();
    }
    private void New_Set_Up_IBE(){
        this.private_key_master = Zr.newRandomElement();
        this.P = G.newRandomElement();
        this.private_key_master = Zr.newRandomElement();
        this.Ppub = (this.P).duplicate().mulZn(this.private_key_master);
    }

    private Element generate_private_key_ID(String ID){
        Element Qid = G.newRandomElement();
        byte[] IDbytes = ID.getBytes();
        Qid.setFromHash(IDbytes, 0, IDbytes.length);
        Element private_key_ID = (this.private_key_master).duplicate().mul(Qid);
        return private_key_ID;
    }

    private void build_HashMap(){
        this.Key_couples = new HashMap ();
        for (String adresse: IDs){
            this.Key_couples.put(adresse, generate_private_key_ID(adresse));
        }
    }

    private void ajoute_Couples(String ID){
        this.Key_couples.put(ID, generate_private_key_ID(ID));
    }

    private void remove_Couples(String ID){}

    private static double[] encAL_GAMAL(double M, double a, double p, double[] publickey){
        double[] crypted = {Math.pow(publickey[0],a) % p, M*Math.pow(publickey[0],a) % p};
        return crypted;
    }

    private static double inverse(double n, double p){
        for (int i = 1; i<p; i++){
            if((i * n )% p == 1){return i;}
        }
        return 0;
    }

    private static double decAL_GAMAL(double M, double b, double p, double[] crypted){
        double w = Math.pow(crypted[0],b) % p;
        double inv_w = inverse(w, 17);
        return inv_w * crypted[1];
    }

    private static ArrayList<String> decoupage(String message){
        ArrayList<String> liste = new ArrayList<String>();
        for (char ch: message.toCharArray()) {
            //liste.add();
        }
        return liste;
    }

    public String chiffrement(String message){
        //String code=decoupage(message);
        //cyphertext=encAL_GAMAL();
        return message;
    }

    public static void main(String[] args) {
        Pairing pairing = PairingFactory.getPairing("//Users/auger/Desktop/ICY 4A/Crypto avancée/IMINE/Cours/jpbc-2.0.0/params/curves/a.properties");
        Field Zr = pairing.getZr();
        Element privatekey = Zr.newRandomElement();
        Field G = pairing.getG1();
        Element generateur = Zr.newRandomElement();
      /*  Element[] publickey = PKAL_GAMAL(generateur, privatekey);
        Arraylist<String>cyphertext =chiffrement("Message à chiffrer avec l'algorithme d'AL-GAMAL.", public_key);*/
    }


}
