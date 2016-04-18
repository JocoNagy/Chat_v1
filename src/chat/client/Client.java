/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.client;

import chat.Chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import chat.MessageSender;

/**
 *
 * @author Joco
 */
public class Client extends Chat {

    private Socket socket;
    private BufferedReader clientBW;
    private PrintWriter clientPW;
    private JList<String> userList;

    JTextArea textArea;

    public Client(String ipAddress, String userName, JTextArea textArea, JList<String> userList) throws IOException {
        super(userName);
        this.textArea = textArea;
        this.userList = userList;
        System.out.println("ip: " + ipAddress);
        socket = new Socket(ipAddress, 8888);

        System.out.println("csatlakozva...");
        clientPW = new PrintWriter(socket.getOutputStream());
        setDaemon(true);
    }

    @Override
    public void run() {

        try {
            sendMessage(getUserName());
            if (clientBW == null) {
                clientBW = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));;
            }
            while (true) {

                System.out.println("Reciever is waiting for message...");

                String input = clientBW.readLine();
                if (input.contains(":")) {
                    System.out.println("Got message: " + input);
                    textArea.append(input + "\n");
                    System.out.println("message printed to TArea");

                } else if (input.equals("bye")) {
                    JOptionPane.showMessageDialog(textArea, "Szerver leállás", "A szerver leállt", JOptionPane.ERROR_MESSAGE);
                    setClosing(true);
                    closeSocket();
                } else {
                    System.out.println("getting userList");
                    userList.setListData(input.split(","));

                }
            }

        } catch (IOException ex) {
            if (!isClosing()) {
                if (ex.getMessage().equals("Connection reset")) {

                    JOptionPane.showMessageDialog(textArea, "Kapcsolat megszakadt!", "Kapcsolat megszakadt!", JOptionPane.ERROR_MESSAGE);
                    closeSocket();
                }
                JOptionPane.showMessageDialog(textArea, "Váratlan hiba!!", "Váratlan hiba!!\n\"" + ex + "\"", JOptionPane.ERROR_MESSAGE);
                closeSocket();

            }
        }
    }

    @Override
    public void sendMessage(String message) throws IOException {
        try {
            if (message.contains(":")) {
                textArea.append(message + "\n");
                System.out.println("sending message: " + message);
            }

            MessageSender ms = new MessageSender(message, clientPW);
            ms.start();
            System.out.println("Waiting");
            ms.join();
            System.out.println("return");
        } catch (InterruptedException ex) {

            System.out.println("interrupted: " + ex.getMessage());
        }

    }

    public void closeSocket() {
        try {
            if (!isClosing()) {
                
                clientPW.append("bye");
                clientPW.flush();
                setClosing(true);
                System.out.println("Closing client Requested by the client)");
            }else System.out.println("Closing client: Requested by the server)");

            socket.close();
            clientBW.close();
            clientPW.close();
            System.out.println("everything closed");
        } catch (IOException ex1) {
            System.out.println("cannot close");
        } finally {
            System.out.println("exiting...");
            System.exit(0);
        }
    }
}
