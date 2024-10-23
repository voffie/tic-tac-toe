package se.iths.tictactoe.model;

import javafx.beans.property.SimpleStringProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    Model model = new Model();

    @Test
    void boardShouldBeEmptyOnStart() {
        String[][] board = {{"", "", ""}, {"", "", ""}, {"", "", ""}};
        assertArrayEquals(board, model.getBoard());
    }

    @Test
    void currentPlayerShouldBeXOnStart() {
        assertEquals("X", model.getCurrentPlayer());
    }

    @Test
    void stateShouldBePlayingOnStart() {
        assertEquals(State.PLAYING, model.getState());
    }

    @Test
    void shouldBeAbleToChangeState() {
        model.setState(State.GAME_OVER);
        assertEquals(State.GAME_OVER, model.getState());
    }

    @Test
    void canUpdateBoard() {
        String[][] board = {{"X", "", ""}, {"", "", ""}, {"", "", ""}};
        model.setToken(0,0); // X
        assertArrayEquals(board, model.getBoard());
    }

    @Test
    void canNotAddTokenOnOccupiedCell() {
        String[][] board = {{"X", "", ""}, {"", "", ""}, {"", "", ""}};
        model.setToken(0,0); // X
        model.setToken(0,0); // O

        assertArrayEquals(board, model.getBoard());
    }

    @Test
    void boardShouldBeWonVertically() {
        model.setToken(0,0); // X
        model.setToken(0,1); // O
        model.setToken(1,0); // X
        model.setToken(0,2); // O
        model.setToken(2,0); // X

        assertTrue(model.isWinner());
    }

    @Test
    void boardShouldBeWonHorizontally() {
        model.setToken(1,0); // X
        model.setToken(0,0); // O
        model.setToken(1,1); // X
        model.setToken(0,1); // O
        model.setToken(2,0); // X
        model.setToken(0,2); // O

        assertTrue(model.isWinner());
    }

    @Test
    void boardShouldBeWonDiagonally() {
        model.setToken(0,0); // X
        model.setToken(0,1); // O
        model.setToken(1,1); // X
        model.setToken(0,2); // O
        model.setToken(2,2); // X

        assertTrue(model.isWinner());
    }

    @Test
    void boardShouldBeWonDiagonally2() {
        model.setToken(0,2); // X
        model.setToken(0,1); // O
        model.setToken(1,1); // X
        model.setToken(0,0); // O
        model.setToken(2,0); // X

        assertTrue(model.isWinner());
    }

    @Test
    void boardShouldBeFull() {
        model.setToken(0,0); // X
        model.setToken(0,1); // O
        model.setToken(0,2); // X
        model.setToken(1,1); // O
        model.setToken(1,0); // X
        model.setToken(2,0); // O
        model.setToken(1,2); // X
        model.setToken(2,2); // O
        model.setToken(2,1); // X

        assertTrue(model.isFull());
    }

    @Test
    void boardShouldNotBeFull() {
        model.setToken(0,0); // X
        model.setToken(0,1); // O

        assertFalse(model.isFull());
    }

    @Test
    void boardShouldResetAfterAWin() {
        String[][] board = {{"", "", ""}, {"", "", ""}, {"", "", ""}};

        model.setToken(0,2); // X
        model.setToken(0,1); // O
        model.setToken(1,1); // X
        model.setToken(0,0); // O
        model.setToken(2,0); // X

        model.setState(State.PLAYING);
        assertArrayEquals(board, model.getBoard());
    }

    @Test
    void statusShouldBeUpdated() {
        model.setStatus("X's turn");

        assertEquals("X's turn", model.getStatus());
    }

    @Test
    void p1PointsShouldBeUpdated() {
        model.setP1Points();

        assertEquals("Player 1 (X): 1", model.getP1Points());
    }

    @Test
    void p2PointsShouldBeUpdated() {
        model.setP2Points();

        assertEquals("Player 2 (O): 1", model.getP2Points());
    }

    @Test
    void statusPropertyShouldReturn() {
        assertEquals("X's turn", model.statusProperty().getValue());
    }

    @Test
    void p1PointsPropertyShouldReturn() {
        assertEquals("Player 1 (X): 0", model.p1PointsProperty().getValue());
    }

    @Test
    void p2PointsPropertyShouldReturnCorrectValueIfIsLocal() {
        model.setIsLocal(true);
        assertEquals("CPU (O): 0", model.p2PointsProperty().getValue());
    }

    @Test
    void p2PointsPropertyShouldReturnCorrectValueIfIsOnline() {
        model.setIsLocal(false);
        assertEquals("Player 2 (O): 0", model.p2PointsProperty().getValue());
    }

}
