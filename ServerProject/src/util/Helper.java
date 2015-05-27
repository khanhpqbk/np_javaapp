/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedWriter;
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
import main.Main;
import model.Algorithm;
import model.Item;

/**
 *
 * @author KHANH
 */
public class Helper {
    /**
    * convert eg. 160 (10001000 (2)) -> string "10001000"
    */
   public static String byteToString(int i) {
       
       String s = "";
       int remain;
       
       while ( i != 0) {
           remain = i % 2;
           i /= 2;
           s += remain;
       }
       
       while (s.length() < 8)
           s += "0";
       
       StringBuilder sb = new StringBuilder(s);
       s = sb.reverse().toString();
       return s;
   }
   
   /**
     * eg string "00001111" -> 15 (byte type)
     * @param s
     * @return 
     */
    public static int stringToByte(String s)
    {
        int b = 0;
        char[] arr = s.toCharArray();
        for(int i = arr.length - 1; i >= 0; i--) {
//            System.out.println("" + (arr[i] - '0') );
            b += (arr[i] - '0')  * Math.pow(2, arr.length - i - 1);
        }
        
        return b;
    }
    
    public static byte[] compress(byte[] byteArr) {
         
//         File fcom = new File("C:\\output_tempcom.txt");
        byte[] arrCom = null;
        
        
//        byte[] f = byteArr;
         
//         File temp = new File("C:\\output_temp.txt");
        
        HashMap<Character, Integer> map = countSymbolsBytes(byteArr);
        ArrayList<Item> tab = new ArrayList<Item>();

        int len = byteArr.length;
        
//         System.out.println(len);
        
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
        
//        for(Item it: tab) {
//            System.out.println((int)it.ch + " " + it.freq);
//        }
        
        String[] arr = new String[256];
        for(Item item: tab) {
            arr[(int)item.ch] = item.code;
        } 
        
        
        
        arrCom = compressBytesAtOnce(byteArr, arr);

        return arrCom;
     }
     
    public static boolean authenticate(String user, String pass, Map<String, String> map) {
      
         for(Map.Entry<String, String> it: map.entrySet()) {
             if( user.equals(it.getKey()) && pass.equals(it.getValue()) )
                 return true;
         }
         return false;
     }
     
    public static boolean isUserExist(String user, Map<String, String> map) {
         for(Map.Entry<String, String> it: map.entrySet()) {
             if( user.equals(it.getKey()) )
                 return true;
         }
         return false;
     }
     
    public static void readFileUserpass(HashMap<String, String> map) {
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
     
    public static void writeFileUserpass(String[] userPass) {

        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("data\\userpass.txt", true)))) {
            out.println(userPass[0] + " " + userPass[1]);
        }catch (IOException e) {
        //exception handling left as an exercise for the reader
        }
        
     }
     
    private static int getSize(File f) {
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
    private static void compressFileAtOnce(File src, File des, String[] tabArr) {
        
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
//            System.out.println(tempCode);
            fos.write(j);
            
            // mark byte
            fos.write(len);
            
            
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    /**
     * compress and return byte array immediately (1 step only instead of 2 steps before)
     * 
     * @param File src
     * 
     * @param tabArr
     *  array represents the table
     * @return 
     *  byte array
     */
    private static byte[] compressBytesAtOnce(byte[] src, String[] tabArr) {
        
        ArrayList<Byte> listByte = new ArrayList<>();
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int i;

        String sub = "";
        int j = 0;
        String tempCode = "";

            for(i = 0; i < src.length; i++) {
//                System.out.println((int)src[i]);
                int k = src[i] ;
                if( src[i] < 0  )
                    k = 256 + src[i];
                String code = tabArr[k];
                
//                System.out.println(code);
                tempCode = tempCode.concat(code);
                
//                System.out.println(tempCode);
                
                if(tempCode.length() >= 8) {
                    sub = tempCode.substring(0, 8);
                    
//                    System.out.println(sub);
                    
                    tempCode = tempCode.substring(8, tempCode.length());
                    
                    j = Helper.stringToByte(sub);
//                    fos.write(j);
                    listByte.add((byte)j);
                }
                
            }
            
            // viet not cac bits con sot lai
            int len = tempCode.length();
//            int lenTemp = len;
            while(tempCode.length() < 8) {
                tempCode = tempCode.concat("0");
                
            }
            j = Helper.stringToByte(tempCode);
//            System.out.priclient.read();ntln(tempCode);
//            fos.write(j);
            listByte.add((byte)(j));
            // mark byte
            listByte.add((byte)len);
            
        int size = listByte.size();
        byte[] arr = new byte[size];
        for(int k = 0; k < size; k++) 
            arr[k] = listByte.get(k);

        return arr;
    }
    
    private static HashMap countSymbolsFile(File f){

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
   
   
    private static HashMap countSymbolsBytes(byte[] f){

        int i;
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
            
        for ( i = 0; i < f.length; i++ ) {
            int k = f[i];
            if(k < 0 ) {
//                if(k == -256)
//                    System.out.println(k);
                k += 256;
            }
            if (!map.containsKey( (char)k) )
                map.put( ((char)k), 1);
            else {
                map.put( ((char)k), map.get((char)k) + 1);
            }
        }
        return map;
    }

   
}
