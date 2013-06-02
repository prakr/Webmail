/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

/**
 *
 * @author Prakash
 */
public class SMTPParser {
    public SMTPParser(){
        
    }
    
    public String getMailfrom(String Message) throws SMTPParserException{
         String[] split = Message.split("&");
         if(split[0].charAt(split[0].length()-1) == '='){
              System.out.println("missing");
              throw new SMTPParserException("From address missing"); 
          }
          else{
              split = split[0].split("=");
              return split[1]; 
          }
    }
    public String getRcptto(String Messsage) throws SMTPParserException{
        String[] split = Messsage.split("&");
        if(split[1].charAt(split[1].length()-1)=='='){
                 System.out.println("missing");
              throw new SMTPParserException("Rcptto address missing"); 
          }
        else{
            split = split[1].split("=");
            return split[1];
        }
    }
    public String getSubject(String Message) throws SMTPParserException{
        String[] split = Message.split("&");
        if(split[2].charAt(split[2].length()-1)=='='){
              throw new SMTPParserException("Subject is missing"); 
          }
        else{
            split = split[2].split("=");
            return split[1].replace("%C5", "=C5").replace("%C4","=C4").replace("%D6","=D6").replace("%E5","=E5").replace("%E4","=E4").replace("%F6","=F6");
        }
    }
    
    public String getMessage(String Message) throws SMTPParserException{
        //System.out.println(Message);
        String[] split = Message.split("&");
        if(split[5].charAt(split[5].length()-1)=='='){
             throw new SMTPParserException("Body message is missing"); 
         }
        else{
            split = split[5].split("=");
            return split[1].replace("%C5", "=C5").replace("%C4","=C4").replace("%D6","=D6").replace("%E5","=E5").replace("%E4","=E4").replace("%F6","=F6").replace("%0D","=0D").replace("%0A","=0A");
        }
    }
    
   public String getSMTPserver(String Message){
        String[] split = Message.split("&");
        split = split[3].split("=");
        if(split.length>1) {
           return split[1];
       }
        else {
           return null;
       }
    }
   
   public String getSendingTime(String Message){
       String[] split = Message.split("&");
       split = split[4].split("=");
       if(split.length > 1) {
           return split[1];
       }
       else {
           return null;
       }
   }
  }
