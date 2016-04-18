/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import chat.Chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import chat.MessageSender;

/**
 *
 * @author Joco
 */
public class ClientOnServer extends Chat {

    private int userID;
    private Socket socket;
    private BufferedReader clientBW;
    private PrintWriter clientPW;
    private Server server;

    public ClientOnServer(Server server, int userID, Socket socket) throws IOException {
        this.server = server;
        this.userID = userID;
        this.socket = socket;

        initIO();
        setDaemon(true);
    }

    private void initIO() throws IOException {
        System.out.println("creating new client...");
        clientPW = new PrintWriter(this.socket.getOutputStream());
        sendMessage("Szerver üzenet: Welcome");
        clientBW = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        try {

            if (clientBW == null) {
                ;
            }
            while (true) {

                System.out.println("Reciever is waiting for message...");
                String input = clientBW.readLine();
                if (!input.contains(":")) {
                    if (input.equals("bye")) {
                        System.out.println(getUserName() + " Kilépett");
                        server.sendMessage("Szerver üzenet: " + getUserName() + " kilépett...");
                        closeSocket();
                    } else {
                        setUserName(input);
                    }

                } else {
                    System.out.println("Got message: " + input);
                    server.setSentBy(userID);
                    server.sendMessage(input);
                }

            }
        } catch (IOException ex) {
            System.out.println("serverSide read " + ex.getMessage());
        } finally {
            closeSocket();
        }
    }

    public void sendMessage(String message) throws IOException {
        MessageSender ms = new MessageSender(message, clientPW);
        ms.start();
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public void closeSocket() {
        try {

            if (!isClosing()) {
                clientPW.append("bye");
                clientPW.flush();
                setClosing(true);

                System.out.println("Closing client: " + getUserName() + " (Requested by the server");

            } else {
                server.removeUser(userID);
                System.out.println("Closing client: " + getUserName() + " (Requested by the client");
            }

            socket.close();
            clientBW.close();
            clientPW.close();

            System.out.println("done..");
        } catch (IOException ex) {
            System.out.println("Cannot close something: " + ex.getMessage());
        }
    }
}
