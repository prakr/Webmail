/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

import httpServer.ClientHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prakash
 */
public class SMTPClientThread implements Runnable{

    public SMTPClientThread(){
        
    }
    @Override
    public void run() {
        while(true){
            EmailMessage message = MessageList.findAndGetMessageToSend();
            if(message != null){
                try{
                    SMTPclient smtpC = new SMTPclient(message.getSMTPserver(),message.getFrom());
                    smtpC.sendData("HELO example.org\r\n");
                    smtpC.sendData("MAIL FROM:<"+ message.getFrom() +">\r\n");
                    smtpC.sendData("RCPT TO:<"+ message.getRcpt() +">\r\n");
                    smtpC.sendData("DATA\r\n");
                    smtpC.sendData("Subject:" + message.getSubject() + "\r\nMIME-Version: 1.0\r\nContent-Type: TEXT/PLAIN; charset=I3SO-8859-1\r\nContent-Transfer-Encoding: quoted-printable\r\n\r\n" + message.getMesssage() + "\r\n.\r\n");
                    smtpC.sendData("QUIT\r\n");
                    MessageList.removeMessageFromList(message);
                    smtpC.closeSockets();
                    //Add message to list again where status is "sucessfully sent"
                    sendStatus("Message Sent Successfully",message.getFrom());
                }
                catch (SMTPServerException ex) {
                    //sendErrorPage(ex.getMessage());
                    MessageList.removeMessageFromList(message);
                    sendStatus(ex.getMessage(),message.getFrom());
                    System.out.println(ex.getMessage());
                }
                catch (IOException ex) {
                        Logger.getLogger(SMTPClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
            }
            
            
        }
    }
    
    private void sendStatus(String message, String from ){
        try {
            SMTPclient smtpC = new SMTPclient(null, from);
            smtpC.sendData("HELO example.org\r\n");
            smtpC.sendData("MAIL FROM:<status@ik2213.lab>\r\n");
            smtpC.sendData("RCPT TO:<"+ from +">\r\n");
            smtpC.sendData("DATA\r\n");
            smtpC.sendData("Subject: Mail status\r\n\r\n" + message + "\r\n.\r\n");
            smtpC.sendData("QUIT\r\n");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SMTPServerException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
