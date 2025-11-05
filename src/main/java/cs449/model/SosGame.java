package cs449.model;

public abstract class SosGame {
    private final Board board;
    private Player turn = Player.BLUE;
    private final GameMode mode;
    private int blue = 0, red = 0;
    private boolean over = false;
    private Player winner = null;

    protected SosGame(int n, GameMode mode) {
        this.board = new Board(n);
        this.mode = mode;
    }

    public Board board() { return board; }
    public Player turn() { return turn; }
    public GameMode mode() { return mode; }
    public int blueScore() { return blue; }
    public int redScore() { return red; }
    public boolean isOver() { return over; }
    public Player winner() { return winner; }

    public void newGame() {
        int n = board.size();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                board.set(r, c, Letter.EMPTY);
            }
        }
        turn = Player.BLUE;
        blue = red = 0;
        over = false;
        winner = null;
    }

    public boolean place(int r, int c, Letter L) {
        if (isOver()) return false;
        if (L == Letter.EMPTY) return false;
        if (!board.isEmpty(r, c)) return false;

        board.set(r, c, L);

        int newSOS = countNewSOS(r, c);
        applyScoring(newSOS);
        updateGameOverAfterMove(newSOS);
        applyTurnPolicy(newSOS);

        return true;
    }

    protected abstract void applyScoring(int newSOS);
    protected abstract void applyTurnPolicy(int newSOS);
    protected abstract void updateGameOverAfterMove(int newSOS);

    protected void toggleTurn() { turn = (turn == Player.BLUE ? Player.RED : Player.BLUE); }
    protected void setOver(boolean v) { over = v; }
    protected void setWinner(Player p) { winner = p; }
    /** Using null to represent draw; method kept for clarity/extensibility. */
    protected void setDrawIfNoWinner() { if (winner == null) winner = null; }
    protected void incBlue(int by) { blue += by; }
    protected void incRed(int by) { red += by; }

    protected boolean boardFull() {
        int n = board.size();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board.get(r, c) == Letter.EMPTY) return false;
            }
        }
        return true;
    }

    protected int countNewSOS(int r, int c) {
        int n = board.size();
        int cnt = 0;

        int[][] dirs = {
                {1, 0},
                {0, 1},
                {1, 1},
                {1, -1}
        };

        Letter here = board.get(r, c);

        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];

            if (here == Letter.O) {
                int r1 = r - dr, c1 = c - dc;
                int r2 = r + dr, c2 = c + dc;
                if (in(n, r1, c1) && in(n, r2, c2)) {
                    if (board.get(r1, c1) == Letter.S && board.get(r2, c2) == Letter.S) {
                        cnt++;
                    }
                }
            }

            if (here == Letter.S) {
                int rMid = r + dr, cMid = c + dc;
                int rEnd = r + 2*dr, cEnd = c + 2*dc;
                if (in(n, rMid, cMid) && in(n, rEnd, cEnd)) {
                    if (board.get(rMid, cMid) == Letter.O && board.get(rEnd, cEnd) == Letter.S) {
                        cnt++;
                    }
                }

                int rMidB = r - dr, cMidB = c - dc;
                int rEndB = r - 2*dr, cEndB = c - 2*dc;
                if (in(n, rMidB, cMidB) && in(n, rEndB, cEndB)) {
                    if (board.get(rMidB, cMidB) == Letter.O && board.get(rEndB, cEndB) == Letter.S) {
                        cnt++;
                    }
                }
            }
        }
        return cnt;
    }

    private boolean in(int n, int r, int c) {
        return r >= 0 && r < n && c >= 0 && c < n;
    }
}
