package cs449.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Sprint5Tests {

    // Test simple mode wins immediately on SOS
    @Test
    public void testSimpleModeImmediateWin() {
        SosGame g = new SimpleGame(3);
        g.newGame();

        g.place(0, 0, Letter.S);
        g.place(0, 1, Letter.O);
        g.place(0, 2, Letter.S); // SOS formed

        assertTrue(g.isOver());
        assertEquals(Player.BLUE, g.winner());
    }

    // Test general mode scores accumulate
    @Test
    public void testGeneralModeScoring() {
        SosGame g = new GeneralGame(3);
        g.newGame();

        g.place(0, 0, Letter.S);
        g.place(1, 0, Letter.S);
        g.place(2, 0, Letter.S);

        // No SOS yet
        assertEquals(0, g.blueScore());

        // Now create SOS vertically
        g.place(1, 1, Letter.O);
        g.place(1, 0, Letter.S);

        // Score should increase
        assertTrue(g.blueScore() > 0 || g.redScore() > 0);
    }


    @Test
    public void testInvalidMove() {
        SosGame g = new SimpleGame(3);
        g.newGame();

        assertTrue(g.place(0, 0, Letter.S));
        assertFalse(g.place(0, 0, Letter.O)); // cannot overwrite
    }


    @Test
    public void testComputerChoosesValidMove() {
        ComputerParticipant bot = new ComputerParticipant(Player.BLUE);
        SosGame g = new SimpleGame(3);
        g.newGame();

        int[] move = bot.chooseMove(g);

        assertNotNull(move);
        assertEquals(2, move.length);
        assertTrue(move[0] >= 0 && move[0] < 3);
        assertTrue(move[1] >= 0 && move[1] < 3);
        assertTrue(g.board().isEmpty(move[0], move[1]));
    }

    @Test
    public void testHumanDoesNothing() {
        HumanParticipant hp = new HumanParticipant(Player.RED);
        SosGame g = new SimpleGame(3);
        g.newGame();

        int[] move = hp.chooseMove(g);

        assertNull(move);
    }
}
