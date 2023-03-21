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
import userInterface.Client;

public class HttpClient {

    protected String emailAddress;

    protected boolean connected;

    protected KeyPair elGamalKeyPair;

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

    public void privateKeyRequest(){
        try {
            URL SERVER_URL = new URL("http://192.168.230.64:8080/privateKeyRequest");

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
            System.out.println(Integer.parseInt(urlConn.getHeaderField("Content-length")));
            byte[] b = new byte[Integer.parseInt(urlConn.getHeaderField("Content-length"))];
            dis.read(b);

            //TODO Fix Problem with the received string
            String response = new String(b);
            System.out.println("message reçu du serveur:" + response);

        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        HttpClient c = new HttpClient("appli.mail.crypto@gmail.com");
        c.privateKeyRequest();
    }
}
