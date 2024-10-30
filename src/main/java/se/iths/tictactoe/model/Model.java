package se.iths.tictactoe.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.iths.tictactoe.network.GameClient;
import se.iths.tictactoe.network.State;

import java.util.Objects;

import static se.iths.tictactoe.network.State.*;
import static se.iths.tictactoe.network.Utils.deserializeMessage;

public class Model {
    private final GameClient client;
    ObservableList<String[]> board = FXCollections.observableArrayList(new String[]{"", "", ""}, new String[]{"", "", ""}, new String[]{"", "", ""});
    private String token;
    private String opponentToken;
    private String currentPlayer = "X";
    private int playerScore = 0;
    private int opponentScore = 0;
    private final StringProperty status = new SimpleStringProperty(currentPlayer + "'s turn");
    private final StringProperty playerScoreLabel = new SimpleStringProperty("You (X): 0");
    private final StringProperty opponentScoreLabel = new SimpleStringProperty("Opponent (O): 0");
    private State state = PLAYING;
    public boolean handled = false;

    public Model() {
        client = new GameClient("localhost", 8080);
    }

    public Model(int port) {
        client = new GameClient("localhost", port);
    }

    public void connect() {
        client.connect(this::receiveMessage);
    }

    private boolean isCellFree(int row, int col) {
        return board.get(row)[col].isEmpty();
    }

    public void sendMove(int row, int col) {
        if (isCellFree(row, col)) {
            client.sendMove(row, col);
        }
    }

    private void handleTokenMessage(String message) {
        token = message.split(":")[1];
        opponentToken = Objects.equals(token, "X") ? "O" : "X";
        try {
            Platform.runLater(() -> {
                this.playerScoreLabel.set("You (" + token + "): 0");
                this.opponentScoreLabel.set("Opponent (" + opponentToken + "): 0");
            });
        } catch (Exception e) {
            System.out.println("Error in Platform.runLater " + e.getMessage());
        }
    }

    private void updateBoard(String serializedBoard) {
        String[][] deserializedMessage = deserializeMessage(serializedBoard);
        for (int row = 0; row < 3; row++) {
            String[] newRow = new String[3];
            for (int col = 0; col < 3; col++) {
                newRow[col] = deserializedMessage[row][col].equals("-") ? "" : deserializedMessage[row][col];
            }
            board.set(row, newRow);
        }
    }

    private void handleState() {
        String statusMessage;
        if (state == GAME_OVER) {
            String winner = isPlayerTurn() ? "You" : "Opponent";
            statusMessage = winner + " won! The game is over!";

            if (isPlayerTurn()) {
                try {
                    Platform.runLater(this::incrementPlayerScore);
                } catch (Exception e) {
                    System.out.println("Error in Platform.runLater " + e.getMessage());
                }
            } else {
                try {
                    Platform.runLater(this::incrementOpponentScore);
                } catch (Exception e) {
                    System.out.println("Error in Platform.runLater " + e.getMessage());
                }
            }

        } else if (state == GAME_OVER_DRAW) {
            statusMessage = "Draw! The game is over!";
        } else {
            statusMessage = isPlayerTurn() ? "Your turn" : "Opponent's turn";
        }

        try {
            Platform.runLater(() -> setStatus(statusMessage));
        } catch (Exception e) {
            System.out.println("Error in Platform.runLater " + e.getMessage());
        }
    }

    public void receiveMessage(String message) {
        handled = false;
        if (message.contains("Token:")) {
            handleTokenMessage(message);
        } else {
            String[] boardStateSplit = message.split(",CurrentPlayer:");
            String[] playerStateSplit = boardStateSplit[1].split(",State:");
            currentPlayer = playerStateSplit[0];
            state = State.valueOf(playerStateSplit[1]);

            updateBoard(boardStateSplit[0]);
            handleState();
        }
        handled = true;
    }

    public ObservableList<String[]> getBoard() {
        return board;
    }

    public State getState() {
        return state;
    }

    public String getToken() {
        return token;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public boolean isPlayerTurn() {
        return Objects.equals(currentPlayer, token);
    }

    public String getPlayerScoreLabel() {
        return playerScoreLabel.get();
    }

    public StringProperty playerScoreLabelProperty() {
        return playerScoreLabel;
    }

    public void incrementPlayerScore() {
        playerScore++;
        this.playerScoreLabel.set("You (X): " + playerScore);
    }

    public String getOpponentScoreLabel() {
        return opponentScoreLabel.get();
    }

    public StringProperty opponentScoreLabelProperty() {
        return opponentScoreLabel;
    }

    public void incrementOpponentScore() {
        opponentScore++;
        this.opponentScoreLabel.set("Opponent (" + opponentToken + "): " + opponentScore);
    }
}
