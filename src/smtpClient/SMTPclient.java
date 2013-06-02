/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.*;


/**
 *
 * @author Prakash
 */
public class SMTPclient {
    private Socket clientSocket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    
    public SMTPclient(String smtpServer,String from) throws IOException {    
        initializeSMTPclient(smtpServer,from);
        dataInput = new DataInputStream(clientSocket.getInputStream());
        dataOutput= new DataOutputStream(clientSocket.getOutputStream());
        readData();
    }
    
    public void sendData(String smtpData) throws IOException, SMTPServerException {
        dataOutput.writeBytes(smtpData);
        String tmp = readData();
        switch((tmp.split(" "))[0]){
            case "250":
                break;
            case "354":
                break;
            case "221":
                break;
            default:
                throw new SMTPServerException(tmp);     
        }
       } 
     
     private String readData() throws IOException{
         byte[] data = new byte[512];
         dataInput.read(data);
         return new String(data,Charset.defaultCharset());    
     }
     
    
    private void initializeSMTPclient(String smtpServer,String from) throws IOException{
        if(smtpServer == null){
            Record[] r = new Lookup((from.split("@"))[1], Type.MX).run();
            System.out.println(((MXRecord)r[0]).getTarget().toString());
            if(r.length < 1){
             //error, no record found.
             }
             clientSocket = new Socket(((MXRecord)r[0]).getTarget().toString(),25);
        }
        else{
            clientSocket = new Socket(smtpServer,25);
        }
      // clientSocket = new Socket("192.168.3.11",25);
    } 
    
    public void closeSockets(){
        try {
            dataInput.close();
            dataOutput.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SMTPclient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
