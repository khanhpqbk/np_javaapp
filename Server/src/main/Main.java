/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    static HashMap<String, String> listUserPass = new HashMap<>();
   
     public static void main(String[] args) {
         
//        read(listUserPass);
         
        Server s = new Server(4000);
        
        
        
        s.setOnReceiveListener(new Server.OnReceiveListener() {

            @Override
            public void onReceive(int i, Object o) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                System.out.println("server receive");
                
                switch(i) {
                    case 1: // LOGIN
                        String[] userPass = (String[]) o;
                        s.send(1);
                        s.send(authenticate(userPass[0], userPass[1], listUserPass));
                        
                        break;
                    case 2: // SIGNUP
                        break;
                    case 3: // COMPRESS
                        File f = (File) o;
                        File fcom = compress(f);
                        s.send(Server.COMPRESS);
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
         
         File fcom = new File("C:\\Users\\KHANH\\Desktop\\output_tempcom.txt");
         
         File temp = new File("C:\\Users\\KHANH\\Desktop\\output_temp.txt");
        
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
        
        HashMap<Character, String> tabMap = new HashMap<>();
        for (Item item: tab) {
            tabMap.put(item.ch, item.code);
        }
        
        // bien doi cac ki tu abc thong thuong thanh chuoi string dang. "101110101010"
        compressFile(f, temp, tabMap);     

        
        // bien doi sang dang nhi phan tuong ung va viet chuoi trong file nay vao file
        writeFileCompressedFile(temp, fcom);     

        return fcom;
     }
     
     static boolean authenticate(String user, String pass, HashMap<String, String> map) {
         // TODO: unimplemented
         return true;
     }
     
     static void readFileUserpass(HashMap<String, String> map) {
         // TODO: unimplemented
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("userpass.txt"));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
    
     static void writeFileCompressedFile(File src, File des){
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            int integer;
            String sub = "";
//            byte b;
//            char c;
            int len = 0;
            int j = 0;
            int i;
            
            while ( (i = fis.read()) != -1 ) {
                sub += ((char)i);
                if (sub.length() == 8) {
                    integer = Helper.stringToByte(sub);
                    fos.write(integer);
                    sub = "";
                }
  
            }
            
            // nen' not may bits con sot lai cua file
            
            len = sub.length();
            while (sub.length() < 8) {
                sub = sub.concat("0");
            }
            integer = Helper.stringToByte(sub);
            fos.write(integer);
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

   
    /**
     *
     * 
     * @param src origin
     * @param des destination 
     * @param tabMap
     *  HashMap represents the table
     * @return 
     *  compressed string
     */
    static void compressFile(File src, File des, HashMap<Character, String> tabMap) {
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int i;
        char[] arr;
        
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(des);
            while( (i = fis.read()) != -1) {
                String code = tabMap.get((char) i);
                arr = code.toCharArray();
                for(char c: arr)
                    fos.write(c);
            }
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
