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
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.Main.COMPRESS;
import static main.Main.LOG_IN;
import static main.Main.SIGN_UP;
import util.Helper;

/**
 *
 * @author KHANH
 */
public class ServerThread extends Thread{
     Socket clientSocket = null;
        InputStream is = null;
        OutputStream os = null;
    HashMap<String, String> listUserPass = new HashMap<>();
        
        
        public ServerThread(Socket clientSocket, HashMap<String, String> listUserPass) {
            this.clientSocket = clientSocket;
            this.listUserPass = listUserPass;
        }
        
        @Override
        public void run() {
            try {
                is = clientSocket.getInputStream();
                os = clientSocket.getOutputStream();
            } catch (IOException ex) {
//                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            read();     
        }
        
        public void read() {
        int i = 0;
        if (is != null) {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

                DataInputStream dis = new DataInputStream(is);
                DataOutputStream dos = new DataOutputStream((os));

                
                
                while( !clientSocket.isClosed()) {

                try {
                    i = dis.readInt();
                    switch(i) {
                        case 1: // LOG_IN
                            String s1 = dis.readUTF();
                            String s2 = dis.readUTF();
                            String[] s = {s1, s2};

                            String[] userPass = (String[]) s;
                            send(LOG_IN);
                            Helper.readFileUserpass(listUserPass);
                            boolean b = Helper.authenticate(userPass[0], userPass[1], listUserPass);
                            send(b);
                            
                            break;

                            
                        case 2: // SIGN_UP
                            String s3 = dis.readUTF();
                            String s4 = dis.readUTF();
                            String[] ss = {s3, s4};

                            String[] userPassWrite = (String[]) ss;
                            // signup failure
                            Helper.readFileUserpass(listUserPass);
                            if( Helper.isUserExist(userPassWrite[0], listUserPass) ) {
                                send(SIGN_UP);
                                send(false);

                            } else {
                                send(SIGN_UP);
                                send(true);
                                Helper.writeFileUserpass(userPassWrite);
                            }
                            break;
                            
                        case 3: // COMPRESS
                            int len = dis.readInt();
                            byte[] arr = new byte[len];
                            
                            System.out.println(len);
                            read(arr);
                            
//                            for(int k = 0; k < len; k++)
//                                if(arr[k] == 0) {
//                                    System.out.println("index = " + k);
//                                    break;
//                                }
                            
                            System.out.println("Server has received  file from client.");
//                            System.out.println(len);
                            
                            byte[] arrCom = Helper.compress(arr);
                            
                            System.out.println("size of compressed file: " + arrCom.length);
                            send(COMPRESS);
                            send(arrCom.length);
                            send(arrCom);
                            System.out.println("Server has sent compressed file to client.");
//                            if(listener != null)
//                                listener.onReceive(i, f);
                            break;
                    }
                } catch (IOException ex) {
//                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
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
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
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
             Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
   
    
    public interface OnReceiveListener {
        void onReceive(int i, Object o);
    }
    
    public void setOnReceiveListener(OnReceiveListener l) {
        this.listener = l;
    }
    
    OnReceiveListener listener = null;
        
    
}
