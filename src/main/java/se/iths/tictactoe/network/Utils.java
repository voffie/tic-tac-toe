package se.iths.tictactoe.network;

public class Utils {

    private Utils() {}

    public static String[][] deserializeMessage(String message) {
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
}
