import java.util.ArrayList;
public abstract class Piece{
    private int player;
    private ArrayList<int[]> movementRange;
    private int[] coords = new int[2]; //will be somehting like "23"
    private String name;

    public Piece(int player, int[] coords, String name, boolean pawnMove, boolean diagonal, boolean orthogonally, boolean hopMove){
        this.player = player; //1 is on bottom (white), 2 is on top (black)
        this.movementRange = new ArrayList<>();
        this.coords = coords;
        this.name = name; //first letter should be cap
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
    public char getLetter(){
        return this.name.charAt(0);
    }
    public int getPlayer(){
        return this.player;
    }
    public void addMovementRange(int testX, int testY){
        int[] newCoord = {testX, testY};
        this.movementRange.add(newCoord);
    }
    public void calculateMovementRange(){

    }
    public void calculateDiagonalRange(ChessGame c){//for bishop and queen
        int testX;
        int testY;
        for (int i = 0; i < 4; i++) {
            testX = getX();
            testY = getY();
            while (c.inBoard(testX-1, testY-1) && c.inBoard(testX+1, testY+1)) { 
                switch (i) {
                    case 0: //checking bottom right
                        testX++;
                        testY++;
                        break;
                    case 1: //checking top right
                        testX++;
                        testY--;
                        break;
                    case 2: //checking top left
                        testX--;
                        testY--;
                        break;
                    case 3: //checking bottom left
                        testX--;
                        testY++;
                    default:
                        throw new AssertionError();
                }
                this.addMovementRange(testX, testY);
                if (c.isOccupied(testX, testY)){
                    break;
                }
                
            }
        }
        
    }
    public void calculateOrthogonalRange(ChessGame c){//for bishop and queen
        int testX;
        int testY;
        for (int i = 0; i < 4; i++) {
            testX = getX();
            testY = getY();
            while (c.inBoard(testX-1, testY-1) && c.inBoard(testX+1, testY+1)) { 
                switch (i) {
                    case 0: //checking right
                        testX++;
                        break;
                    case 1: //checking left
                        testX--;
                        break;
                    case 2: //checking up
                        testY++;
                        break;
                    case 3: //checking down
                        testY--;
                        break;
                    default:
                        throw new AssertionError();
                }
                this.addMovementRange(testX, testY);
                if (c.isOccupied(testX, testY)){
                    break;
                }
                
            }
        }
        
    }
    public void calculatePawnRange(ChessGame c){
        int testX = getX();
        int testY = getY();
        if (this.player == 1){ //pawns move up the board so subtract from y
            testX--;
            testY--;
            //this is for capturing
            if(c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
                this.addMovementRange(testX, testY); //check upper left diagonal
            }
            testX += 2;
            if(c.isOccupied(testX, testY) && c.returnOwner(testX, testY) != this.player && c.inBoard(testX, testY)){
                this.addMovementRange(testX, testY); //check upper right diagonal
            }
            testX = getX();
            testY = getY() - 1;
            if(!(c.isOccupied(testX, testY)) && c.inBoard(testX, testY)){//this is for pushing
                this.addMovementRange(testX, testY);
                testY--;
                if(!(c.isOccupied(testX, testY)) && c.inBoard(testX, testY) && getY() == 6){
                    this.addMovementRange(testX, testY);
                }
            }
        }
    }
}