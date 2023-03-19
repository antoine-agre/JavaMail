package Cryptography.IBE;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class IBEscheme {
    static protected Pairing pairing = PairingFactory.getPairing(Path.of("src/Cryptography/param/a.properties").toString());
    static protected Field Zr = pairing.getZr();
    static protected Field G = pairing.getG1();
    static protected Field GT= pairing.getGT();
    protected Element P;
    protected Element Ppub;
    protected Element private_key_master;
    protected HashMap<String, Element>  Key_couples= new HashMap();
    protected ArrayList<String> IDs= new ArrayList();

    public IBEscheme(){
        this.private_key_master = Zr.newRandomElement();
        this.P = G.newRandomElement();
        this.Ppub = P.duplicate().mulZn(private_key_master);
    }

    public static Pairing getPairing() {
        return pairing;
    }

    public static Field getZr() {
        return Zr;
    }

    public static Field getG() {
        return G;
    }

    public static Field getGT() {
        return GT;
    }

    public Element getP() {
        return P;
    }

    public Element getPpub() {
        return Ppub;
    }

    public Element getPrivate_key_master() {
        return private_key_master;
    }

    public HashMap<String, Element> getKey_couples() {
        return Key_couples;
    }

    public ArrayList<String> getIDs() {
        return IDs;
    }

    protected void New_Set_Up_IBE(){
        this.P = G.newRandomElement();
        this.private_key_master = Zr.newRandomElement();
        this.Ppub = (this.P).duplicate().mulZn(this.private_key_master);
         //On reconstruit les clés privé es utilisateurs
        Key_couples.clear();
        build_HashMap();
    }

    protected Element[] Pubic_Parameters(){
        Element[] PP = new Element[2];
        PP[0] = this.P;
        PP[1] = this.Ppub;
        return PP;
    }

    public Element generate_private_key_ID(String ID){
        if (Key_couples.get(ID) == null) {
            byte[] IDbytes = ID.getBytes();
            //On applique la fonction de hachage H1 à l'ID
            Element Qid = pairing.getG1().newElementFromHash(IDbytes, 0, IDbytes.length);
            //On calcule la clé privé de l'utilisateur ID
            Element private_key_ID = Qid.duplicate().mulZn(this.private_key_master);
            //On l'ajoute dans le Hashmap
            this.Key_couples.put(ID, private_key_ID);
            return private_key_ID;
        }
        else {return Key_couples.get(ID);}
    }

    protected void build_HashMap(){
        for (String adresse: IDs){generate_private_key_ID(adresse);}
    }
    protected byte[] XOR(byte[] a, byte[] b){
        byte[] c = new byte[a.length];
        for(int i=0; i<a.length; i++){c[i ]= (byte) ((int)a[i]^(int)b[i]);}
            return c;
    }
    public IBECipherText Encryption_Basic_IBE(Element P, Element Ppub, String ID, String message){
        IBECipherText C = new IBECipherText();
        Element r = pairing.getZr().newRandomElement();
        C.setU(P.duplicate().mulZn(r));
        byte[] IDbytes = ID.getBytes();
        //On applique la fonction de hachage H1 à l'ID
        Element Qid = pairing.getG1().newElementFromHash(IDbytes, 0, IDbytes.length);
        //On applique le couplage sur Ppub et Qid puis le hachage par H2
        C.setV(pairing.pairing(Qid, Ppub).powZn(r).toBytes());
        //On effectue un XOR avec le message en clair
        C.setV(XOR(message.getBytes(), C.getV()));
        return C;
    }

    public byte[] Decryption_Basic_IBE(Element P, Element Ppub, Element private_key_ID, IBECipherText C){
        byte[] M2 = pairing.pairing(private_key_ID, C.getU()).toBytes();
        byte[] M = XOR(C.getV(), M2);
        return M;
    }


    public static void main(String[] args) {
        IBEscheme schema = new IBEscheme();
        IBECipherText cypher = schema.Encryption_Basic_IBE(schema.P, schema.Ppub, "antoine.auger27@gmail.com", "Bonjour Antoine, comment vas-tu ?");
        byte[] plaintext = schema.Decryption_Basic_IBE(schema.P, schema.P, schema.generate_private_key_ID("antoine.auger27@gmail.com"), cypher);
        System.out.println(new String(plaintext, StandardCharsets.US_ASCII));
    }


}
