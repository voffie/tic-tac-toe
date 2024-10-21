package se.iths.tictactoe.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Objects;
import static se.iths.tictactoe.model.State.*;

public class Model {
    private final String[][] board = {{"X", "O", "X"}, {"", "", ""}, {"", "", ""}};
    private State state = PLAYING;
    private String currentPlayer = "X";
    private int p1Score = 0;
    private int p2Score = 0;
    private final StringProperty status = new SimpleStringProperty(currentPlayer + "'s turn");
    private final StringProperty p1Points = new SimpleStringProperty("Player 1 (X): 0");
    private final StringProperty p2Points = new SimpleStringProperty("Player 2 (O): 0");

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getP1Points() {
        return p1Points.get();
    }

    public StringProperty p1PointsProperty() {
        return p1Points;
    }

    public void setP1Points() {
        p1Score = p1Score + 1;
        this.p1Points.set("Player 1 (X): " + p1Score);
    }

    public String getP2Points() {
        return p2Points.get();
    }

    public StringProperty p2PointsProperty() {
        return p2Points;
    }

    public void setP2Points() {
        p2Score = p2Score + 1;
        this.p2Points.set("Player 2 (O): " + p2Score);
    }

    public String[][] getBoard() {
        return board;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public State getState() {
        return state;
    }

    public void setToken(int row, int col) {
        if (!board[row][col].isEmpty()) {
            currentPlayer = Objects.equals(currentPlayer, "X") ? "O" : "X";
            return;
        }

        board[row][col] = currentPlayer;

        if (isWinner()) {
            setStatus(currentPlayer + " won! The game is over!");
            if (Objects.equals(currentPlayer, "X")) {
                setP1Points();
            } else {
                setP2Points();
            }
            state = GAME_OVER;
        } else if (isFull()) {
            setStatus("Draw! The game is over");
            state = GAME_OVER;
        } else {
            currentPlayer = Objects.equals(currentPlayer, "X") ? "O" : "X";
            setStatus(currentPlayer + "'s turn");
        }
    }

    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWinner() {
        String player = currentPlayer;
        for (int i = 0; i < 3; i++) {
            if ((board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) ||
                    (board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player))) {
                return true;
            }
        }

        return (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) ||
                (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player));
    }
}
