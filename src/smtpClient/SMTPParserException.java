/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

/**
 *
 * @author Prakash
 */
public class SMTPParserException extends Exception {

    /**
     * Creates a new instance of
     * <code>SMTPParserException</code> without detail message.
     */
    public SMTPParserException() {
    }

    /**
     * Constructs an instance of
     * <code>SMTPParserException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public SMTPParserException(String msg) {
        super(msg);
    }
    
}
