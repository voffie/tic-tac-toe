package se.iths.tictactoe.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
import java.util.Random;

import static se.iths.tictactoe.model.State.*;

public class Model {
    private String[][] board = {{"", "", ""}, {"", "", ""}, {"", "", ""}};
    private final Random random = new Random();
    private State state = PLAYING;
    private String currentPlayer = "X";
    private int p1Score = 0;
    private int p2Score = 0;
    private boolean isLocal = true;
    private final StringProperty status = new SimpleStringProperty(currentPlayer + "'s turn");
    private final StringProperty p1Points = new SimpleStringProperty("Player 1 (X): 0");
    private final StringProperty p2Points = new SimpleStringProperty("CPU (O): 0");

    public boolean getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;

        p2Points.set(isLocal ? "CPU (O): 0" : "Player 2 (O): 0");
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

    public String getP1Points() {
        return p1Points.get();
    }

    public StringProperty p1PointsProperty() {
        return p1Points;
    }

    public void incrementP1Points() {
        p1Score++;
        this.p1Points.set("Player 1 (X): " + p1Score);
    }

    public String getP2Points() {
        return p2Points.get();
    }

    public StringProperty p2PointsProperty() {
        return p2Points;
    }

    public void incrementP2Points() {
        p2Score++;
        this.p2Points.set("Player 2 (O): " + p2Score);
    }

    public String[][] getBoard() {
        return board;
    }

    public void resetBoard() {
        board = new String[][]{{"", "", ""}, {"", "", ""}, {"", "", ""}};
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String newPlayer) {
        currentPlayer = newPlayer;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state == PLAYING) {
            resetBoard();
            currentPlayer = "X";
            setStatus("X's turn");
        }
        this.state = state;
    }

    private boolean isCellFree(int row, int col) {
        return board[row][col].isEmpty();
    }

    public int[] getCpuToken() {
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!isCellFree(row, col));

        return new int[]{row, col};
    }

    public void setToken(int row, int col) {
        if (isCellFree(row, col)) {
            board[row][col] = currentPlayer;
            checkGameStatus();
        }
    }

    private void checkGameStatus() {
        if (isWinner()) {
            setStatus(currentPlayer + " won! The game is over!");
            if (Objects.equals(currentPlayer, "X")) {
                incrementP1Points();
            } else {
                incrementP2Points();
            }
            state = GAME_OVER;
        } else if (isFull()) {
            setStatus("Draw! The game is over");
            state = GAME_OVER;
        }
    }

    public boolean isFull() {
        for (String[] row : board) {
            for (String cell : row) {
                if (cell.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isWinner() {
        for (int i = 0; i < 3; i++) {
            if (checkRow(i) || checkColumn(i)) {
                return true;
            }

        }
        return checkDiagonals();
    }

    private boolean checkRow(int row) {
        return board[row][0].equals(currentPlayer) && board[row][1].equals(currentPlayer) && board[row][2].equals(currentPlayer);
    }

    private boolean checkColumn(int col) {
        return board[0][col].equals(currentPlayer) && board[1][col].equals(currentPlayer) && board[2][col].equals(currentPlayer);
    }

    private boolean checkDiagonals() {
        return (board[0][0].equals(currentPlayer) && board[1][1].equals(currentPlayer) && board[2][2].equals(currentPlayer)) ||
                (board[0][2].equals(currentPlayer) && board[1][1].equals(currentPlayer) && board[2][0].equals(currentPlayer));
    }
}
