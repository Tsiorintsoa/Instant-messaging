package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSideSocket {
    private String name;
    private final Socket clientSocket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientSideSocket(String domain, int port, String name) throws IOException {
        this.clientSocket = new Socket(domain, port);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        initUsername(name);
        System.out.println("Client socket initialized at port " + port + ", name: " + name);
    }

    public ClientSideSocket(String name) throws IOException {
        this.clientSocket = new Socket("localhost", 8000);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.name = name;
        initUsername(name);
        System.out.println("Client socket initialized at port " + 8000 + ", name: " + name);
    }

    public void initUsername(String username) {
        sendMessageToServer("username:" + username);
    }

    public void sendMessageToServer(String message) {
        this.out.println(message);
    }

    public String getServerMessage() {
        String receivedMessage = null;
        try {
            receivedMessage = this.in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading inputStream (client side): " + e.getMessage());
        }
        return receivedMessage;
    }

    public void closeConnection() {
        try {
            if (this.clientSocket != null) {
                this.clientSocket.close();
            }
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            System.err.println("Client closed connection");
        } catch (IOException e) {
            System.err.println("Error closing client socket" + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
