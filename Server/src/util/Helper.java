/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

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
}
