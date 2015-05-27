/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KHANH
 */
public class Server {
    static int port = 3000;
    Socket clientSocket = null;
    
    OutputStream os = null;
    InputStream is = null;
    
    HashMap<String, String> listUserPass;
    
//    private static Server server = null;
//    
//    public static Server getInstance() {
//        if (server == null)
//            server = new Server(port);
//        return server;
//    }
    
    
    
    public Server(int port, HashMap<String, String> listUserPass)
    {
        this.listUserPass = listUserPass;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            
            System.out.println("Server is listening on port " + port + "...");
            System.out.println("" + serverSocket.getInetAddress().getHostAddress());
            
            
//            System.out.println(InetAddress.getLocalHost().getHostAddress());
            
            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("a client ip = " + clientSocket.getRemoteSocketAddress().toString() + " connected.");


                new ServerThread(clientSocket, listUserPass).start();
                
            
            }
//            is = clientSocket.getInputStream();
//            os = clientSocket.getOutputStream();
            
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
}
