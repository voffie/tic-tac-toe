package se.iths.tictactoe.model;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.iths.tictactoe.network.GameServer;
import se.iths.tictactoe.network.State;

import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static se.iths.tictactoe.network.State.*;

class ModelTest {
    Model modelX;
    Model modelO;

    @BeforeAll
    static void setup() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void init() {
        GameServer server = new GameServer(0);
        Thread.ofVirtual().start(() -> GameServer.main(null));
        await().until(() -> server.getPort() != 0);

        modelX = new Model(server.getPort());
        modelO = new Model(server.getPort());
        modelX.connect();
        modelO.connect();
    }

    @Test
    void boardShouldBeEmptyOnStart() {
        List<String[]> testBoard = new ArrayList<>();
        String[] row1 = new String[]{"", "", ""};
        String[] row2 = new String[]{"", "", ""};
        String[] row3 = new String[]{"", "", ""};
        testBoard.add(row1);
        testBoard.add(row2);
        testBoard.add(row3);

        var modelXBoard = modelX.getBoard();

        assertAll(() -> assertArrayEquals(testBoard.getFirst(), modelXBoard.getFirst()),
                () -> assertArrayEquals(testBoard.get(1), modelXBoard.get(1)),
                () -> assertArrayEquals(testBoard.getLast(), modelXBoard.getLast()));
    }

    @Test
    void currentPlayerShouldBeXOnStart() {
        await().until(() -> modelX.handled && modelO.handled);
        assertEquals("X", modelO.getCurrentPlayer());
    }

    @Test
    void stateShouldBePlayingOnStart() {
        assertEquals(State.PLAYING, modelX.getState());
    }

    @Test
    void canPlaceTokenOnBoard() {
        List<String[]> testBoard = new ArrayList<>();
        String[] row1 = new String[]{"X", "", ""};
        String[] row2 = new String[]{"", "", ""};
        String[] row3 = new String[]{"", "", ""};
        testBoard.add(row1);
        testBoard.add(row2);
        testBoard.add(row3);

        sendAndAwaitMoveX(0, 0);

        var modelXBoard = modelX.getBoard();
        var modelOBoard = modelO.getBoard();

        assertAll(
                () -> assertArrayEquals(testBoard.getFirst(), modelXBoard.getFirst()),
                () -> assertArrayEquals(testBoard.get(1), modelXBoard.get(1)),
                () -> assertArrayEquals(testBoard.getLast(), modelXBoard.getLast()),
                () -> assertArrayEquals(testBoard.getFirst(), modelOBoard.getFirst()),
                () -> assertArrayEquals(testBoard.get(1), modelOBoard.get(1)),
                () -> assertArrayEquals(testBoard.getLast(), modelOBoard.getLast()));
    }

    @Test
    void canNotPlaceTokenOnTakenCell() {
        List<String[]> testBoard = new ArrayList<>();
        String[] row1 = new String[]{"X", "", ""};
        String[] row2 = new String[]{"", "", ""};
        String[] row3 = new String[]{"", "", ""};
        testBoard.add(row1);
        testBoard.add(row2);
        testBoard.add(row3);

        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(0, 0);

        assertAll(
                () -> assertArrayEquals(testBoard.getFirst(), modelX.getBoard().getFirst()),
                () -> assertArrayEquals(testBoard.get(1), modelX.getBoard().get(1)),
                () -> assertArrayEquals(testBoard.getLast(), modelX.getBoard().getLast()),
                () -> assertArrayEquals(testBoard.getFirst(), modelO.getBoard().getFirst()),
                () -> assertArrayEquals(testBoard.get(1), modelO.getBoard().get(1)),
                () -> assertArrayEquals(testBoard.getLast(), modelO.getBoard().getLast()));
    }

    @Test
    void canWinHorizontallyOnFirstRow() {
        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(1, 0);
        sendAndAwaitMoveX(0, 1);
        sendAndAwaitMoveO(1, 1);
        sendAndAwaitMoveX(0, 2);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinHorizontallyOnSecondRow() {
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(0, 0);
        sendAndAwaitMoveX(1, 1);
        sendAndAwaitMoveO(0, 1);
        sendAndAwaitMoveX(1, 2);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinHorizontallyOnThirdRow() {
        sendAndAwaitMoveX(2, 0);
        sendAndAwaitMoveO(0, 0);
        sendAndAwaitMoveX(2, 1);
        sendAndAwaitMoveO(0, 1);
        sendAndAwaitMoveX(2, 2);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnFirstColumn() {
        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(0, 1);
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(1, 1);
        sendAndAwaitMoveX(2, 0);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnSecondColumn() {
        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(0, 1);
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(1, 1);
        sendAndAwaitMoveX(0, 2);
        sendAndAwaitMoveO(2, 1);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnThirdColumn() {
        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(0, 2);
        sendAndAwaitMoveX(0, 1);
        sendAndAwaitMoveO(1, 2);
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(2, 2);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinDiagonallyLeftToRight() {
        sendAndAwaitMoveX(0, 1);
        sendAndAwaitMoveO(0, 0);
        sendAndAwaitMoveX(0, 2);
        sendAndAwaitMoveO(1, 1);
        sendAndAwaitMoveX(1, 2);
        sendAndAwaitMoveO(2, 2);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinDiagonallyRightToLeft() {
        sendAndAwaitMoveX(0, 0);
        sendAndAwaitMoveO(0, 2);
        sendAndAwaitMoveX(0, 1);
        sendAndAwaitMoveO(1, 1);
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(2, 0);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void gameIsDrawnOnFullBoard() {
        sendAndAwaitMoveX(0, 2);
        sendAndAwaitMoveO(0, 0);
        sendAndAwaitMoveX(1, 0);
        sendAndAwaitMoveO(0, 1);
        sendAndAwaitMoveX(1, 1);
        sendAndAwaitMoveO(1, 2);
        sendAndAwaitMoveX(2, 1);
        sendAndAwaitMoveO(2, 0);
        sendAndAwaitMoveX(2, 2);

        assertAll(
                () -> assertEquals(GAME_OVER_DRAW, modelX.getState()),
                () -> assertEquals(GAME_OVER_DRAW, modelO.getState())
        );
    }

    private void sendAndAwaitMoveX(int row, int col) {
        modelX.sendMove(row, col);
        await().until(() -> modelX.handled && modelO.handled);
    }

    private void sendAndAwaitMoveO(int row, int col) {
        modelO.sendMove(row, col);
        await().until(() -> modelX.handled && modelO.handled);
    }

}
