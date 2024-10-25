package se.iths.tictactoe.model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.iths.tictactoe.network.GameClient;
import se.iths.tictactoe.network.State;

import static se.iths.tictactoe.network.State.*;

import java.util.Objects;

public class Model {
    private final GameClient client = new GameClient("localhost", 8080);
    ObservableList<String[]> board = FXCollections.observableArrayList();
    private String token;
    private String currentPlayer = "X";
    private int playerScore = 0;
    private int opponentScore = 0;
    private final StringProperty status = new SimpleStringProperty(currentPlayer + "'s turn");
    private final StringProperty playerScoreLabel = new SimpleStringProperty("You (X): 0");
    private final StringProperty opponentScoreLabel = new SimpleStringProperty("Opponent (O): 0");
    private State state = PLAYING;

    public Model() {
        initializeBoard();
    }

    public void connect() {
        client.connect(this::receiveMessage);
    }

    public void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            final String[] row = new String[3];
            board.add(row);
            for (int j = 0; j < 3; j++) {
                row[j] = "";
            }
        }
    }

    public void sendMove(int row, int col) {
        client.sendMove(row, col);
        board.get(row)[col] = token;
    }

    public void receiveMessage(String message) {
        if (Objects.equals(message, "Invalid move")) {
            return;
        }

        if (message.contains("Token:")) {
            token = message.split(":")[1];
            String opponentToken = Objects.equals(token, "X") ? "O" : "X";
            Platform.runLater(() -> {
                this.playerScoreLabel.set("You (" + token + "): 0");
                this.opponentScoreLabel.set("Opponent (" + opponentToken + "): 0");
            });
            return;
        }

        String[] boardStateSplit = message.split(",CurrentPlayer:");
        String[] playerStateSplit = boardStateSplit[1].split(",State:");
        currentPlayer = playerStateSplit[0];
        state = State.valueOf(playerStateSplit[1]);

        String[][] deserializedMessage = deserializeMessage(boardStateSplit[0]);
        for (int row = 0; row < 3; row++) {
            String[] newRow = new String[3];
            for (int col = 0; col < 3; col++) {
                newRow[col] = deserializedMessage[row][col].equals("-") ? "" : deserializedMessage[row][col];
            }
            board.set(row, newRow);
        }

        String statusMessage;
        if (state == GAME_OVER) {
            String winner = isPlayerTurn() ? "You" : "Opponent";
            statusMessage = winner + " won! The game is over!";

            if (isPlayerTurn()) {
                Platform.runLater(this::incrementPlayerScore);
            } else {
                Platform.runLater(this::incrementOpponentScore);
            }

        } else if (state == GAME_OVER_DRAW) {
            statusMessage = "Draw! The game is over!";
        } else {
            statusMessage = isPlayerTurn() ? "Your turn" : "Opponent's turn";
        }

        Platform.runLater(() -> setStatus(statusMessage));
    }

    private String[][] deserializeMessage(String message) {
        String[][] newBoard = new String[3][3];
        String[] cells = message.split(",");

        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newBoard[i][j] = cells[index].equals("-") ? "" : cells[index];
                index++;
            }
        }
        return newBoard;
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
        this.opponentScoreLabel.set("Opponent (O): " + opponentScore);
    }
}
