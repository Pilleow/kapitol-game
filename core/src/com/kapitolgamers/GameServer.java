package com.kapitolgamers;

import java.io.*;
import java.net.*;
import java.util.Vector;

public class GameServer {
    private ServerSocket serverSocket;
    private Vector<Socket> otherPlayerSockets;

    public GameServer() {
        otherPlayerSockets = new Vector<>();
        try {
            serverSocket = new ServerSocket(12345); // Listen on port 12345
            System.out.println("Server listening on port 12345...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptNewPlayerConnections() {
        Thread connectionThread = new Thread(() -> {
            while (true) {
                try {
                    // Accept new player connections
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New player connected: " + clientSocket);

                    // Add the player's socket to the list of otherPlayerSockets
                    otherPlayerSockets.add(clientSocket);

                    // Optionally, you can perform additional setup or processing here
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        connectionThread.start();
    }

    public void sendToAllClients(Object message) {
        for (Socket clientSocket : otherPlayerSockets) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object receiveFromClient(Socket clientSocket) {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        // Close the server socket when disposing
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
