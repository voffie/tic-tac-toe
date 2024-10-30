package se.iths.tictactoe.model;

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

    @BeforeEach
    public void init() {
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

        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);

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

        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);

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
        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinHorizontallyOnSecondRow() {
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 2);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinHorizontallyOnThirdRow() {
        modelX.sendMove(2, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(2, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(2, 2);
        await().until(() -> modelX.handled && modelO.handled);


        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnFirstColumn() {
        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(2, 0);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnSecondColumn() {
        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(2, 1);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinVerticallyOnThirdColumn() {
        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(2, 2);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinDiagonallyLeftToRight() {
        modelX.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(2, 2);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void canWinDiagonallyRightToLeft() {
        modelX.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(2, 0);
        await().until(() -> modelX.handled && modelO.handled);

        assertAll(
                () -> assertEquals(GAME_OVER, modelX.getState()),
                () -> assertEquals(GAME_OVER, modelO.getState())
        );
    }

    @Test
    void gameIsDrawnOnFullBoard() {
        modelX.sendMove(0, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(0, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(1, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(1, 2);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(2, 1);
        await().until(() -> modelX.handled && modelO.handled);
        modelO.sendMove(2, 0);
        await().until(() -> modelX.handled && modelO.handled);
        modelX.sendMove(2, 2);
        await().until(() -> modelO.handled && modelX.handled);

        assertAll(
                () -> assertEquals(GAME_OVER_DRAW, modelX.getState()),
                () -> assertEquals(GAME_OVER_DRAW, modelO.getState())
        );
    }

}
