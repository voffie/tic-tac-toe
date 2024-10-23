package se.iths.tictactoe.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import se.iths.tictactoe.TicTacToe;
import se.iths.tictactoe.model.Model;
import se.iths.tictactoe.model.State;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static se.iths.tictactoe.model.State.*;

public class Controller {
    private final Model model = new Model();
    public GridPane pane;

    public void initialize() throws URISyntaxException, IOException {
        initializeBoard();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(Objects.requireNonNull(TicTacToe.class.getResource("config.json")).toURI()));
        if (!root.has("online")) {
            throw new RuntimeException("Missing config option: online (true/false)");
        }

        boolean isOnline = root.get("online").asBoolean();

        if (!isOnline) {
            System.out.println("Playing locally!");
            return;
        }

        model.setIsLocal(false);
        int port = root.has("port") ? root.get("port").asInt() : 3000;

        if (root.has("host")) {
            connect(root.get("host").asText(), port);
        } else {
            host(port);
        }
    }

    private void connect(String host, int port) {
        System.out.println("Connecting to " + host + ":" + port);
        throw new RuntimeException("Not implemented yet");
    }

    private void host(int port) {
        System.out.println("Opening server on port: " + port);
        throw new RuntimeException("Not implemented yet");
    }

    public Model getModel() {
        return model;
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
        if (model.getIsLocal()) {
            updateLocalCell(cell, row, col);
        } else {
            updateOnlineCell(cell, row, col);
        }
    }

    private void updateLocalCell(Label cell, int row, int col) {
        String token = model.getCurrentPlayer();
        model.setToken(row, col);
        cell.setText(token);
        State currentState = model.getState();
        model.setCurrentPlayer("O");
        if (currentState == PLAYING) {
            int[] cpuPos = model.getCpuToken();
            int cpuRow = cpuPos[0];
            int cpuCol = cpuPos[1];
            model.setToken(cpuRow, cpuCol);
            Label label = getLabel(cpuRow, cpuCol);
            if (label != null)
                label.setText("O");
            model.setCurrentPlayer("X");
        }
    }

    private void updateOnlineCell(Label cell, int row, int col) {

    }

    private Label getLabel(int row, int col) {
        for (Node label : pane.getChildren()) {
            int[] userData = (int[]) label.getUserData();
            int userDataRow = userData[0];
            int userDataCol = userData[1];
            if (userDataRow == row && userDataCol == col) {
                return (Label) label;
            }
        }

        return null;
    }

    private void initializeBoard() {
        model.resetBoard();
        pane.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label cell = new Label();
                cell.setMinSize(100, 100);
                cell.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-font-size: 36");
                cell.setOnMouseClicked(this::handleClick);
                cell.setUserData(new int[]{i, j});
                pane.add(cell, j, i);
            }
        }
    }
}
