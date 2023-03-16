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
    static protected Field Zr = pairing.getZr();
    static protected Field G = pairing.getG1();
    static protected Field GT= pairing.getGT();
    protected Element P;
    protected Element Ppub;
    protected Element private_key_master;
    protected HashMap<String, Element>  Key_couples= new HashMap();
    protected ArrayList<String> IDs= new ArrayList();

    IBEscheme(){
        this.private_key_master = Zr.newRandomElement();
        this.P = G.newRandomElement();
        this.Ppub = P.duplicate().mulZn(private_key_master);
    }
    protected void New_Set_Up_IBE(){
        this.P = G.newRandomElement();
        this.private_key_master = Zr.newRandomElement();
        this.Ppub = (this.P).duplicate().mulZn(this.private_key_master);
         //On reconstruit les clés privé es utilisateurs
        Key_couples.clean();
        build_HashMap();
    }

    protected Element generate_private_key_ID(String ID){
        if (map.get(ID) == null) {
            byte[] IDbytes = ID.getBytes();
            //On applique la fonction de hachage H1 à l'ID
            Element Qid = pairing.getG1().newElementFromHash(IDbytes, 0, IDbytes.length);
            //On calcule la clé privé de l'utilisateur ID
            Element private_key_ID = Qid.duplicate().mulZn(this.private_key_master);
            //On l'ajoute dans le Hashmap
            this.Key_couples.put(ID, private_key_ID)
            return private_key_ID;
        }
        else {return Key_couples.get(ID);}
    }

    protected void build_HashMap(){
        for (String adresse: IDs){generate_private_key_ID(adresse);}
    }
    protected byte[] XOR(byte[] a, byte[] b){
        byte[] c = new byte[a.length];
        for(int i=0; i<a.length; i++){c[i]= a[i]^[i];}
            return c;
    }
    protected Object[2] Encryption_Basic_IBE(Element P, Element Ppub, String ID, String message){
        Object[] C = new Object[2]
        Element r = pairing.getZr().newRandomElement();
        C[0]= P.duplicate().mulZn(r);
        byte[] IDbytes = ID.getBytes();
        //On applique la fonction de hachage H1 à l'ID
        Element Qid = pairing.getG1().newElementFromHash(IDbytes, 0, IDbytes.length);
        //On applique le couplage sur Ppub et Qid puis le hachage par H2
        C[1] = pairing.pairing(Qid, Ppub).powZn(r).toBytes();
        //On effectue un XOR avec le message en clair
        C[1] =XOR(message.getBytes(), C[1]);
        return C;
    }

    protected byte[] Decryption_Basic_IBE(Element P, Element Ppub, Element private_key_ID, Object[] C){
        Element M2 = pairing.pairing(private_key_ID, C[0]).toBytes();
        byte[] M = XOR(C[1], M2);
        return M;
    }


    public static void main(String[] args) {
        IBEscheme schema = new IBEscheme();
        Oject[] cypher = schema.Encryption_Basic_IBE(schema.P, schema.Ppub, "antoine.auger27@gmail.com", "Bonjour Antoine, comment vas-tu ?");
        byte[] plaintext = schema.Decryption_Basic_IBE(schema.P, schema.P, generate_private_key_ID("antoine.auger27@gmail.com"), cypher);
        System.out.println(StandardCharsets.UTF_8.name((plaintext));
    }


}
