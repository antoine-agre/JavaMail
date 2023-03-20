package Server;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import Cryptography.IBE.*;
import Cryptography.ElGamal.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class HttpServeur {

    static IBEscheme ibe = new IBEscheme();


    /**
     * verifie si l'identite specifiee est bien celle du client en envoyant un mail de confirmation
     *
     * @param emailAddress l'adresse mail sur laquelle le message de verification est envoye
     * @return true si l'identite est verifiee, false sinon
     */
    public boolean verifyClientId(String emailAddress) {

        return true;
    }

    public static void main(String[] args) {

        try {
            System.out.println("my address:" + InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);


            HttpServer server = HttpServer.create(s, 1000);
            System.out.println(server.getAddress());
            server.createContext("/privateKeyRequest", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {
                    byte[] bytes1 = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                    he.getRequestBody().read(bytes1);
                    String clientData = new String(bytes1);


                    PairingParameters pairingParams = PairingFactory.getPairingParameters("params/curves/a.properties");
                    Pairing pairing = PairingFactory.getPairing(pairingParams);
                    Element elGamalPublicKey = pairing.getZr().newElement();
                    elGamalPublicKey.setFromBytes(bytes1);

                    System.out.println(elGamalPublicKey);

                    byte[] bytes = "bonjour client ..".getBytes();

                         he.sendResponseHeaders(200, bytes.length);

                         OutputStream os = he.getResponseBody();

                         os.write(bytes);
                         System.out.println("sending response done....");
                         os.close();
                    //TODO Verify client ID
/*
                    Element privateKey = ibe.generate_private_key_ID(emailAddress);

                    CipherText encryptedPrivateKey = encrypt(privateKey, keyPair.publicKey(), pairing,generator);


                    Element[] PP = ibe.Public_Parameters();


                    System.out.println(privateKey.toString());
                    System.out.println(PP[0].toString());
                    System.out.println(PP[1].toString());


                    byte[] privateKeyBytes = privateKey.toString().getBytes();
                    byte[] P = PP[0].toString().getBytes();
                    byte[] Ppub = PP[1].toString().getBytes();


                    he.sendResponseHeaders(200, privateKeyBytes.length + P.length + PP.length);

                    OutputStream os = he.getResponseBody();

                    os.write(privateKeyBytes);
                    os.write(P);
                    os.write(Ppub);
                    System.out.println("sending response done....");
                    os.close();

 */
                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

}
