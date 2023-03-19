package Client;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import Cryptography.ElGamal.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Client {

    protected String emailAddress;

    protected boolean connected;

    protected KeyPair elGamalKeyPair;

    public Client() {
        emailAddress = "";
        connected = false;
    }

    public Client(String emailAddress) {
        this.emailAddress = emailAddress;
        connected = false;
    }

    public void generateElGamalKeyPair() {
        PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParams);
        Element generator = pairing.getZr().newRandomElement();
        elGamalKeyPair = ElGamal.generateKeyPair(pairing, generator);
    }

    public void privateKeyRequest(){
        try {
            URL SERVER_URL = new URL("http://127.0.1.1:8080/service");

            URLConnection urlConn = SERVER_URL.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);

            OutputStream out = urlConn.getOutputStream();
            String emailMsg = emailAddress + "\n";
            out.write(emailMsg.getBytes());

            generateElGamalKeyPair();
            Element publicKey = elGamalKeyPair.publicKey();
            String publicKeyMsg = publicKey.toString() + "\n";
            out.write(publicKeyMsg.getBytes());

            InputStream dis = urlConn.getInputStream();
            byte[] b = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
            dis.read(b);

            String response = new String(b);
            System.out.println("message reçu du serveur:" + response);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Client c = new Client("appli.mail.crypto@gmail.com");
        c.privateKeyRequest();

    }
}
