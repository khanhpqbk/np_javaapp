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
import javax.swing.JFrame;
import ui.HomePage;
import ui.Signup;
import ui.Upload;

/**
 *
 * @author KHANH
 */
public class Client {
    
    String ip = "0.0.0.0";
    static int port = 3000;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    
    InputStream is = null;
    OutputStream os = null;
    
    OnReceiveListener listener = null;
    
    private static Client client = new Client(port);
    
    private static byte[] arr;
    
    public static Client getInstance() {
        return client;
    }
    
    private Client(int port) {
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
    
    public void send(byte[] arr) {
        try {
//            System.out.println(arr[0] + " " + arr[1]);
            os.write(arr);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void read() {
            DataInputStream dis = new DataInputStream(is);
            
            while(true) {
                try {
                    int i = dis.readInt();
                    switch(i) {
                        case 1: // AUTHENTICATE ON LOGIN
                            boolean b = dis.readBoolean();
                            if ( b ) {
                                HomePage.getInfoLabel().setText("Login success!");
                                HomePage.getFrames()[0].setVisible(false);
                                Upload upload = new Upload();
                                upload.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                upload.setVisible(true);
                            }
                            else {
                                HomePage.getInfoLabel().setText("Invalid username/password combination!");
                            }
                            break;
                        case 2: // SIGNUP: ERROR
                            boolean b2 = dis.readBoolean();
                            if (!b2)
                                Signup.getInfoSignupLabel().setText("User already exists! ");
                            else 
                                Signup.getInfoSignupLabel().setText("Signup completed!");
                            break;
                        case 3: // COMPRESS - FILE COMPRESSED RECEIVED
                            int len = dis.readInt();
                            arr = new byte[len];
                            is.read(arr);
                            System.out.println("file received by client");
                            Upload.getDownloadLabel().setText("Click to download!");

                            break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                
            }         
    }

        
    public void close() {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static byte[] getByteArr() {
        return arr;
    }
    
            
    public interface OnReceiveListener {
        void onReceive(int i, Object o);
    }
    
    public void setOnReceiveListener(OnReceiveListener l) {
        this.listener = l;
    }
    
}
