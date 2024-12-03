package scripts;

import sockets.ClientSideSocket;
import view.MessagingView;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class ClientScript {

    public static void main(String[] args) {

        ClientSideSocket clientSideSocket;

        MessagingView.setLookAndFeel(new NimbusLookAndFeel());
        String name = MessagingView.askUserName("Enter name: ", "Register");

        try {
            clientSideSocket = new ClientSideSocket("localhost", 8000, name);

            MessagingView.createMainFrame(name);
            MessagingView.addListenerToButton(MessagingView.getButton(), clientSideSocket::sendMessageToServer);

            Thread messageReceivingThread = new Thread(() -> {
                while (true) {
                    // read from server while we do not type "exit"
                    String[] response = clientSideSocket.getServerMessage().split(": ");
                    String sender = response[0];
                    String responseBody = "";
                    if (response.length > 1) {
                        responseBody = response[1];
                    }

                    // add server response to messageBox (GUI)
                    MessagingView.addStyledMessage(responseBody, sender);
                }
            });
            messageReceivingThread.start();

        } catch (Exception e) {
            System.err.println("An error occurred for " + name + "'s socket : " + e.getMessage());
        }
    }

}