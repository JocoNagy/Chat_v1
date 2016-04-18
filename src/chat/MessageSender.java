package chat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.PrintWriter;

/**
 *
 * @author Joco
 */
public class MessageSender extends Thread {

//    private Map<Integer, Client> users;
    private String message;
//    private String senderName;
    PrintWriter pw;

    public MessageSender(String message, PrintWriter pw) {
//        this.senderName = senderName;
        this.pw = pw;
        this.message = message;
        message = null;
        setDaemon(true);
    }

    @Override
    public void run() {
        if (message != null) {
            pw.println(message);
            pw.flush();
        }

    }

}
