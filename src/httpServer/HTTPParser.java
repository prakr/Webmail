/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpServer;

/**
 *
 * @author Prakash
 */
public class HTTPParser {
    
    public HTTPParser(){
        
    }
    
    public HTTPMethods getMethod(String httpMessage) throws MethodNotSupportedException{
        String[] split = httpMessage.split("/");
        switch(split[0]){
            case "GET ": 
                return HTTPMethods.GET;
            case "POST ":
                return HTTPMethods.POST;
            default:
                System.out.println("Debugging@: " + httpMessage);
                throw new MethodNotSupportedException("No such method");
        }
    }
    
    public String getRequestedPage(String httpMessage){
        String[] split = httpMessage.split("/");
        switch(split[1]){
            case " HTTP":
                return "INDEX";
            case "index.html HTTP":
                return "INDEX";
            case "status.html HTTP":
                return "STATUS";
            default:
                return "NO SUCH PAGE";
        }
    }
    
    public String getMessage(String httpMessage){
        String[] split = httpMessage.split("\n");
        //System.out.println(httpMessage);
        return split[split.length-1].replace("%40", "@").replace("+", " ").replace("%3F", "?");
        
    }
}
