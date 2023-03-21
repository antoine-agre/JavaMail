package Client;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import Cryptography.ElGamal.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;
import userInterface.Client;

public class HttpClient {

    protected String emailAddress;

    protected boolean connected;

    protected KeyPair elGamalKeyPair;

    protected Element ibePrivateKey;

    protected Element P;

    protected Element Ppub;

    public HttpClient() {
        emailAddress = "";
        connected = false;
    }

    public HttpClient(String emailAddress) {
        this.emailAddress = emailAddress;
        connected = false;
    }

    public void generateElGamalKeyPair() {
        PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParams);
        Element generator = pairing.getZr().newRandomElement();
        elGamalKeyPair = ElGamal.generateKeyPair(pairing, generator);
    }

    protected void generate_PMK_P(){
        PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParams);

        //Fichier de configuration pour stocker la clé secrète
        String configFilePath = "src/Cryptography/IBE/PKM.properties";
        Properties prop = new Properties();
        InputStream in;
        try {
            in = new FileInputStream(configFilePath);
            prop.load(in);
        } catch(IOException e) {e.printStackTrace();}
        String chaine = prop.getProperty("PKM");
        String chaine2 = prop.getProperty("P");
        if (chaine.length() != 0 && chaine2.length() != 0){//La clé existe et est stocké dans le fichier
            try{
                this.ibePrivateKey = pairing.getZr().newElementFromBytes(Base64.decode(chaine));
                this.P = pairing.getG1().newElementFromBytes(Base64.decode(chaine2));
                this.Ppub = pairing.getG1().newElementFromBytes(Base64.decode(chaine2));
            } catch(IOException e) {e.printStackTrace();}
        }
        else {//Elle est générée et stockée dans un fichier
            this.ibePrivateKey = pairing.getZr().newRandomElement();
            this.P = pairing.getG1().newRandomElement();
            try{
                //On convertit les Elements en string
                prop.setProperty("PKM", Base64.encodeBytes(this.ibePrivateKey.toBytes()));
                prop.setProperty("P", Base64.encodeBytes(this.P.toBytes()));
                prop.store(new FileOutputStream(configFilePath), null);
            } catch(IOException e) {e.printStackTrace();}
        }
    }
    public void privateKeyRequest(){
        try {
            URL SERVER_URL = new URL("http://127.0.1.1:8080/privateKeyRequest");

            URLConnection urlConn = SERVER_URL.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);

            OutputStream out = urlConn.getOutputStream();

            generateElGamalKeyPair();
            Element publicKey = elGamalKeyPair.publicKey();
            System.out.println(publicKey);
            out.write(publicKey.toBytes());

            out.write("\n".getBytes());

            String emailMsg = emailAddress;
            out.write(emailMsg.getBytes());

            out.close();

            InputStream dis = urlConn.getInputStream();
            byte[] b = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
            dis.read(b);

            File f = new File("macle");
            f.createNewFile();

            FileOutputStream fout = new FileOutputStream(f);

            fout.write(b);
            fout.close();
            FileInputStream fin = new FileInputStream(f);
            ObjectInputStream oin = new ObjectInputStream(fin);

            CleClient cleClient = (CleClient) oin.readObject();

            PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
            Pairing pairing = PairingFactory.getPairing(pairingParams);
            
            Element c1 = pairing.getZr().newElement();
            c1.setFromBytes(cleClient.getC1());

            Element c2 = pairing.getG1().newElement();
            c2.setFromBytes(cleClient.getC2());

            Element P = pairing.getG1().newElement();
            P.setFromBytes(cleClient.getP());

            Element Ppub = pairing.getG1().newElement();
            Ppub.setFromBytes(cleClient.getPpub());

            //TODO Fix Problem with the received string
            String response = new String(b);

        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        HttpClient c = new HttpClient("appli.mail.crypto@gmail.com");
        c.privateKeyRequest();
    }
}
