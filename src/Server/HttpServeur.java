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
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServeur {


    public void verifyClientId(String emailAddress){

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

                    String[] clientDataTable = clientData.split("\n");
                    String emailAddress = clientDataTable[0];
                    String elGamalPublicKey = clientDataTable[1];

                    System.out.println(emailAddress);
                    System.out.println(elGamalPublicKey);

                    byte[] bytes = "bonjour client ..".getBytes();

                    he.sendResponseHeaders(200, bytes.length);

                    OutputStream os = he.getResponseBody();

                    os.write(bytes);
                    System.out.println("sending response done....");
                    os.close();
                }
            });

            server.start();
        } catch (IOException ex) {
            Logger.getLogger(HttpServeur.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

}
