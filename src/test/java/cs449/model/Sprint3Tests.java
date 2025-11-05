package cs449.model;

import cs449.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Sprint3Tests {

    @Test
    void simple_firstSOS_wins_immediately() {
        SosGame g = new SimpleGame(3);

        g.place(0, 0, Letter.S);
        g.place(1, 1, Letter.S);
        g.place(0, 1, Letter.O);
        g.place(2, 2, Letter.S);
        g.place(0, 2, Letter.S);

        assertTrue(g.isOver(), "Game should end on first SOS in Simple mode.");
        assertEquals(Player.BLUE, g.winner(), "Blue should win immediately in Simple mode.");
    }

    @Test
    void general_scoring_and_extra_turn_on_score() {
        SosGame g = new GeneralGame(3);

        g.place(0, 0, Letter.S);
        g.place(1, 1, Letter.S);
        g.place(0, 1, Letter.O);
        g.place(2, 2, Letter.S);
        g.place(0, 2, Letter.S);

        assertEquals(1, g.blueScore(), "Blue score should be 1 after forming SOS in General mode.");
        assertEquals(0, g.redScore(), "Red score should be 0.");
        assertEquals(Player.BLUE, g.turn(), "Blue should keep the turn after scoring in General mode.");
    }
}
