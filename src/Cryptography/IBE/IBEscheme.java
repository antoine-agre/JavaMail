package Cryptography.IBE;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class IBEscheme {
    static protected Pairing pairing = PairingFactory.getPairing("params/curves/a.properties");
    static protected Field Zr = pairing.getZr();
    static protected Field G = pairing.getG1();
    static protected Field GT= pairing.getGT();
    protected Element P;
    protected Element Ppub;
    protected Element private_key_master;
    protected HashMap<String, Element>  Key_couples= new HashMap();
    protected ArrayList<String> IDs= new ArrayList();

    public IBEscheme(){
        generate_PMK_P();
        this.Ppub = P.duplicate().mulZn(private_key_master);
    }
    protected void New_Set_Up_IBE(){
        generate_PMK_P();
        this.Ppub = (this.P).duplicate().mulZn(this.private_key_master);
         //On reconstruit les clés privé es utilisateurs
        Key_couples.clear();
        build_HashMap();
    }

    //Fonction qui genere la clé privé maitre et gere la lecture dans un fichier
    protected void generate_PMK_P(){
        //Fichier de configuration pour stocker la clé secrète
        String configFilePath = "/Users/auger/Documents/GitHub/JavaMail/src/Cryptography/IBE/PKM.properties";
        Properties Parameters = new Properties();
        try {
            Parameters.load(new FileInputStream(configFilePath));
        } catch(IOException e) {e.printStackTrace();}
        String chaine = Parameters.getProperty("PKM");
        String chaine2 = Parameters.getProperty("P");
        if (chaine.length() != 0){
            //La clé existe et est stocké dans le fichier
            System.out.println("Lecture depuis fichier");
            byte[] bytes = chaine.getBytes();
            byte[] bytes2 = chaine2.getBytes();
            Element e = Zr.newElement();
            Element f = G.newElement();
            this.private_key_master = e.setFromHash(bytes, 0, bytes.length).duplicate();
            this.P = e.setFromHash(bytes2, 0, bytes2.length).duplicate();
        }
        else {
            //Elle est générée et stockée dans un fichier
            System.out.println("Stockage dans le fichier");
            this.private_key_master = Zr.newRandomElement();
            this.P = G.newRandomElement();
            Parameters.put("PKM", this.private_key_master.toString());
            Parameters.put("P", this.P.toString());
        }
    }

    protected Element[] Pubic_Parameters(){
        Element[] PP = new Element[2];
        PP[0] = this.P;
        PP[1] = this.Ppub;
        return PP;
    }

    protected Element generate_private_key_ID(String ID){
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
    protected IBECipherText Encryption_Basic_IBE(Element P, Element Ppub, String ID, String message){
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

    protected byte[] Decryption_Basic_IBE(Element P, Element Ppub, Element private_key_ID, IBECipherText C){
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
