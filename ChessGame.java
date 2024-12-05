public class ChessGame{
    public static final int CHESSBOARDLENGTH = 8;
    Piece[][] chessboard; //starts from a1 in the top left (actual chess has a1 in bottom left)
    public ChessGame(){
        this.chessboard = new Piece[CHESSBOARDLENGTH][CHESSBOARDLENGTH];
        //chessboard[row][column]
        startGame();
    }
    public boolean inBoard(int x, int y){
        return x >= 0 && x < ChessGame.CHESSBOARDLENGTH && y >= 0 && y < ChessGame.CHESSBOARDLENGTH;
    }
    public void startGame(){ //add corresponding pieces
        for (int i = 0; i < CHESSBOARDLENGTH; i++) {
            
        }
    }
    public void displayBoard(){
        for (int i = 0; i < CHESSBOARDLENGTH; i++) {
            for (int j = 0; j < CHESSBOARDLENGTH; j++) {
                try {
                    System.out.print(this.chessboard[i][j].getLetter());
                } catch (Exception e) {
                    System.out.print(" ");
                }
            }
            System.out.println("\n");
        }
    }
    public boolean isOccupied(int x, int y){
        //returns false if nothing is there
        return this.chessboard[x][y] != null;
    }
    public int returnOwner(int x, int y){
        return this.chessboard[x][y].getPlayer();
    }
}