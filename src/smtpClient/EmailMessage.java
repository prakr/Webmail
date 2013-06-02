/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

/**
 *
 * @author Prakash
 */
public class EmailMessage {
    private String from;
    private String rcpt;
    private String status;
    private String subject;
    private Long date;
    private String message;
    private String smtpServer;
    
    public EmailMessage(String from ,String rcpt,String status,String subject,String message, Long sendingDelay,String smtpServer){
        this.from = from;
        this.rcpt = rcpt;
        this.status = status;
        this.subject = subject;
        this.message = message;
        this.date = sendingDelay;
        this.smtpServer = smtpServer;
    }
    
    public String getFrom(){
        return from;
    }
    
     public String getRcpt(){
         return rcpt;
     }
     
     public String getStatus(){
         return status;
     }
    public String getSubject(){
        return subject;
    }
    public String getMesssage(){
        return message;
    }
    public Long getDate(){
        return date;
    }
    
   public String getSMTPserver(){
       return smtpServer;
   }
}
