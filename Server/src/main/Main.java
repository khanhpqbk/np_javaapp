/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Algorithm;
import util.Helper;
import model.Item;
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
         
        readFileUserpass(listUserPass);
         
        Server s = new Server(3000);
        
        
        
        s.setOnReceiveListener(new Server.OnReceiveListener() {

            @Override
            public void onReceive(int i, Object o) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                System.out.println("server receive");
                
                switch(i) {
                    case 1: // LOGIN
                        String[] userPass = (String[]) o;
                        s.send(LOG_IN);
                        readFileUserpass(listUserPass);
                        boolean b = authenticate(userPass[0], userPass[1], listUserPass);
                        s.send(b);
                        
                        break;
                    case 2: // SIGNUP
                        String[] userPassWrite = (String[]) o;
                        // signup failure
                        readFileUserpass(listUserPass);
                        if( isUserExist(userPassWrite[0], listUserPass) ) {
                            s.send(SIGN_UP);
                            s.send(false);
                            
                        } else {
                            s.send(SIGN_UP);
                            s.send(true);
                            writeFileUserpass(userPassWrite);
                        }
                        break;
                    case 3: // COMPRESS
                        File f = (File) o;
                        File fcom = compress(f);
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
     
     static File compress(File f) {
         
         File fcom = new File("C:\\output_tempcom.txt");
         
//         File temp = new File("C:\\output_temp.txt");
        
        HashMap<Character, Integer> map = countSymbolsFile(f);
        ArrayList<Item> tab = new ArrayList<Item>();

        int len = getSize(f);
        
//         System.out.println(getSize(f));
        
        // xay dung Item.char, Item.freq cho tung item
        // chuyen HashMap thanh arrayList
        for (Map.Entry<Character, Integer> item : map.entrySet())
        {
            Float freq = item.getValue()/(float)len;
            tab.add(new Item(item.getKey(), freq));
        }

        // lap table mapping character -> code de look up sau nay
        // build Item.code cho tung item dua vao freq
        Algorithm fano = new Algorithm(tab);
        
       
        String[] arr = new String[256];
        for(Item item: tab) {
            arr[(int)item.ch] = item.code;
        } 
        
         compressFileAtOnce(f, fcom, arr);

        return fcom;
     }
     
     static boolean authenticate(String user, String pass, HashMap<String, String> map) {
      
         for(HashMap.Entry<String, String> it: map.entrySet()) {
             if( user.equals(it.getKey()) && pass.equals(it.getValue()) )
                 return true;
         }
         return false;
     }
     
     static boolean isUserExist(String user, HashMap<String, String> map) {
         for(HashMap.Entry<String, String> it: map.entrySet()) {
             if( user.equals(it.getKey()) )
                 return true;
         }
         return false;
     }
     
     static void readFileUserpass(HashMap<String, String> map) {
         Path path = Paths.get("data\\userpass.txt");
        try {
            List<String> list = Files.readAllLines(path, StandardCharsets.US_ASCII);
            for(String s: list) {
                StringTokenizer st = new StringTokenizer(s, " ");
                map.put(st.nextToken(), st.nextToken());
            }
            
//            for(HashMap.Entry<String, String> it: map.entrySet()) {
//                System.out.println(it.getKey() + it.getValue());
//            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
         
     }
     
     static void writeFileUserpass(String[] userPass) {
//        try {
//            List<String> list = new ArrayList<>();
//            list.add(userPass[0] + " " + userPass[1]);
//            Path path = Paths.get("data\\userpass.txt");
//            Files.write(path, list, StandardCharsets.US_ASCII);
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data\\userpass.txt", true)))) {
            out.println(userPass[0] + " " + userPass[1]);
        }catch (IOException e) {
        //exception handling left as an exercise for the reader
        }
        
     }
     
     static int getSize(File f) {
        int j = 0, i;
        try {
            FileInputStream fis = new FileInputStream(f);
            
            while ( (i = fis.read()) != -1 ) {
                j++;
            }
            fis.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return j;
    }
     
     /**
     * compress and write to file immediately (1 step only instead of 2 steps before)
     * 
     * @param File src
     * @param File des
     * @param tabArr
     *  array represents the table
     * @return 
     *  compressed string
     */
    static void compressFileAtOnce(File src, File des, String[] tabArr) {
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int i;
        char[] arr;
        String sub = "";
        int j = 0;
        String tempCode = "";
        
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            while( (i = fis.read()) != -1) {
                String code = tabArr[i];
                tempCode = tempCode.concat(code);
                
//                System.out.println(tempCode);
                
                if(tempCode.length() >= 8) {
                    sub = tempCode.substring(0, 8);
                    
//                    System.out.println(sub);
                    
                    tempCode = tempCode.substring(8, tempCode.length());
                    
                    j = Helper.stringToByte(sub);
                    fos.write(j);
                }
                
            }
            
            // viet not cac bits con sot lai
            int len = tempCode.length();
//            int lenTemp = len;
            while(tempCode.length() < 8) {
                tempCode = tempCode.concat("0");
                
            }
            j = Helper.stringToByte(tempCode);
            System.out.println(tempCode);
            fos.write(j);
            
            // mark byte
            fos.write(len);
            
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    

   
   
    static HashMap countSymbolsFile(File f){

        FileInputStream fis = null;
        int i;
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        
        try {
            fis = new FileInputStream(f);
            
            while ( (i = fis.read()) != -1 ) {
                
                if (!map.containsKey((char)i))
                    map.put((char)i, 1);
                else {
                    map.put((char)i, map.get((char)i)+1);
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


        return map;
    }

   
}
