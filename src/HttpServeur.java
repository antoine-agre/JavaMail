/*
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
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author imino
 */
public class HttpServeur {


    public static void main(String[] args) {

        try {
            // InetSocketAddress s = new InetSocketAddress("localhost", 8080);
            System.out.println("my address:" + InetAddress.getLocalHost());
            InetSocketAddress s = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
            //  InetSocketAddress s = new InetSocketAddress("localhost", 8080);


            HttpServer server = HttpServer.create(s, 1000);
            System.out.println(server.getAddress());
            server.createContext("/service", new HttpHandler() {
                public void handle(HttpExchange he) throws IOException {
                    byte[] bytes1 = new byte[Integer.parseInt(he.getRequestHeaders().getFirst("Content-length"))];
                    he.getRequestBody().read(bytes1);
                    String msg = new String(bytes1);

                    System.out.println("message reçu " + msg);

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
