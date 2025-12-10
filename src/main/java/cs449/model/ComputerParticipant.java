package cs449.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ComputerParticipant extends Participant {

    public ComputerParticipant(Player color) {
        super(color);
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    @Override
    public void makeMove(SosGame game) {

        var board = game.board();
        int n = board.size();

        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.isEmpty(r, c)) {
                    empties.add(new int[]{r, c});
                }
            }
        }
        if (empties.isEmpty()) return;

        int[] rc = empties.get(ThreadLocalRandom.current().nextInt(empties.size()));
        int r = rc[0], c = rc[1];

        Letter L = ThreadLocalRandom.current().nextBoolean() ? Letter.S : Letter.O;
        game.place(r, c, L);
    }
}
