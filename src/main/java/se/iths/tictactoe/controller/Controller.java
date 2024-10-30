package se.iths.tictactoe.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import se.iths.tictactoe.model.Model;
import se.iths.tictactoe.network.ComputerClient;
import java.util.Objects;

public class Controller {
    private final Model model = new Model();
    public GridPane pane;
    public HBox container;
    public HBox top;
    public Label bottom;

    public void initialize() {
        initializeBoard();
        model.getBoard().addListener((ListChangeListener<String[]>) _ -> {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    String[] currentRow = model.getBoard().get(row);
                    String text = currentRow[col];
                    Label label = getLabel(row, col);
                    Platform.runLater(() -> {
                        if (label != null) {
                            label.setText(Objects.equals(text, "-") ? "" : text);
                        }
                    });
                }
            }
        });
    }

    private void initializeBoard() {
        pane.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                createCell(i, j);
            }
        }
    }

    private void createCell(int row, int col) {
        Label cell = new Label();
        cell.setMinSize(100, 100);
        cell.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-font-size: 36");
        cell.setOnMouseClicked(this::handleClick);
        cell.setUserData(new int[]{row, col});
        pane.add(cell, col, row);
    }

    public Model getModel() {
        return model;
    }

    private void handleClick(MouseEvent mouseEvent) {
        Label cell = (Label) mouseEvent.getSource();
        int[] position = (int[]) cell.getUserData();
        int row = position[0];
        int col = position[1];
        model.sendMove(row, col);
    }

    private Label getLabel(int row, int col) {
        for (Node label : pane.getChildren()) {
            int[] userData = (int[]) label.getUserData();
            if (userData[0] == row && userData[1] == col) {
                return (Label) label;
            }
        }

        return null;
    }

    public void localClicked() {
        renderGameUI();
        model.connect();
        ComputerClient cpu = new ComputerClient();
        cpu.connect();
    }

    public void onlineClicked() {
        renderGameUI();
        model.connect();
    }

    public void renderGameUI() {
        container.setVisible(false);
        container.setManaged(false);

        pane.setVisible(true);
        pane.setManaged(true);

        top.setManaged(true);
        top.setVisible(true);

        bottom.setManaged(true);
        bottom.setVisible(true);
    }
}
