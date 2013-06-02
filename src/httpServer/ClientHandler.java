/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpServer;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import smtpClient.EmailMessage;
import smtpClient.MessageList;
import smtpClient.SMTPParser;
import smtpClient.SMTPParserException;
import smtpClient.SMTPServerException;
import smtpClient.SMTPclient;

/**
 *
 * @author Prakash
 */
public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedInputStream bffrdInput;
    private DataOutputStream dataOutput;
    private byte[] inputBytes;
    private String index_webpage = "HTTP/1.0 200 OK\r\nConnection: close\r\nContent-Type: text/html\r\n\r\n<html><head><meta http-equiv='Content-Type' content='text/html; charset=ISO-8850-1'><title>IK2213 - Webmail project</title></head><body><h1>Index</h1><h2> Post form</h2>"
            + "<form method='post' action='http://localhost:4711'>From:     <input type='text' size =40 name='from'></br>"
            + "To:       <input type='text' size =40 name='to'></br>Subject:  <input type='text' size =40 name='subject'></br>"
            + "SMTP server:    <input type='text' size =40 name='smtp_server'></br>Sending delay:    <input type='text' size =40 name='delay'></br>"
            + "Message:       <textarea cols='40' rows='10' name='message'></textarea></br>"
            + "<input type=submit value='Submit Post'></form></body></html>\r\n";
    private String status_webpage;
    private String not_found = "HTTP/1.0 200 OK\r\nConnection: close\r\nContent-Type: text/html\r\n\r\n<html><body><h1>404 Not found</h1></body></html>\r\n";
    private HTTPParser parser;
    
    public ClientHandler(Socket clientSocket) throws IOException{
        this.clientSocket = clientSocket;
        bffrdInput = new BufferedInputStream(this.clientSocket.getInputStream());
        dataOutput = new DataOutputStream(this.clientSocket.getOutputStream());
        inputBytes = new byte[1024];
        parser = new HTTPParser();
    }
    
    @Override
    public void run() {
        try {
            //System.out.println("in run");
            int nBytes = bffrdInput.read(inputBytes);
            handleHTTPMessage(new String(inputBytes, Charset.defaultCharset()));
            bffrdInput.close();
            dataOutput.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MethodNotSupportedException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void handleHTTPMessage(String httpMessage) throws MethodNotSupportedException, IOException{
        // System.out.println(httpMessage);
        switch(parser.getMethod(httpMessage)){
            case GET:
                 handleHTTPGetMessage(httpMessage);
                 // status
                break;
            case POST:
                String message = parser.getMessage(httpMessage);
                String from = "null";
                SMTPParser smtpParser = new SMTPParser();
                 try {
                    if(smtpParser.getSendingTime(message) == null){
                        from = smtpParser.getMailfrom(message);
                        SMTPclient smtpC = new SMTPclient(smtpParser.getSMTPserver(message),from);
                        smtpC.sendData("HELO example.org\r\n");
                        smtpC.sendData("MAIL FROM:<"+ from +">\r\n");
                        smtpC.sendData("RCPT TO:<"+ smtpParser.getRcptto(message) +">\r\n");
                        smtpC.sendData("DATA\r\n");
                        System.out.println(org.apache.tools.ant.util.DateUtils.getDateForHeader());
                        //MIME-Version: 1.0\r\n\Content-Type: TEXT/PLAIN; charset=I3SO-8859-1\r\nContent-Transfer-Encoding: quoted-printable\r\n\r\n
                        smtpC.sendData("Date: " + org.apache.tools.ant.util.DateUtils.getDateForHeader() + "\r\nFrom: <" + from + ">\r\n" + "Subject: =?ISO-8859-1?Q?" + smtpParser.getSubject(message) + "?=" + "\r\nMIME-Version: 1.0\r\nContent-Type: TEXT/PLAIN; charset=ISO-8859-1\r\nContent-Transfer-Encoding: quoted-printable\r\n\r\n" + smtpParser.getMessage(message) + "\r\n.\r\n");
                        smtpC.sendData("QUIT\r\n");
                        successPage("Message Sent Successfully");
                        sendStatus("Message Sent Successfully",smtpParser.getMailfrom(message));
                        smtpC.closeSockets();
                        
                    } else {
                        long sendingTime = (Long.parseLong(smtpParser.getSendingTime(message))*1000) + System.currentTimeMillis();
                        System.out.println(sendingTime - System.currentTimeMillis());
                        MessageList.addMessageToList(new EmailMessage(smtpParser.getMailfrom(message), smtpParser.getRcptto(message), "ToBeSent", smtpParser.getSubject(message), smtpParser.getMessage(message), sendingTime,"mail.ik2213.lab"));
                        successPage("Message queued");
                       }
                } catch (SMTPParserException e){
                    sendErrorPage(e.getMessage());
                    if(!from.equals("null")) {
                        sendStatus(e.getMessage(), from);
                    }
                } catch (SMTPServerException ex) {
                    sendErrorPage(ex.getMessage());
                    sendStatus(ex.getMessage(),from);
                    System.out.println(ex.getMessage());
                }
        }
    }
    
    private void handleHTTPGetMessage(String httpGetMessage){
        switch(parser.getRequestedPage(httpGetMessage)){
            case "INDEX":
                sendIndexResponse();
                break;
            case "STATUS":
                sendStatusResponse();
                break;
            default:
                sendNotFound();
                break;
        }
    }
    
    private void sendIndexResponse(){
        try {
            dataOutput.writeBytes(index_webpage);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendNotFound() {
        try {
            dataOutput.writeBytes(not_found);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendStatus(String message,String from ){
        try {
            SMTPclient smtpC = new SMTPclient(null ,from);
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
    
    private void sendErrorPage(String errorMsg) {
        try {  
            dataOutput.writeBytes("HTTP/1.0 200 OK\r\nConnection: close\r\nContent-Type: text/html\r\n\r\n<html><body><h1>"+errorMsg+"</h1></body></html>\r\n");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void successPage(String successMsg) {
        try {
            System.out.println("HEJ, NULL!");
            dataOutput.writeBytes("HTTP/1.0 200 OK\r\nConnection: close\r\nContent-Type: text/html\r\n\r\n<html><body><h1>"+successMsg+"</h1></body></html>\r\n");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendStatusResponse() {
        try {
        ArrayList<EmailMessage> message = MessageList.getMessageList();
    	status_webpage = "HTTP/1.0 200 OK\r\nConnection: close\r\nContent-Type: text/html\r\n\r\n"
	         + "<html>\r\n"
	         + "<head>\r\n"
	         + "<meta charset=\"utf-8\" />\r\n"
	         + "<title>Status</title>\r\n"
	         + "</head>\r\n"
	         + "<body>\r\n"
                 + "<table border=\"1\">\r\n"
                 + "<tr><th>TO</th><th>From</th><th>Subject</th><th>Delivery Time</th></tr>";
    	
        for(EmailMessage m : message){
        status_webpage += "<tr>\r\n";
        status_webpage += "<td>" + m.getFrom() + "</td>\r\n";
        status_webpage += "<td>" + m.getRcpt() + "</td>\r\n";
        status_webpage += "<td>" + m.getSubject()+ "</td>\r\n";
        status_webpage += "<td>" + ((m.getDate()-System.currentTimeMillis())/1000) + "</td>\r\n";
        status_webpage += "</tr>\r\n";
        }
        status_webpage += "</table>\r\n</body>\r\n</html>\r\n";
        dataOutput.writeBytes(status_webpage);
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
