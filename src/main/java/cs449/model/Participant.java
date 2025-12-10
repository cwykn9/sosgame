package cs449.model;

public abstract class Participant {

    private final Player color;

    protected Participant(Player color) {
        this.color = color;
    }

    public Player color() {
        return color;
    }

    public abstract boolean isComputer();

    public abstract void makeMove(SosGame game);
}
