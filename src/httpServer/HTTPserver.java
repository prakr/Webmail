/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import smtpClient.SMTPClientThread;

/**
 *
 * @author Prakash
 */
public class HTTPserver {
   private static ServerSocket incommingRequestSocket;
    
   public static void main(String[] args) throws IOException{
       initializeHTTPServer();
       (new Thread(new SMTPClientThread())).start();
       int i = 1;
       while(true){
           
           (new Thread(new ClientHandler(incommingRequestSocket.accept()))).start();
           System.out.println("Threads created: " + i++);
           try {
               Thread.sleep(20);
           } catch (InterruptedException ex) {
               Logger.getLogger(HTTPserver.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
   }
   
   private static void initializeHTTPServer() throws IOException{
       incommingRequestSocket = new ServerSocket(4711);
   }
}
