package com.kapitolgamers;

import com.kapitolgamers.classes.items.ItemManager;
import com.kapitolgamers.classes.structures.MapManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class GameServer {
    private ServerSocket serverSocket;
    private Vector<Socket> otherPlayerSockets;
    private MapManager map;
    private ItemManager items;

    public GameServer() {
        otherPlayerSockets = new Vector<>();
        map = new MapManager("sprites/rooms");
        items = new ItemManager();

        generateNewMapAndItems();

        try {
            serverSocket = new ServerSocket(12345); // Listen on port 12345
            System.out.println("Server listening on port 12345...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNewMapAndItems() {
        map.generateMap(12);
        items.generateItems(10, map);
        // map.printMapToConsole();
    }

    public void startAcceptingNewPlayerConnections() {
        Thread connectionThread = new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New player connected: " + clientSocket);
                    otherPlayerSockets.add(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        connectionThread.start();
    }


    public void startProcessingExistingPlayerRequests() {
        Thread connectionThread = new Thread(() -> {
            while (true) {
                for (Socket socket : otherPlayerSockets) {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        if (inputStream == null) {
                            System.out.println("Input stream is null for socket: " + socket);
                            continue; // Skip this socket and proceed to the next one
                        }
                        while (inputStream.available() > 0) {
                            ObjectInputStream in = new ObjectInputStream(inputStream);
                            String message = (String) in.readObject(); // Read String message
                            System.out.println("Received " + message + " from " + socket);

                            if ("REQUEST MAPROOMS".equals(message)) {
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject(map.getMapRooms());
                                out.flush();
                                System.out.println("Sent MAPROOMS to " + socket);
                            } else if ("REQUEST ITEMS".equals(message)) {
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject(items.getItemsData());
                                out.flush();
                                System.out.println("Sent ITEMS to " + socket);
                            } else {
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject(null);
                                out.flush();
                                System.out.println("Sent null to " + socket);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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
