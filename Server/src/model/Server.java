/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
public class Server {
    static int port = 3000;
    Socket clientSocket = null;
    
    OutputStream os = null;
    InputStream is = null;
    
    public static int LOG_IN = 1;
    public static int SIGN_UP = 2;
    public static int COMPRESS = 3;
    
    OnReceiveListener listener = null;
    
    public Server(int port)
    {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            
            System.out.println("Server is listening on port " + port + "...");
            
            clientSocket = serverSocket.accept();
            
            System.out.println("a client ip = " + clientSocket.getLocalAddress().toString() + " connected.");
            
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void read() {
        int i = 0;
        if (is != null) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                DataInputStream dis = new DataInputStream(is);
                DataOutputStream dos = new DataOutputStream((os));
                oos = new ObjectOutputStream(os);
                
                
                while(true) {
                    try {
                        i = dis.readInt();
                        switch(i) {
                            case 1: // LOG_IN
                                String s1 = dis.readUTF();
                                String s2 = dis.readUTF();
                                String[] s = {s1, s2};
                                if(listener != null)
                                    listener.onReceive(i, s);
                                break;
//                            System.out.println(i);
                                
                                
                            case 2: // SIGN_UP
                                break;
                            case 3: // COMPRESS
                                ois = new ObjectInputStream(is);
                                File f = (File) ois.readObject();
                                if(listener != null)
                                    listener.onReceive(i, f);
                                break;
                        }
                        
//                dos.write(1);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
//        return i;
    }
    
     public void send(int i) {
        if ( os != null ) {
            try {
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeInt(i);
                
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void send(boolean b) {
        if( os != null ) {
            try {
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeBoolean(b);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void send(Object o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
//            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
   
    
    public interface OnReceiveListener {
        void onReceive(int i, Object o);
    }
    
    public void setOnReceiveListener(OnReceiveListener l) {
        this.listener = l;
    }
}
