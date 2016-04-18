/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import chat.Chat;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Joco
 */
public class Server extends Chat {

    private Map<Integer, ClientOnServer> users;

    private JTextArea textArea;
    private JList<String> userList;
    private ServerSocket serverSocket;
    private int sentBy;
//   public ServerHelper sH;
//   private  Sender sender;
//    String userName;

    public Server(String userName, JTextArea textArea, JList<String> userList) throws IOException {
        super(userName + " - (host)");
        this.textArea = textArea;
        this.userList = userList;
        setDaemon(true);
        textArea.append("Szerver üzenet: Welcome\n");
        createServerSocket();
//        sH = new ServerHelper(textArea);
    }

    @Override
    public void run() {

        try {

            users = new Hashtable();
            int i = 1;
            while (true) {
                getUsers();
                System.out.println(i + ". Kliens-re vár...");
                Socket cs = serverSocket.accept();
                System.out.println(i + ". Kliens sikeresen csatlakozott...");
                ClientOnServer client = new ClientOnServer(this, i, cs);
                users.put(i++, client);

                client.start();
                while (client.getUserName() == null);
                sendMessage(getUsers());
                textArea.append("Szerver üzenet: "+client.getUserName() + " csatlakozott...");
                sendMessage("Szerver üzenet: "+client.getUserName() + " csatlakozott...");
            }
        } catch (IOException ex) {
            if (!isClosing()) {
                JOptionPane.showMessageDialog(textArea, "Váratlan hiba!!", "Váratlan hiba!!\n\"" + ex + "\"", JOptionPane.ERROR_MESSAGE);
                closeSocket();
            }

        }
    }

    private void createServerSocket() throws IOException {
        serverSocket = new ServerSocket(8888);
    }

    public void removeUser(int id) throws IOException {
        System.out.println("removing user");
        users.remove(id);
        sendMessage(getUsers());
    }

    private String getUsers() {
        StringBuilder usersList = new StringBuilder(getUserName());

        for (int id : users.keySet()) {
            ClientOnServer user = users.get(id);
            if (user != null) {
                System.out.println(user.getUserName());
                usersList.append(",").append(user.getUserName());
            } else {
                users.remove(id);
            }
        }
        System.out.println("Users: " + usersList.toString());
        userList.setListData(usersList.toString().split(","));
        return usersList.toString();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        if (message.contains(":")) {
            textArea.append(message + "\n");
        }
        System.out.println("sending message: " + message);
        for (Integer id : users.keySet()) {
            if (sentBy != id) {
                users.get(id).sendMessage(message);
            }
        }
    }

    @Override
    public void closeSocket() {
        try {
            setClosing(true);
            if (users.size() > 0) {

                for (int id : users.keySet()) {
                    Chat user = users.get(id);
                    users.remove(id);
                    user.closeSocket();
                    
                    user.join();
                }

            }
            serverSocket.close();
            System.out.println("Server socket closed");
        } catch (IOException ex) {
            System.out.println("cannot close the server socket...." + ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected int getSentBy() {
        return sentBy;
    }

    public void setSentBy(int sentBy) {
        this.sentBy = sentBy;
    }

}
