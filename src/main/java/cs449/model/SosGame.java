package cs449.model;

public class SosGame {
    private Board board; private GameMode mode; private Player turn = Player.BLUE;
    public SosGame(int n, GameMode mode){ this.board=new Board(n); this.mode=mode; }
    public Board board(){ return board; } public GameMode mode(){ return mode; } public Player turn(){ return turn; }
    public boolean place(int r,int c, Letter L){
        if(L==Letter.EMPTY) return false; if(!board.isEmpty(r,c)) return false;
        board.set(r,c,L); turn = (turn==Player.BLUE)? Player.RED : Player.BLUE; return true;
    }
    public void newGame(int n, GameMode mode){ this.board=new Board(n); this.mode=mode; this.turn=Player.BLUE; }
}


