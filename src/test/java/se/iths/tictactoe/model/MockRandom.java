package se.iths.tictactoe.model;

import java.util.Random;

public class MockRandom extends Random {
    @Override
    public int nextInt() {
        return 1;
    }
}
