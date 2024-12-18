import java.util.ArrayList;
import java.util.Arrays;
public class Piece{
    private final int player;
    private final ArrayList<int[]> movementRange;
    private int[] coords = new int[2]; //will be somehting like [2, 3]
    private final String name;
    private final String movements;
    private boolean castled;

    public Piece(int player, int[] coords, String name, String movements){
        this.player = player; //1 is on bottom (white), 2 is on top (black)
        this.movementRange = new ArrayList<>();
        this.coords = coords;
        this.name = name; //first letter should be cap
        this.movements = movements;
        this.castled = false; //only for kings
    }
    //some getters
    public int getX(){
        return this.coords[0];
    }
    public int getY(){
        return this.coords[1];
    }
    public int[] getCoords(){
        return this.coords;
    }
    public String getName(){
        return this.name;
    }
    public String getLetter(){
        return this.name.substring(0,1);
    }
    public int getPlayer(){
        return this.player;
    }
    public ArrayList<int[]> getMovementRange(ChessGame c){
        return this.movementRange;
    }
    //important public methods
    public boolean inMovementRange(int x, int y){
        int[] coord = {x, y};
        for (int elem = 0; elem < movementRange.size(); elem++) {
            if(Arrays.equals(movementRange.get(elem), coord)){
                return true; //green the available movement squares
            }
        }
        return false;
    }
    public ArrayList<int[]> calculateMovementRange(ChessGame c){
        clearMovementRange();
        if(this.movements.contains("p")){
            calculatePawnRange(c);
        }
        if(this.movements.contains("d")){
            calculateODRange(c, 'd');
        }
        if(this.movements.contains("o")){
            calculateODRange(c, 'o');
        }
        if(this.movements.contains("n") || this.movements.contains("k")){
            calculateKRange(c);
        }
        return this.movementRange;
    }
    @Override
    public String toString(){
        return this.name + " at " + this.getX() + ", " + this.getY() + ", owned by Player " + this.player;
    }
    public void move(int[] coord, ChessGame c){
        if("King".equals(this.name) && Math.abs(coord[0] - this.coords[0]) == 2){ //check if king moved 2 squares
            if(coord[0] - this.coords[0] > 0){ //castling right
                c.castleMoveRook(this, 1);
            } else { //castling left
                c.castleMoveRook(this, -1);
            }
        }
        c.chessboard[this.coords[1]][this.coords[0]] = null;
        this.coords = coord;
        c.chessboard[this.coords[1]][this.coords[0]] = this;
        if("Pawn".equals(this.name)){
            if((this.player == 1 && getY() == 0) || (this.player == 2 && getY() == 7)){
                c.promotePawn(this);
            }
        }
    }
    public void changeCastle(){
        this.castled = true;
    }
    public void move(int x, int y, ChessGame c){
        int[] newCoords = {x, y};
        move(newCoords, c);
    }
    //private methods below only for calculating movement
    private void addMovementRange(int testX, int testY){
        int[] newCoord = {testX, testY};
        this.movementRange.add(newCoord);
    }
    private void clearMovementRange(){
        this.movementRange.removeAll(this.movementRange);
    }
    private int[] returnODTest(int c, char d){
        int[] test = new int[2];
        if(d == 'd'){
            switch (c) {//for diagonal
                case 0 -> {
                    //checking bottom right
                    test[0] = 1;
                    test[1] = 1;
                }
                case 1 -> {
                    //checking top right
                    test[0] = 1;
                    test[1] = -1;
                }
                case 2 -> {
                    //checking top left
                    test[0] = -1;
                    test[1] = -1;
                }
                case 3 -> {
                    //checking bottom left
                    test[0] = -1;
                    test[1] = 1;
                }
                default -> throw new AssertionError("case has to be 0, 1, 2, or 3");
            }
            //for diagonal
                    } else {//for orthogonal
            switch (c) {
                case 0 -> {
                    //checking bottom right
                    test[0] = 1;
                    test[1] = 0;
                }
                case 1 -> {
                    //checking top right
                    test[0] = -1;
                    test[1] = 0;
                }
                case 2 -> {
                    //checking top left
                    test[0] = 0;
                    test[1] = -1;
                }
                case 3 -> {
                    //checking bottom left
                    test[0] = 0;
                    test[1] = 1;
                }
                default -> throw new AssertionError("case has to be 0, 1, 2, or 3");
            }
        }
        return test;
    }
    private void calculateODRange(ChessGame c, char d){//for bishop and queen and rook
        int testX;
        int testY;
        int[] test;
        for (int i = 0; i < 4; i++) {
            testX = this.getX();
            testY = this.getY();
            while (c.inBoard(testX, testY)) {
                test = returnODTest(i, d);
                testX += test[0];
                testY += test[1];
                if(c.inBoard(testX, testY)){
                    if (c.isOccupied(testX, testY)){//a piece in the way stops the movement
                        if(c.returnOwner(testX, testY) != this.player){
                            this.addMovementRange(testX, testY);//allow capture if is enemy piece
                        }
                        break;
                    } else {
                        this.addMovementRange(testX, testY);
                    }
                }
                
            }
        }
        
    }
    private int alterPawnY(){
        if(this.player == 1){
            return -1;
        }
        return 1;
    }
    private void calculatePawnRange(ChessGame c){
        int testX = this.getX();
        int testY = this.getY();

        testX--;
        testY += alterPawnY();
        //this is for capturing
        if(c.inBoard(testX, testY) && c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
            this.addMovementRange(testX, testY); //check upper left diagonal
        }
        testX = this.getX() + 1;
        if(c.inBoard(testX, testY) && c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
            this.addMovementRange(testX, testY); //check upper right diagonal
        }
        testX = this.getX();
        testY = this.getY() + alterPawnY();
        if(c.inBoard(testX, testY) && !(c.isOccupied(testX, testY))){//this is for pushing
            this.addMovementRange(testX, testY);
            testY += alterPawnY();
            
            if(c.inBoard(testX, testY) && !(c.isOccupied(testX, testY))){
                if((this.player == 1 && this.getY() == 6)||(this.player == 2 && this.getY() == 1)){
                    this.addMovementRange(testX, testY);//check if pawn can move 2 squares
                }
            }
        }
    }
    private int[][] returnKnightTests(){
        int[][] tests = {
            {this.getX()-1, this.getY()-2},
            {this.getX()+1, this.getY()-2},
            {this.getX()+2, this.getY()-1},
            {this.getX()+2, this.getY()+1},
            {this.getX()+1, this.getY()+2},
            {this.getX()-1, this.getY()+2},
            {this.getX()-2, this.getY()-1},
            {this.getX()-2, this.getY()+1}};
        return tests;
    }
    private int[][] returnKingTests(boolean castled){
        if(castled){
            int[][] tests = {
                {this.getX()-1, this.getY()-1},
                {this.getX(), this.getY()-1},
                {this.getX()+1, this.getY()-1},
                {this.getX()+1, this.getY()},
                {this.getX()+1, this.getY()+1},
                {this.getX(), this.getY()+1},
                {this.getX()-1, this.getY()+1},
                {this.getX()-1, this.getY()}};
            return tests;
        } else {
            int[][] tests = {
                {this.getX()-1, this.getY()-1},
                {this.getX(), this.getY()-1},
                {this.getX()+1, this.getY()-1},
                {this.getX()+1, this.getY()},
                {this.getX()+1, this.getY()+1},
                {this.getX(), this.getY()+1},
                {this.getX()-1, this.getY()+1},
                {this.getX()-1, this.getY()},
                {this.getX()-2, this.getY()},
                {this.getX()+2, this.getY()}};
            return tests;
        }
    }
    private boolean canCastle(int side, ChessGame c){
        //side is positive or negative, ONLY FOR KINGS
        if(!this.castled){//king hasnt castled yet
            if(this.getX() != 4 || (this.getY() != 0 && this.getY() != 7)){//king is in wrong place
            } else {
                //king is in right place
                if(side > 0){//test kingside castle
                    if (c.isOccupied(7, this.getY()) && c.getPiece(7, this.getY()).getName().equals("Rook")
                            && !c.isOccupied(6, this.getY())
                            && !c.isOccupied(5, this.getY())){//if rook is in corner and no pieces in the way
                        return true;
                    }
                } else if(side < 0){//test queenside castle
                    if (c.isOccupied(0, this.getY()) && c.getPiece(0, this.getY()).getName().equals("Rook")
                            && !c.isOccupied(1, this.getY())
                            && !c.isOccupied(2, this.getY())
                            && !c.isOccupied(3, this.getY())){//if rook is in corner and no pieces in the way
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void calculateKRange(ChessGame c) {
        int[][] tests;
        if (this.movements.contains("n")){
            tests = returnKnightTests();
        } else {
            tests = returnKingTests(this.castled);
        }
        for(int[] test : tests){
            if (c.inBoard(test[0], test[1])){
                if (c.isOccupied(test[0], test[1])){
                    if(c.returnOwner(test[0], test[1]) != this.player){
                        this.addMovementRange(test[0], test[1]);
                    }
                } else {
                    if(this.getName().equals("King") && Math.abs(test[0] - this.getX()) == 2){
                        //we are checking a castle test
                        if(canCastle(test[0] - this.getX(), c)){
                            this.addMovementRange(test[0], test[1]);
                        }
                    } else{//we are checking a normal test
                        this.addMovementRange(test[0], test[1]);
                    }
                    
                }
            }
        }
    }
}