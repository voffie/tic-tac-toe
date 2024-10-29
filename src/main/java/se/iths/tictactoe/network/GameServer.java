package se.iths.tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static se.iths.tictactoe.network.State.*;

public class GameServer {
    private static final int PORT = 8080;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final GameState gameState = new GameState();
    private static int players = 0;
    private static final String[] tokens = {"X", "O"};

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game server started on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                System.out.println("With token: " + tokens[players]);
                ClientHandler clientHandler = new ClientHandler(clientSocket, tokens[players]);
                clients.add(clientHandler);
                Thread.ofVirtual().start(clientHandler);
                players++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void broadcastGameState() {
        synchronized (clients) {
            String state = serializeGameState();
            for (ClientHandler client : clients) {
                client.sendMessage(state);
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

                    if (gameState.isCellFree(row, col)) {
                        gameState.updateBoard(row, col);
                        broadcastGameState();
                    } else {
                        sendMessage("Invalid move");
                    }
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
