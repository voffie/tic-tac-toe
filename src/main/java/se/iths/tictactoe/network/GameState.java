package se.iths.tictactoe.network;

import java.util.Objects;
import static se.iths.tictactoe.network.State.*;

public class GameState {
    private final String[][] board = {{"", "", ""}, {"", "", ""}, {"", "", ""}};
    private String currentPlayer = "X";
    private int p1Points = 0;
    private int p2Points = 0;
    private State state = PLAYING;

    public String[][] getBoard() {
        return board;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public State getState() {
        return state;
    }

    public int getP1Points() {
        return p1Points;
    }

    public void incrementP1Points() {
        p1Points++;
    }

    public void getP2Points() {
        p2Points++;
    }

    public void incrementP2Points() {
        p2Points++;
    }

    public void updateBoard(int row, int col) {
        board[row][col] = currentPlayer;
        checkGameStatus();
    }

    public boolean isCellFree(int row, int col) {
        return board[row][col].isEmpty();
    }

    public void checkGameStatus() {
        if (isWinner()) {
            System.out.println(getCurrentPlayer() + " won! The game is over!");
            if (Objects.equals(getCurrentPlayer(), "X")) {
                incrementP1Points();
            } else {
                incrementP2Points();
            }
            state = GAME_OVER;
        } else if (isFull()) {
            System.out.println("Draw! The game is over");
            state = GAME_OVER_DRAW;
        } else {
            setCurrentPlayer((Objects.equals(getCurrentPlayer(), "X")) ? "O" : "X");
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
