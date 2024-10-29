package se.iths.tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;

import static se.iths.tictactoe.network.State.*;
import static se.iths.tictactoe.network.Utils.deserializeMessage;

public final class ComputerClient {
    private final String[][] board = {{"", "", ""}, {"", "", ""}, {"", "", ""}};
    private String token = "O";
    private State state = PLAYING;
    Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Random random = new Random();

    public void connect() {
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread.ofVirtual().start(this::listenForMessages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getCpuToken() {
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!isCellFree(row, col));

        return new int[]{row, col};
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCellFree(int row, int col) {
        return board[row][col].isEmpty();
    }

    public void sendMove() {
        if (out != null && state != GAME_OVER || state != GAME_OVER_DRAW) {
            System.out.println(state);
            System.out.println(state == GAME_OVER || state == GAME_OVER_DRAW);
            int[] cpuPos = getCpuToken();
            out.println(cpuPos[0] + "," + cpuPos[1]);
            board[cpuPos[0]][cpuPos[1]] = token;
        }
    }

    private void handleMessage(String message) {
        if (Objects.equals(message, "Invalid move")) {
            return;
        }

        if (message.contains("Token:")) {
            token = message.split(":")[1];
            return;
        }

        if (state == GAME_OVER || state == GAME_OVER_DRAW) {
            System.out.println("This runs!");
            return;
        }

        String[] boardStateSplit = message.split(",CurrentPlayer:");
        String[] playerStateSplit = boardStateSplit[1].split(",State:");
        state = State.valueOf(playerStateSplit[1]);

        String[][] deserializedMessage = deserializeMessage(boardStateSplit[0]);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = deserializedMessage[row][col].equals("-") ? "" : deserializedMessage[row][col];
            }
        }

        if (!Objects.equals(token, playerStateSplit[0]))
            return;

        sendMove();
    }
}
