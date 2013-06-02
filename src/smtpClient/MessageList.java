/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

import java.util.ArrayList;


/**
 *
 * @author Prakash
 */
public class MessageList {
    private static ArrayList<EmailMessage> list = new ArrayList<>();
    
    private static Object synchronousLock = new Object();
    
    private MessageList(){}
    
    public static synchronized void addMessageToList(EmailMessage message){
        list.add(message);
    }
    
    public static synchronized void removeMessageFromList(EmailMessage message){
        list.remove(message);
    }
    
    public static synchronized ArrayList<EmailMessage> getMessageList(){
        return list;
    }
    
    public static synchronized EmailMessage findAndGetMessageToSend(){
        for(EmailMessage m : list){
            if((m.getDate() <= System.currentTimeMillis()) && (m.getStatus().equals("ToBeSent"))){
                return m;
            }
        }
        return null;
    }
}
