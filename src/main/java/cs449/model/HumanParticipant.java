package cs449.model;

public class HumanParticipant extends Participant {

    public HumanParticipant(Player color) {
        super(color);
    }

    @Override
    public boolean isComputer() {
        return false;
    }

    @Override
    public void makeMove(SosGame game) {

    }
}
