package cs449.model;

public class GeneralGame extends SosGame {

    public GeneralGame(int n) {
        super(n, GameMode.GENERAL);
    }

    @Override
    protected void applyScoring(int newSOS) {
        if (newSOS > 0) {
            if (turn() == Player.BLUE) {
                incBlue(newSOS);
            } else {
                incRed(newSOS);
            }
        }
    }

    @Override
    protected void applyTurnPolicy(int newSOS) {
        if (newSOS == 0 && !isOver()) {
            toggleTurn();
        }
    }

    @Override
    protected void updateGameOverAfterMove(int newSOS) {
        if (boardFull()) {
            if (blueScore() > redScore()) {
                setWinner(Player.BLUE);
            } else if (redScore() > blueScore()) {
                setWinner(Player.RED);
            } else {
                setDrawIfNoWinner(); // tie = draw
            }
            setOver(true);
        }
    }
}
