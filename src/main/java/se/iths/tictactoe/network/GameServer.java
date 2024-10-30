package se.iths.tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static se.iths.tictactoe.network.State.*;

public class GameServer {
    private static int PORT = 8080;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static GameState gameState;
    private static final String[] tokens = {"X", "O"};

    public GameServer(int port) {
        PORT = port;
    }

    public int getPort() {
        return PORT;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game server started on port: " + PORT);
            PORT = serverSocket.getLocalPort();
            gameState = new GameState();
            clients.clear();

            while (true) {
                int players = clients.size();
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client #" + players + " connected: " + clientSocket.getInetAddress());
                System.out.println("With token: " + tokens[players % tokens.length]);
                ClientHandler clientHandler = new ClientHandler(clientSocket, tokens[players % tokens.length]);
                clients.add(clientHandler);
                Thread.ofVirtual().start(clientHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    private static String serializeGameState() {
        StringBuilder currentState = new StringBuilder();
        for (String[] row : gameState.getBoard()) {
            for (String cell : row) {
                currentState.append(cell.isEmpty() ? "-" : cell).append(",");
            }
        }
        currentState.append("CurrentPlayer:").append(gameState.getCurrentPlayer());
        State state = gameState.getState();
        currentState.append(",State:").append(state);

        return currentState.toString();


    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private final String token;

        public ClientHandler(Socket socket, String token) {
            this.socket = socket;
            this.token = token;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage("Token:" + token);
                sendMessage(serializeGameState());

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    String[] positions = message.split(",");
                    int row = Integer.parseInt(positions[0]);
                    int col = Integer.parseInt(positions[1]);

                    String currentPlayer = gameState.getCurrentPlayer();
                    State state = gameState.getState();

                    if (!Objects.equals(currentPlayer, token) || state == GAME_OVER || state == GAME_OVER_DRAW) {
                        return;
                    }

                    gameState.updateBoard(row, col);
                    broadcastMessage(serializeGameState());
                }
                clients.remove(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
