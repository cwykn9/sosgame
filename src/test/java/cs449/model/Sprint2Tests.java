package cs449.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Sprint2Tests {

    @Test
    void newGame_initializesBoardAndBlueStarts() {
        var g = new SosGame(3, GameMode.SIMPLE);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(Letter.EMPTY, g.board().get(r, c));
            }
        }
        assertEquals(Player.BLUE, g.turn());
    }

    @Test
    void placeS_onEmptyCell_thenTurnToggles() {
        var g = new SosGame(3, GameMode.SIMPLE);
        assertTrue(g.place(0, 0, Letter.S));
        assertEquals(Letter.S, g.board().get(0, 0));
        assertEquals(Player.RED, g.turn());
    }

    @Test
    void cannotOverwriteCell() {
        var g = new SosGame(3, GameMode.GENERAL);
        assertTrue(g.place(1, 1, Letter.O));
        assertFalse(g.place(1, 1, Letter.S)); // blocked
        assertEquals(Letter.O, g.board().get(1, 1));
    }

    @Test
    void newGame_appliesSizeAndMode_andResetsTurn() {
        var g = new SosGame(5, GameMode.GENERAL);
        g.place(0, 0, Letter.S);            // make some moves first
        g.newGame(3, GameMode.SIMPLE);      // then start a new game
        assertEquals(3, g.board().size());
        assertEquals(GameMode.SIMPLE, g.mode());
        assertEquals(Player.BLUE, g.turn());
    }
}
