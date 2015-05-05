/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;


import java.io.File;
import java.util.HashMap;
import util.Helper;
import model.Server;


/**
 *
 * @author KHANH
 */
public class Main {
    
    public static int LOG_IN = 1;
    public static int SIGN_UP = 2;
    public static int COMPRESS = 3;
    
    static HashMap<String, String> listUserPass = new HashMap<>();
   
     public static void main(String[] args) {

         
        Helper.readFileUserpass(listUserPass);
         
        Server s = Server.getInstance();
        
        
        
        s.setOnReceiveListener(new Server.OnReceiveListener() {

            @Override
            public void onReceive(int i, Object o) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                System.out.println("server receive");
                
                switch(i) {
                    case 1: // LOGIN
                        String[] userPass = (String[]) o;
                        s.send(LOG_IN);
                        Helper.readFileUserpass(listUserPass);
                        boolean b = Helper.authenticate(userPass[0], userPass[1], listUserPass);
                        s.send(b);
                        
                        break;
                    case 2: // SIGNUP
                        String[] userPassWrite = (String[]) o;
                        // signup failure
                        Helper.readFileUserpass(listUserPass);
                        if( Helper.isUserExist(userPassWrite[0], listUserPass) ) {
                            s.send(SIGN_UP);
                            s.send(false);
                            
                        } else {
                            s.send(SIGN_UP);
                            s.send(true);
                            Helper.writeFileUserpass(userPassWrite);
                        }
                        break;
                    case 3: // COMPRESS
                        File f = (File) o;
                        File fcom = Helper.compress(f);
                        s.send(COMPRESS);
                        s.send(fcom);
                        System.out.println("Server has sent compressed file to client.");
                        break;
                }
            }
        });
        
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                s.read();
//            }
//        }).start();
        
    }
     
     
}
