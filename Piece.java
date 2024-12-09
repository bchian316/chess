import java.util.ArrayList;
import java.util.Arrays;
public class Piece{
    private final int player;
    private final ArrayList<int[]> movementRange;
    private int[] coords = new int[2]; //will be somehting like [2, 3]
    private final String name;
    private final String movements;

    public Piece(int player, int[] coords, String name, String movements){
        this.player = player; //1 is on bottom (white), 2 is on top (black)
        this.movementRange = new ArrayList<>();
        this.coords = coords;
        this.name = name; //first letter should be cap
        this.movements = movements;
    }
    public int getX(){
        return this.coords[0];
    }
    public int getY(){
        return this.coords[1];
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
    public boolean inMovementRange(int x, int y){
        int[] coord = {x, y};
        for (int elem = 0; elem < movementRange.size(); elem++) {
            if(Arrays.equals(movementRange.get(elem), coord)){
                return true; //green the available movement squares
            }
        }
        return false;
    }
    public void addMovementRange(int testX, int testY){
        int[] newCoord = {testX, testY};
        this.movementRange.add(newCoord);
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
    public void clearMovementRange(){
        this.movementRange.removeAll(this.movementRange);
    }
    public int[] returnODTest(int c, char d){
        int[] test = new int[2];
        if(d == 'd'){
            switch (c) {//for diagonal
                case 0: //checking bottom right
                    test[0] = 1;
                    test[1] = 1;
                    break;
                case 1: //checking top right
                    test[0] = 1;
                    test[1] = -1;
                    break;
                case 2: //checking top left
                    test[0] = -1;
                    test[1] = -1;
                    break;
                case 3: //checking bottom left
                    test[0] = -1;
                    test[1] = 1;
                    break;
                default:
                    throw new AssertionError("case has to be 0, 1, 2, or 3");
            }
        } else {//for orthogonal
            switch (c) {
                case 0: //checking bottom right
                    test[0] = 1;
                    test[1] = 0;
                    break;
                case 1: //checking top right
                    test[0] = -1;
                    test[1] = 0;
                    break;
                case 2: //checking top left
                    test[0] = 0;
                    test[1] = -1;
                    break;
                case 3: //checking bottom left
                    test[0] = 0;
                    test[1] = 1;
                    break;
                default:
                    throw new AssertionError("case has to be 0, 1, 2, or 3");
            }
        }
        return test;
    }
    public void calculateODRange(ChessGame c, char d){//for bishop and queen
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
    public int alterPawnY(){
        if(this.player == 1){
            return -1;
        }
        return 1;
    }
    public void calculatePawnRange(ChessGame c){
        int testX = this.getX();
        int testY = this.getY();

        testX--;
        testY += alterPawnY();
        //this is for capturing
        if(c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
            this.addMovementRange(testX, testY); //check upper left diagonal
        }
        testX = this.getX() + 1;
        if(c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
            this.addMovementRange(testX, testY); //check upper right diagonal
        }
        testX = this.getX();
        testY = this.getY() + alterPawnY();
        if(!(c.isOccupied(testX, testY)) && c.inBoard(testX, testY)){//this is for pushing
            this.addMovementRange(testX, testY);
            testY += alterPawnY();
            
            if(!(c.isOccupied(testX, testY)) && c.inBoard(testX, testY)){
                if((this.player == 1 && this.getY() == 6)||(this.player == 2 && this.getY() == 1)){
                    this.addMovementRange(testX, testY);//check if pawn can move 2 squares
                }
            }
        }
    }
    public int[][] returnKnightTests(){
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
    public int[][] returnKingTests(){
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
    }
    public void calculateKRange(ChessGame c) {
        int[][] tests;
        if (this.movements.contains("n")){
            tests = returnKnightTests();
        } else {
            tests = returnKingTests();
        }
        for(int[] test : tests){
            if (c.inBoard(test[0], test[1])){
                if (c.isOccupied(test[0], test[1])){
                    if(c.returnOwner(test[0], test[1]) != this.player){
                        this.addMovementRange(test[0], test[1]);
                    }
                } else {
                    this.addMovementRange(test[0], test[1]);
                }
            }
        }
    }
    @Override
    public String toString(){
        return this.name + " at " + this.getX() + ", " + this.getY() + ", owned by Player " + this.player;
    }
    public void move(int[] coord, ChessGame c){
        c.chessboard[this.coords[1]][this.coords[0]] = null;
        this.coords = coord;
        c.chessboard[this.coords[1]][this.coords[0]] = this;
    }
}