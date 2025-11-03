/**
 * @author: Clarence
 */

package view;

public class QuitHandler extends RuntimeException {
    public QuitHandler() { super(); }
    public QuitHandler(String msg) { super(msg); }   
}
