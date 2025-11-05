package cs449.model;


public class SimpleGame extends SosGame {

    public SimpleGame(int n) {
        super(n, GameMode.SIMPLE);
    }

    @Override
    protected void applyScoring(int newSOS) {
        if (newSOS > 0) {
            setWinner(turn());
        }
    }

    @Override
    protected void applyTurnPolicy(int newSOS) {
        if (!isOver()) {
            toggleTurn();
        }
    }

    @Override
    protected void updateGameOverAfterMove(int newSOS) {
        if (newSOS > 0) {
            setOver(true);
        } else if (boardFull()) {
            setDrawIfNoWinner();
            setOver(true);
        }
    }
}
