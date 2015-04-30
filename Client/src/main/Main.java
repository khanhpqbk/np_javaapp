/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import javax.swing.JFrame;
import model.Client;
import ui.HomePage;
import ui.Upload;

/**
 *
 * @author KHANH
 */
public class Main {
    
    static Client client;
    static File fcom;
    
    public static int LOG_IN = 1;
    public static int SIGN_UP = 2;
    public static int COMPRESS = 3;
    
    public static void main(String[] args) {
//        new HomePage().show();
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePage().setVisible(true);
            }
        });
        
        client = new Client(3000);
        
        client.setOnReceiveListener(new Client.OnReceiveListener() {

            @Override
            public void onReceive(int i, Object o) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                System.out.println("receive!");
                if (o instanceof Boolean) { 
                    Boolean b = (Boolean) o;
                    if ( b ) {
                        HomePage.getInfoLabel().setText("Login success!");
                        Upload upload = new Upload();
                        upload.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        upload.setVisible(true);
                    }
                    else {
                        HomePage.getInfoLabel().setText("Login failure!");
                    }
                } else if (o instanceof File) {
                    System.out.println("file received by client");
                    fcom = (File) o;
                    Upload.getDownloadLabel().setText("Click to download!");
//                    receivedFile = true;
//                    System.out.println(fcom.getName());
                }
                
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                client.read();
            }
        }).start();
    }
    
    public static Client getClient() {
        return client;
    }
    
    public static File getFileCompressed() {
        return fcom;
    }
}
