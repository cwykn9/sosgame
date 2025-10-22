package cs449.model;
public class Board {
    private final int n; private final Letter[][] cells;
    public Board(int n){ if(n<3) throw new IllegalArgumentException("n>=3");
        this.n=n; cells=new Letter[n][n];
        for(int r=0;r<n;r++) for(int c=0;c<n;c++) cells[r][c]=Letter.EMPTY;
    }
    public int size(){ return n; }
    public boolean in(int r,int c){ return r>=0&&r<n&&c>=0&&c<n; }
    public Letter get(int r,int c){ return in(r,c)?cells[r][c]:null; }
    public boolean isEmpty(int r,int c){ return in(r,c)&&cells[r][c]==Letter.EMPTY; }
    void set(int r,int c, Letter L){ if(!in(r,c)) throw new IndexOutOfBoundsException(); cells[r][c]=L; }
}
