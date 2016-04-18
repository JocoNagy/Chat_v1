/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;

/**
 *
 * @author Joco
 */
public abstract class Chat extends Thread{
    private boolean closing;
    private String userName;

    public Chat() {
    }

    public Chat(String userName) {
        this.userName = userName;
    }
    
    @Override
    public abstract void run();
    public abstract void sendMessage(String message) throws IOException;
    public abstract void closeSocket();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isClosing() {
        return closing;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
    }


    
    
    
}
