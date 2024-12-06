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
        //chessboard[y][x]
        this.startGame();
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
                modifier = "0";
                break;
            case "bold":
                modifier = "1";
                break;
            case "italic":
                modifier = "3";
                break;
            case "underline":
                modifier = "4";
        }
        System.out.print("\033["+modifier+";"+color+"m"+message+"\033[0m");
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
    public void displayBoard(ArrayList<int[]> movementRange, int pieceX, int pieceY){
        System.out.println("   1  2  3  4  5  6  7  8\n");
        String mes, mod, col;
        for (int y = 0; y < CHESSBOARDLENGTH; y++) {
            System.out.print((y+1) + " ");
            for (int x = 0; x < CHESSBOARDLENGTH; x++) {
                mes = " O ";
                mod = "normal";
                col = "white";
                int[] coord = {x, y};
                if(isOccupied(x, y)){//if there is a piece there
                    if(this.chessboard[y][x].getPlayer() == 1){//set default color
                        col = "blue";
                    } else {
                        col = "red";
                    }
                    mes = " " + this.chessboard[y][x].getLetter() + " ";
                } else {//for empty spaces
                    mes = " O ";
                }
                for (int elem = 0; elem < movementRange.size(); elem++) {
                    if(Arrays.equals(movementRange.get(elem), coord)){
                        col = "green"; //green the available movement squares
                    }
                }
                cprint(mes, col, mod);
            }
            System.out.println("\n");
        }
    }
    public void displayBoard(){
        ArrayList<int[]> empty = new ArrayList<>();
        displayBoard(empty, -1, -1);
    }
    public boolean isOccupied(int x, int y){
        //returns false if nothing is there
        return this.chessboard[y][x] != null;
    }
    public int returnOwner(int x, int y){
        return this.chessboard[y][x].getPlayer();
    }
    public void playGame(){
        int currentPlayer = 1;
        while (true) {
            //this.clearConsole();
            this.displayBoard();
            this.doTurn(currentPlayer);
            if (currentPlayer == 1){
                currentPlayer = 2;
            } else {
                currentPlayer = 1;
            }
        }
    }
    public void doTurn(int currentPlayer){
        String input = "";
        int x = -1;
        int y = -1;
        System.out.println("Player " + currentPlayer + " turn:\nSelect a piece by typing it's 2 number coordinate (1-8)\ne.g. 1 4");
        do {
            try {
                input = scanner.nextLine();
                x = Integer.parseInt(input.substring(0, 1)) - 1;
                y = Integer.parseInt(input.substring(2, 3)) - 1;
            } catch (NumberFormatException | StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException | NoSuchElementException e){
            }
            
        } while (!inBoard(x, y)//the y is too large (player can type 1-8 inclusive)
                || !isOccupied(x, y) //an empty spot is selected
                || returnOwner(x, y) != currentPlayer  //player does not own the piece
                || input.length() != 3 //the input is the wrong length
                || input.charAt(1) != ' ' //the middle char is not a space
                //|| !this.chessboard[y][x].getMovementRange(this).isEmpty()
                );
        
        Piece selectedPiece = chessboard[y][x];
        clearConsole();
        displayBoard(selectedPiece.getMovementRange(this), selectedPiece.getX(), selectedPiece.getY());
        int var = scanner.nextInt();
    }
}