/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smtpClient;

/**
 *
 * @author Prakash
 */
public class SMTPServerException extends Exception {

    /**
     * Creates a new instance of
     * <code>SMTPServerException</code> without detail message.
     */
    public SMTPServerException() {
    }

    /**
     * Constructs an instance of
     * <code>SMTPServerException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public SMTPServerException(String msg) {
        super(msg);
    }
}
