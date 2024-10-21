package se.iths.tictactoe.controller;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import se.iths.tictactoe.model.Model;
import se.iths.tictactoe.model.State;
import static se.iths.tictactoe.model.State.*;

public class Controller {
    private final Model model = new Model();
    public GridPane pane;
    public Label status;
    public Label player1Points;
    public Label player2Points;

    public Model getModel() {
        return model;
    }

    public void initialize() {
        initializeBoard();
    }

    private void handleClick(MouseEvent mouseEvent) {
        Label cell = (Label) mouseEvent.getSource();
        int[] position = (int[]) cell.getUserData();
        int row = position[0];
        int col = position[1];
        State state = model.getState();
        if (state != GAME_OVER) {
            updateCell(cell, row, col);
        } else {
            initializeBoard();
            model.setState(PLAYING);
        }
    }

    private void updateCell(Label cell, int row, int col) {
        String token = model.getCurrentPlayer();
        model.setToken(row, col);
        cell.setText(token);
    }

    private void initializeBoard() {
        String[][] board = model.getBoard();
        pane.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label cell = new Label();
                cell.setMinSize(100, 100);
                cell.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-font-size: 36");
                cell.setOnMouseClicked(this::handleClick);
                cell.setUserData(new int[]{i, j});
                board[j][i] = "";
                pane.add(cell, j, i);
            }
        }
    }
}
