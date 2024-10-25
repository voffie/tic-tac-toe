package se.iths.tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class GameClient {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> callback;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(Consumer<String> callback) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.callback = callback;

            Thread.ofVirtual().start(this::listenForMessages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                callback.accept(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMove(int row, int col) {
        if (out != null) {
            out.println(row + "," + col);
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
