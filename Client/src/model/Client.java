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
    
//    String ip = "192.168.2.101";
    static String ip = "127.0.0.1";
    static int port = 3000;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    
    InputStream is = null;
    OutputStream os = null;
    
    OnReceiveListener listener = null;
    
    private static Client client;
    
    private static byte[] arr;
    
    public static Client getInstance() {
        if(client == null)
            client = new Client(ip, port);
        return client;
    }
    
    public static void setIp(String ip) {
        Client.ip = ip;
    }
    
    private Client(String ip, int port) {
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
        final int MAX_VAL = 65536;
        int j = 0;
        try {
//            System.out.println(arr[0] + " " + arr[1]);
            while(j < arr.length / MAX_VAL) {
                os.write(arr, j*MAX_VAL, MAX_VAL);
                j++;
            }
            os.write(arr, j*MAX_VAL, arr.length - j * MAX_VAL);
//            dos.write(arr);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void read() {
            DataInputStream dis = new DataInputStream(is);
            
            while( !clientSocket.isClosed() ) {
                try {
                    int i = dis.readInt();
                    switch(i) {
                        case 1: // AUTHENTICATE ON LOGIN
                            boolean b = dis.readBoolean();
                            if ( b ) {
                                HomePage.getInfoLabel().setText("Login success!");
                                HomePage.getFrames()[1].setVisible(false);
                                Upload upload = new Upload();
                                upload.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                            read(arr);
                            System.out.println("file received by client");
                            Upload.getDownloadLabel().setText("Click to download!");

                            break;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                
            }         
    }
    
    public void read(byte arr[]) {
        final int MAX_VAL = 65536;
        
        byte[] readArr = new byte[MAX_VAL];
        int i;
         try {
             for( i = 0; i < arr.length / MAX_VAL; i++) {
                 is.read(readArr, 0, MAX_VAL);
                 System.arraycopy(readArr, 0, arr, i * MAX_VAL, MAX_VAL);
             }
             is.read(readArr, 0, arr.length - i * MAX_VAL);
             System.arraycopy(readArr, 0, arr, i * MAX_VAL, arr.length - i * MAX_VAL);
             
         } catch (IOException ex) {
             Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
