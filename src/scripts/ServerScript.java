package scripts;

import sockets.ServerSideSocket;

public class ServerScript {

    public static void main(String[] args) {
        try {
            ServerSideSocket serverSideSocket = new ServerSideSocket(8000);
            serverSideSocket.handleConnections();
        } catch (Exception e) {
            System.err.println("An error occurred while running server : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
