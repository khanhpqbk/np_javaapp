/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KHANH
 */
public class Client {
    String ip = "192.168.2.111";
    int port = 3000;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    
    InputStream is = null;
    OutputStream os = null;
    
    OnReceiveListener listener = null;
    
    public Client(int port) {
        try {
            clientSocket = new Socket(ip, port);
            
            System.out.println("Client connected to server on port " + port + ".");
            
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(int i) {
        if ( os != null ) {
            try {
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeInt(i);
                
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void send(String[] s) {
        if ( os != null ) {
            try {
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF(s[0]);
                dos.writeUTF(s[1]);
                
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void send(Object o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
//            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void read() {
        ObjectInputStream ois = null;
        
            DataInputStream dis = new DataInputStream(is);
            
            while(true) {
                try {
                    int i = dis.readInt();
                    switch(i) {
                        case 1: // boolean
                            boolean b = dis.readBoolean();
                            if (listener != null)
                                listener.onReceive(0, b);
                            break;
                        case 3: // file
                            ois = new ObjectInputStream(is);
                            Object o = ois.readObject();
                            
//                            System.out.println("client receive file");
                            if (listener != null)
                                listener.onReceive(0, o);
//                            ois.close();
                            break;
                    }
                    
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }                
    }
        
//        finally {
//            try {
//                ois.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    
            
    public interface OnReceiveListener {
        void onReceive(int i, Object o);
    }
    
    public void setOnReceiveListener(OnReceiveListener l) {
        this.listener = l;
    }
    
}
