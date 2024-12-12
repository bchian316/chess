import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ChessGame{
    public static final int CHESSBOARDLENGTH = 8;
    Piece[][] chessboard; //starts from a1 in the top left (actual chess has a1 in bottom left)
    Scanner scanner;

    public ChessGame(){
        this.chessboard = new Piece[CHESSBOARDLENGTH][CHESSBOARDLENGTH];
        scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess!");
        System.out.println("This is a simplified version of Chess without checks and checkmate.");
        System.out.println("All you have to do is capture the enemy King without losing your own!");
        System.out.println("Move pieces by first selecting the piece's coordinate using chess notation");
        System.out.println("Then, type the coordinate of your desired move");
    }
    public int getOppositePlayer(int player){
        if(player == 1){
            return 2;
        }
        return 1;
    }
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {//only for windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {//for everything else
                System.out.print("\033[H\033[2J");//print a ansi escape code that clears the entire screen
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
        }
    }
    public static void cprint(String message, String color, String modifier){
        switch (color) {
            case "black":
                color = "30";
                break;
            case "red":
                color = "31";
                break;
            case "green":
                color = "32";
                break;
            case "yellow":
                color = "33";
                break;
            case "blue":
                color = "34";
                break;
            case "purple":
                color = "35";
                break;
            case "cyan":
                color = "36";
                break;
            case "white":
                color = "37";
                break;
        }
        switch (modifier) {
            case "normal":
                modifier = "0;";
                break;
            case "bold":
                modifier = "1;";
                break;
            case "italic":
                modifier = "3;";
                break;
            case "underline":
                modifier = "4;";
                break;
            case "background":
                modifier = "";
                color = Integer.toString(Integer.parseInt(color)+10);
        }
        System.out.print("\033["+modifier+color+"m"+message+"\033[0m");
    }
    public boolean inBoard(int x, int y){
        return x >= 0 && x < ChessGame.CHESSBOARDLENGTH && y >= 0 && y < ChessGame.CHESSBOARDLENGTH;
    }
    public void createPiece(int player, int x, int y, String name){
        String movement = "";
        switch (name) {
            case "Pawn":
                movement = "p";
                break;
            case "Bishop":
                movement = "d";
                break;
            case "Rook":
                movement = "o";
                break;
            case "Queen":
                movement = "do";
                break;
            case "Night":
                movement = "n";
                break;
            case "King":
                movement = "k";
                break;
            default:
                throw new AssertionError("Must pass in Night, King, Bishop, Queen, Rook, or Pawn");
        }
        int[] coords = {x, y};
        this.chessboard[y][x] = new Piece(player, coords, name, movement);
    }
    public final void startGame(){ //add corresponding pieces
        createPiece(2, 0, 0, "Rook");
        createPiece(2, 1, 0, "Night");
        createPiece(2, 2, 0, "Bishop");
        createPiece(2, 3, 0, "Queen");
        createPiece(2, 4, 0, "King");
        createPiece(2, 5, 0, "Bishop");
        createPiece(2, 6, 0, "Night");
        createPiece(2, 7, 0, "Rook");
        for (int i = 0; i < CHESSBOARDLENGTH; i++) {
            createPiece(2, i, 1, "Pawn");
        }
        for (int i = 0; i < CHESSBOARDLENGTH; i++) {
            createPiece(1, i, 6, "Pawn");
        }
        createPiece(1, 0, 7, "Rook");
        createPiece(1, 1, 7, "Night");
        createPiece(1, 2, 7, "Bishop");
        createPiece(1, 3, 7, "Queen");
        createPiece(1, 4, 7, "King");
        createPiece(1, 5, 7, "Bishop");
        createPiece(1, 6, 7, "Night");
        createPiece(1, 7, 7, "Rook");

        playGame();
    }
    public void displayBoard(Piece piece){
        clearConsole();
        String mes, mod, col;
        for (int y = 0; y < CHESSBOARDLENGTH; y++) {
            cprint(Integer.toString(CHESSBOARDLENGTH-y) + " ", "white", "bold");
            for (int x = 0; x < CHESSBOARDLENGTH; x++) {
                mes = " - ";
                mod = "normal";
                col = "white";
                int[] coord = {x, y};
                if(piece != null && piece.getX() == coord[0] && piece.getY() == coord[1]){
                    mod = "background";
                }
                if(isOccupied(x, y)){//if there is a piece there
                    if(getPiece(x, y).getPlayer() == 1){//set default color
                        col = "blue";//p1 is blue
                    } else {
                        col = "red";//p2 is red
                    }
                    mes = " " + getPiece(x, y).getLetter() + " ";
                }
                if(piece != null){
                    ArrayList<int[]> movementRange = piece.calculateMovementRange(this);
                    for (int elem = 0; elem < movementRange.size(); elem++) {
                        if(Arrays.equals(movementRange.get(elem), coord)){
                            mod = "background";
                            col = "green"; //green the available movement squares
                        }
                    }
                }
                cprint(mes, col, mod);
            }
            System.out.println();
        }
        cprint("   a  b  c  d  e  f  g  h\n", "white", "bold");
    }
    public void displayBoard(){
        displayBoard(null);
    }
    public Piece getPiece(int x, int y){
        return this.chessboard[y][x];
    }
    public boolean isOccupied(int x, int y){
        //returns false if nothing is there
        return getPiece(x, y) != null;
    }
    public int returnOwner(int x, int y){
        try {
            return getPiece(x, y).getPlayer();
        } catch (NullPointerException e) {
            return -1;
        }
        
    }
    public void playGame(){
        int currentPlayer = 1;
        while (true) {
            this.displayBoard();
            this.doTurn(currentPlayer);
            currentPlayer = getOppositePlayer(currentPlayer);
        }
    }
    public void doTurn(int currentPlayer){
        int[] coord;
        do { //select piece
            coord = getUserCoord(currentPlayer, "Player " + currentPlayer + " turn:\nSelect a piece by typing it's coordinate", null);
        } while (returnOwner(coord[0], coord[1]) != currentPlayer);
        Piece selectedPiece = getPiece(coord[0], coord[1]);

        do { //select where to move piece
            coord = getUserCoord(0, "Player " + currentPlayer + " turn:\nType the coordinate of where you want to move", selectedPiece);
            if(isOccupied(coord[0], coord[1]) && getPiece(coord[0], coord[1]).getPlayer() == currentPlayer){
                selectedPiece = getPiece(coord[0], coord[1]);
            }
        } while (!selectedPiece.inMovementRange(coord[0], coord[1]));//this allows deselecting because a friendly piece will never be in another friendly piece's movement range
        selectedPiece.move(coord, this);
        if(selectedPiece.getName().equals("King")){
            selectedPiece.changeCastle();//any king move will prohibit castling
        }
        this.getEnemyKing(currentPlayer);//check if enemy king is captured
        
    }
    public int[] getUserCoord(int occupied, String prompt, Piece selectedPiece){
        //if occupied is 0, then we can select any space
        // if it is 1 or 2 then we have to select a space with a piece there owned by the player
        String input = "";
        int x = -1;
        int y = -1;
        do {
            displayBoard(selectedPiece);
            System.out.println(prompt);
            try {
                input = scanner.nextLine();
                x = (input.charAt(0) - 'a');
                y = CHESSBOARDLENGTH - Integer.parseInt(input.substring(1, 2));
            } catch (NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException | NoSuchElementException e){
            }
            
        } while (input.length() != 2 //the input is the wrong length
                || !inBoard(x, y)//the y is too large (player can type 1-8 inclusive)
                || (occupied != 0 && !isOccupied(x, y)) //an empty spot is selected
                || (occupied != 0 && getPiece(x, y).getPlayer() != occupied)//if u try to select ur opponent's piece
                );
        int[] coord = {x, y};
        return coord;
    }
    public void promotePawn(Piece p){
        char playerChoice = ' ';//we create an entirely new piece so we dont have to make setters :)
        displayBoard();
        System.out.println("Choose promotion: (Q)ueen, (R)ook, (B)ishop, (K)night");
        do {
            playerChoice = scanner.nextLine().toLowerCase().charAt(0);
        } while (playerChoice != 'q' && playerChoice != 'b' && playerChoice != 'r' && playerChoice != 'k');
        switch (playerChoice) {
            case 'q':
                createPiece(p.getPlayer(), p.getX(), p.getY(), "Queen");
                break;
            case 'r':
                createPiece(p.getPlayer(), p.getX(), p.getY(), "Rook");
                break;
            case 'b':
                createPiece(p.getPlayer(), p.getX(), p.getY(), "Bishop");
                break;
            case 'k':
                createPiece(p.getPlayer(), p.getX(), p.getY(), "Night");
                break;
            default:
                throw new AssertionError();
        }
    }
    public void castleMoveRook(Piece king, int side){
        //assume that rook and king are already at correct places
        //side is either 1 or -1
        if(side == 1){
            this.getPiece(7, king.getY()).move(5, king.getY(), this);
        } else {
            this.getPiece(0, king.getY()).move(3, king.getY(), this);
        }
    }
    
    public Piece getEnemyKing(int player){ //this gets the king of the opposite player
        for (Piece[] row : chessboard) {
            for (Piece p : row) {
                if(p != null && p.getName().equals("King") && p.getPlayer() != player){
                    return p;
                }
            }
        }
        displayBoard();
        System.out.println("Player " + player + " WINS!!! yay");
        System.exit(0);
        return null; //this line will not be executed
    }
}