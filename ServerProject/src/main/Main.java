/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;


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

         
        util.Helper.readFileUserpass(listUserPass);
         
        Server s = new Server(3000, listUserPass);
        
        
        
     }
     
     
}
