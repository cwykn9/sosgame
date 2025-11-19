package cs449;

import cs449.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Sprint4Tests {

    @Test
    void computerMove_placesValidLetterInEmptyCell() {
        SosGame g = new GeneralGame(5);
        g.newGame();

        int emptyCountBefore = countEmpty(g);

        boolean placed = g.place(0, 0, Letter.S);

        assertTrue(placed, "Computer (or human) should be able to place S in an empty cell");
        assertEquals(Letter.S, g.board().get(0, 0));
        assertEquals(emptyCountBefore - 1, countEmpty(g));
    }

    @Test
    void computerCannotOverwriteCell() {
        SosGame g = new GeneralGame(3);
        g.newGame();

        assertTrue(g.place(1, 1, Letter.O));
        assertFalse(g.place(1, 1, Letter.S), "Cannot overwrite existing cell");
        assertEquals(Letter.O, g.board().get(1, 1));
    }

    @Test
    void simpleModeEndsAfterFirstSOS() {
        SosGame g = new SimpleGame(3);
        g.newGame();

        // Create S O S horizontally:
        g.place(0, 0, Letter.S);
        g.place(0, 1, Letter.O);
        g.place(0, 2, Letter.S);

        assertTrue(g.isOver(), "Simple mode must end immediately after SOS");
        assertEquals(Player.BLUE, g.winner(), "Blue made the first SOS");
    }

    @Test
    void generalModeContinuesAfterSOS() {
        SosGame g = new GeneralGame(3);
        g.newGame();

        // Same pattern as before, Blue makes SOS:
        g.place(0, 0, Letter.S);
        g.place(0, 1, Letter.O);
        g.place(0, 2, Letter.S);

        assertFalse(g.isOver(), "General mode must continue after forming SOS");
        assertEquals(1, g.blueScore(), "Blue should have 1 point");
    }

    @Test
    void generalModeEndsWhenBoardFull() {
        SosGame g = new GeneralGame(3);
        g.newGame();


        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (g.board().isEmpty(r, c)) {
                    g.place(r, c, Letter.S);
                }
            }
        }

        assertTrue(g.isOver(), "Game must end when the board is full in General mode");
    }

    private int countEmpty(SosGame g) {
        int n = g.board().size();
        int count = 0;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (g.board().get(r, c) == Letter.EMPTY) {
                    count++;
                }
            }
        }
        return count;
    }
}
