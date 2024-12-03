package sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.HashMap;

public class ServerSideSocket {
    private final ServerSocket serverSocket;
    private final HashMap<PrintWriter, String> clientsOutputStream = new HashMap<>();

    public ServerSideSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        System.out.println("Server socket launched at port " + port);
        System.out.println("Waiting for client connection...");
    }

    public void handleConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client accepted : " + clientSocket.getInetAddress());
                // starting new client thread
                ClientHandler client = new ClientHandler(clientSocket);
                client.start();

                // add new client to our hashmap in a synchronized way
                synchronized (this.clientsOutputStream) {
                    this.clientsOutputStream.put(new PrintWriter(clientSocket.getOutputStream(), true), client.name);
                }
            } catch (IOException e) {
                System.err.println("Error while waiting connection : " + e.getMessage());
            }
        }
    }

    // thread that is going to be started for / by each client on connection
    private class ClientHandler extends Thread {
        private final String name;
        private final PrintWriter out;
        private final BufferedReader in;

        private ClientHandler(Socket clientSocket) throws IOException {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.name = this.in.readLine().split("username:")[1];
            broadCastClients(name + " joined");
        }

        @Override
        public void run() {
            respondClient();
        }

        private String getClientMessage() {
            try {
                if (this.in != null) {
                    return this.in.readLine();
                }
            } catch (IOException e) {
                System.err.println("Error reading inputStream (server side) : " + e.getMessage());
            }
            return null;
        }

        private void respondClient() {
            String clientMessage;
            // clientMessage will be "null" if outputStream is closed
            while ((clientMessage = getClientMessage()) != null && !clientMessage.trim().isEmpty()) {
                System.err.println("Responding client...");
                broadCastClients(name + ": " + clientMessage);
            }
            broadCastClients(name + " left");
            closeClientConnection();
        }

        private void broadCastClients(String message) {
            // sending broadcast message in a synchronized way
            synchronized (clientsOutputStream) {
                for (PrintWriter writer : clientsOutputStream.keySet()) {
                    writer.println(message);
                }
            }
        }

        private void closeClientConnection() {
            try {
                synchronized (clientsOutputStream) {
                    clientsOutputStream.remove(this.out);
                }
                if (this.in != null) {
                    this.in.close();
                }
                if (this.out != null) {
                    this.out.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client resources: " + e.getMessage());
            }
        }
    }
}