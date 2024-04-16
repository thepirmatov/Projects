
/**
 * It is Main class of Tic-Tac-Toe game.
 *
 * @author Beksultan Pirmatov
 * @version 10/03/2020
 */
import java.util.Scanner;

public class Main
{
    static Scanner keyboard = new Scanner(System.in);
    final static int NBR_OF_GAMES = 3;
    final static int FIRST_PLAYER_WINS = 1;
    final static int SECOND_PLAYER_WINS = 2;
    final static int TIE = 0;
    public static String player1Char = "X";
    public static String player2Char = "O";
    public static int boardRow = 3;
    public static int boardCol = 3;
    private final static int MIN_COL_ROW = 3;
    private final static int MAX_COL_ROW = 9;
    public static String [][] gameboard;
    public static int[] player1ScoreCount = new int[10];
    public static int[] player2ScoreCount = new int[10];
    private static int player1TotalScore = 0;
    private static int player2TotalScore = 0;
    /**
     * it is main function of tic-tac-toe game
     *
     */
    public static void main(String args[]){
        do{
            firstMenu();
        }
        while(IR4.getYorN("Play again? y/n"));
    }

    public static void firstMenu(){
        int choice;
        do{
            choice = getFirstMenu("1. Classic Tic-Tac-Toe\n2. Tic-Toc-Tic-Tac-Toe Blackout\n3. Exit");
            switch(choice){
                case 1:
                menuClassic();
                break;
                case 2:
                menuBlackout();
                break;
            }
        }while(choice != 3);
    }

    public static int getFirstMenu(String msg){
        int value = IR4.getInteger(msg);
        while(isInvalidFirstMenuChoice(value)){
            System.out.println("Incorrect choice!!!");
            System.out.println("Try again.");
            value = IR4.getInteger(msg);
        }

        return value;
    }

    public static boolean isInvalidFirstMenuChoice(int value){
        if(value<1 || value > 3){
            return true;
        }
        return false;
    }

    public static void menuBlackout(){
        displayWelcome();
        int choice;
        displayBoardSizeAndPlayers(boardRow, boardCol, player1Char, player2Char);
        int matchResult;
        String [][] gameboardDefault = new String [boardRow][boardCol];
        boolean quit = false;
        do{
             choice = displaySecondMenu();
            switch(choice){
                case 1: 
                    displayRules(false); 
                    break;
                case 2: 
                    getPlayer();
                    break;
                case 3: 
                    gameboard = getBoardSizeBlackout();
                    break;
                case 4:
                    if(boardRow == 3 && boardCol == 3){
                        matchResult = playMatch(gameboardDefault, false);
                    }
    
                    else matchResult = playMatch(gameboard, false);
                    displayFinalResults(matchResult); 
                    break;
                case 5:
                    quit = true;
                    break;
            }
        }while(!quit);
    }

    public static void menuClassic(){
        displayWelcome();
        displayBoardSizeAndPlayers(boardRow, boardCol, player1Char, player2Char);
        int matchResult;
        String [][] gameboardDefault = new String [boardRow][boardCol];
        int choice;
        boolean quit = false;
        do{
            choice = displaySecondMenu(); 
            switch(choice){
                case 1: 
                displayRules(true); 
                break;
                case 2: 
                getPlayer();
                break;
                case 3: 
                gameboard = getBoardSizeClassic(); 
                break;
                case 4:
                if(boardRow == 3 && boardCol == 3){
                    matchResult = playMatch(gameboardDefault, true);
                }

                else matchResult = playMatch(gameboard, true);
                displayFinalResults(matchResult); 
                break;
                case 5:
                    quit = true;
                    break;
            }
            //choice = displaySecondMenu();
        }while(choice != 5);
    }

    public static String [][] getBoardSizeBlackout(){
        String [][] board;
        int row = IR4.getInteger("Enter your value for the ROW of the board (3-9): ");
        while(isInvalidBoardSizeBlackout(row)){
            System.out.println("Enter only 3-9 numbers");
            row = IR4.getInteger("Enter your value for the ROW of the board (3-9): ");
        }

        boardRow = row;

        int col = IR4.getInteger("Enter your value for the COLUMN of the board (3-9): ");
        while(isInvalidBoardSizeBlackout(col)){
            System.out.println("Enter only 3-9 numbers");
            col = IR4.getInteger("Enter your value for the COLUMN of the board (3-9): ");
        }

        boardCol = col;

        board = new String[boardRow][boardCol];
        return board;
    }

    public static boolean isInvalidBoardSizeBlackout(int size){
        if(size < MIN_COL_ROW || size > MAX_COL_ROW){
            return true;
        }
        return false;
    }

    public static String [][] getBoardSizeClassic(){
        String [][] board;
        int choice;
        choice = IR4.getInteger("Choose your board size for Classic Mode\n 1:\t\t\t4x4 \n 2:\t\t\t5x5 \n 3:\t\t\t6x6 \n 4:\t\t\t7x7 \n 5:\t\t\t8x8 \n 6:\t\t\t9x9 \n 0:\t\t\tDefault 3x3");
        while(isInvalidBoardSizeClassic(choice)){
            System.out.println("Invalid choice!!!");
            System.out.println("Try again.");
            choice = IR4.getInteger("Choose your board size for Classic Mode\n 1:\t\t\t4x4 \n 2:\t\t\t5x5 \n 3:\t\t\t6x6 \n 4:\t\t\t7x7 \n 5:\t\t\t8x8 \n 6:\t\t\t9x9 \n 0:\t\t\tDefault 3x3");
        }

        if(choice == 1){
            boardRow = 4;
            boardCol = 4;
        }
        else if(choice == 2){
            boardRow = 5;
            boardCol = 5;
        }
        else if(choice == 3){
            boardRow = 6;
            boardCol = 6;
        }
        else if(choice == 4){
            boardRow = 7;
            boardCol = 7;
        }
        else if(choice == 5){
            boardRow = 8;
            boardCol = 8;
        }
        else if(choice == 6){
            boardRow = 9;
            boardCol = 9;
        }
        else{
            boardRow = 3;
            boardCol = 3;
        }
        board = new String[boardRow][boardCol];

        return board;
    }

    public static boolean isInvalidBoardSizeClassic(int choice){        
        if(choice < 0 || choice > 6){
            return true;
        }
        else return false;

    }

    public static int displaySecondMenu(){
        displayWelcome();
        int result = IR4.getInteger("1: \t\t\t Display the Rules \n2: \t\t\t Change Player's Character \n3: \t\t\t Change the Board size \n4: \t\t\t Start Game \n5: \t\t\t Exit");

        while(result < 1 || result > 5){
            System.out.println("Wrong choice!!!");
            System.out.print("Please, try again: \n");
            result = IR4.getInteger("1: \t\t\t Display the Rules \n2: \t\t\t Change Player's Character \n3: \t\t\t Change the Board size \n4: \t\t\t Start Game \n5: \t\t\t Exit");
        }

        return result;
    }

    public static void displayBoardSizeAndPlayers(int row, int col, String player1, String player2){
        System.out.println("Player 1: "+player1 +"\t\t Player 2: " + player2);
        System.out.println("The board size is "+row+"x"+col);
    }

    /**
     * playMatch(String [][] board) - this method controls the playGame()
     *
     * @param  board  this is a String 2D array
     * @return 1 return 1 if X won the match
     * @return 2 return 2 if O won the match
     * @return 3 return 3 when all games have been played and nobody won
     */
    public static int playMatch(String [][] board, boolean isClassic){
        int gameNbr;
        int result;
        double firstScore, secondScore;
        firstScore = 0;
        secondScore = 0;
        if(isClassic){
            for(gameNbr = 1; gameNbr < NBR_OF_GAMES; gameNbr++){
                System.out.println("----- Game number "+gameNbr +"  -----");
                //result of: 1=X won, 2=O won, 3=tie
                displayBoardSizeAndPlayers(boardRow, boardCol, player1Char, player2Char);
                result = playGame(board, isClassic);
                displayBoard(board);

                if(result == FIRST_PLAYER_WINS){
                    firstScore++;
                    System.out.println(player1Char+" won this game!");
                }
                else if(result == SECOND_PLAYER_WINS){
                    secondScore++;
                    System.out.println(player2Char+" won this game!");
                }
                else{
                    firstScore = firstScore + 0.5;
                    secondScore = secondScore + 0.5;
                    System.out.println("Nobody won this game. Tie!");
                }

                System.out.println("The score is: "+firstScore + "-" + secondScore);

                if(firstScore > (NBR_OF_GAMES * 0.5)){
                    //X won the match
                    return FIRST_PLAYER_WINS; 
                }

                if(secondScore > (NBR_OF_GAMES * 0.5)){
                    //O won the match
                    return SECOND_PLAYER_WINS; 
                }
            }

            return TIE;

        }

        else{
            for(gameNbr = 1; gameNbr < NBR_OF_GAMES; gameNbr++){
                System.out.println("----- Game number "+gameNbr +"  -----");
                //result of: 1=X won, 2=O won, 3=tie
                displayBoardSizeAndPlayers(boardRow, boardCol, player1Char, player2Char);
                playGame(board, isClassic);
                displayBoard(board);

                if(player1TotalScore >player2TotalScore){
                    firstScore++;
                    System.out.println(player1Char+" won this game!");
                }
                else if(player1TotalScore < player2TotalScore){
                    secondScore++;
                    System.out.println(player2Char+" won this game!");
                }
                else{
                    firstScore = firstScore + 0.5;
                    secondScore = secondScore + 0.5;
                    System.out.println("Nobody won this game. Tie!");
                }

                System.out.println("The score is: "+firstScore + "-" + secondScore);

                if(firstScore > (NBR_OF_GAMES * 0.5)){
                    //X won the match
                    return FIRST_PLAYER_WINS; 
                }

                if(secondScore > (NBR_OF_GAMES * 0.5)){
                    //O won the match
                    return SECOND_PLAYER_WINS; 
                }
            }

            return TIE;
        }
    }

    public static void getPlayer(){
        System.out.println("Player 1, ");
        String newValue1 = IR4.getString("You can choose only characters A-Z and a-z");
        while(isInValidPlayer(newValue1,1)){
            newValue1 = IR4.getString("Try agan!");
        }

        player1Char = newValue1;

        System.out.println("Player 2, ");
        String newValue2 = IR4.getString("You can choose only characters A-Z and a-z");
        while(isInValidPlayer(newValue2,2)){
            newValue2 = IR4.getString("Try agan!");
        }

        player2Char = newValue2;
    }

    public static boolean isInValidPlayer(String player, int order){
        if(order == 1){
            int playerChar = player.charAt(0);

            if(player.length() > 1){
                System.out.println("Enter only a single character");
                return true;
            }

            if ((playerChar < 65 || player.charAt(0) > 90) && (playerChar < 97 || playerChar > 122)){
                System.out.println("You can choose only characters A-Z and a-z");
                return true;
            }
            return false;

        }

        int playerChar = player.charAt(0);
        if(player.equals(player1Char)){
            System.out.println("You can't enter same character again!");
            return true;
        }

        if(player.length() > 1){
            System.out.println("Enter only a single character");
            return true;
        }

        if ((playerChar < 65 || player.charAt(0) > 90) && (playerChar < 97 || playerChar > 122)){
            System.out.println("You can choose only characters A-Z and a-z");
            return true;
        }
        return false;
    }

    public static void displayRules(boolean choice){
        if(choice){
            System.out.println("");
            System.out.println("Each player will take turns putting a mark on the board.");
            System.out.println("Players will enter row and column like this: 12 or 23.");
            System.out.println("A player will win when they get streak(equal to row of the board) of their marks in a row.");
            System.out.println("If the board is filled without streak in a row, the game is a tie.");
            System.out.println("");
            System.out.println("The best of 3 games is the winner! Good luck!");
        }
        else{
            System.out.println();
            System.out.println("The players play until the board is full.");
            System.out.println("The goal is to earn more points than the other player.");
            System.out.println("Points are earned by making streaks in rows, columns, or diagonally.");
            System.out.println("Points are defined as:");
            System.out.println("9 in a row = 25 points\n8 in a row = 20 points\n7 in a row = 15 points\n6 in a row = 12 points\n5 in a row = 10 points\n4 in a row = 5 points\n3 in a row = 3 points\n2 in a row = 1 point");
        }
    }

    public static void displayWelcome(){
        System.out.println("*************************************************************");
        System.out.println("Welcome to the Amazing game of Best-of-Streak Tic-Tac-Toe!");
        System.out.println("*************************************************************");
    }

    public static void displayFinalResults(int result){
        if(result == FIRST_PLAYER_WINS){
            System.out.println("Player 1 ("+player1Char+") won the match!");
        }
        else if(result == SECOND_PLAYER_WINS){
            System.out.println("Player 2 ("+player2Char+") won the match!");
        }
        else{
            System.out.println("The match is a tie!");
        }
    }

    public static String [][] initializeBoard(String [][] board){
        int r, c;
        for(r=0; r<boardRow; r++){
            for(c=0; c<boardCol; c++){
                board[r][c] = " ";
            }
        }

        return board;  
    }

    public static void displayBoard(String [][] theBoard){
        System.out.println();
        System.out.print(" ");
        for(int i = 1; i<=boardCol; i++){
            System.out.printf("%4s",i);
        }
        System.out.println();
        for(int r = 0; r<boardRow; r++){
            System.out.print((r+1)+":");
            for(int c= 0; c<boardCol; c++){
                if(c == boardCol-1){
                    System.out.printf("%2s",theBoard[r][c]);
                    continue; 
                }
                else{
                    if(c > 0){
                        System.out.printf("%2s |",theBoard[r][c]);
                    }
                    else System.out.printf("%2s |",theBoard[r][c]);
                }
            }
            if(r == boardRow-1){
                System.out.println();
                continue;
            }
            System.out.println();
            System.out.print("  ");
            for(int c= 0; c<boardCol; c++){
                if(c == boardCol-1){
                    System.out.print("---");
                }
                else{
                    System.out.print("---+");
                }
            }
            System.out.println();
        }

        System.out.println();
    }

    public static int getPlayersMove (String msg, String [][] board){
        int newValue;
        newValue = IR4.getInteger(msg);

        while(isInvalid(newValue, board)){
            newValue = IR4.getInteger(msg);
        }

        return newValue;
    }

    public static boolean isInvalid (int newValue, String [][] board){
        if(newValue > 11 || newValue < ((boardRow)*10 + (boardCol)) || newValue == 9999){

            return false;
        }else System.out.println("Your move must be in 11 through "+((boardRow)*10 + (boardCol))+" format");;

        if(((newValue%10) > 1)||((newValue%10)<boardCol) || newValue != 9999){

            return false;
        }else System.out.println("Column values must be 1-"+(boardCol));

        if ((board [(newValue / 10)-1] [(newValue %10)-1] != " ")){
            System.out.println("That space is already taken!");
            return true;
        }

        return true;
    }

    public static int playGame(String [][] theBoard, boolean isClassic){
        int move;
        int result = 0;
        initializeBoard(theBoard);
        if(isClassic) {    
            do{
                displayBoard(theBoard);
                move = getPlayersMove("What is your move, "+player1Char+"?", theBoard);

                theBoard [(move/10)-1][(move%10)-1] = player1Char;
                result = getPlayerScoreClassic(player1Char, theBoard);

                if(result == 0){
                    displayBoard (theBoard);
                    move = getPlayersMove("What is your move, "+player2Char+"?", theBoard);
                    theBoard [(move / 10)-1 ][(move%10) -1] = player2Char;
                    result = getPlayerScoreClassic(player2Char, theBoard);
                    if(result == 1) result = 2;
                }
            }while(result == 0);
            return result;
        }else {
            do{

                displayBoard(theBoard);
                move = getPlayersMove("What is your move, "+player1Char+"?", theBoard);
                if(move == 9999){
                    theBoard = randomlyFill(theBoard);
                }
                else theBoard [(move/10)-1][(move%10)-1] = player1Char;

                if(!isBoardFull(theBoard)){
                    displayBoard (theBoard);
                    move = getPlayersMove("What is your move, "+player2Char+"?", theBoard);
                    theBoard [(move / 10)-1 ][(move%10) -1] = player2Char;
                }

            }while(!isBoardFull(theBoard));

            player1ScoreCount = getPlayerScoreBlackout(player1Char, theBoard);
            player2ScoreCount = getPlayerScoreBlackout(player2Char, theBoard);

            result = displayGameResults(player1ScoreCount,player2ScoreCount);
            return result;
        }
    }

    public static String[][] randomlyFill(String [][] board){
        String[] random = {player1Char,player2Char};
        for(int r=0; r<board.length; r++){
            for(int c=0; c< board[r].length; c++){
                board[r][c] = random[IR4.getRandomNumber(0,1)];
            }
        }

        return board;
    }

    public static int displayGameResults(int[] firstPlayer, int[] secondPlayer){
        System.out.println("\t\t\tPlayer "+player1Char +"     Player"+player2Char);
        System.out.println("\t\t\tNbr   Pts     Nbr   Pts");
        int[] points = {0,0,1,3,5,10,12,15,20,25};
        for(int i = 2; i<firstPlayer.length; i++){
            System.out.print(i+" in a row (x1 ):  ");
            System.out.printf("%7d    %3d     %3d    %2d",firstPlayer[i],firstPlayer[i]*points[i], secondPlayer[i],secondPlayer[i]*points[i]);
            System.out.println();
        }
        System.out.printf("%35s %13s \n","----","----");
        player1TotalScore = firstPlayer[2]*1+firstPlayer[3]*3+firstPlayer[4]*5+firstPlayer[5]*10+firstPlayer[6]*12+firstPlayer[7]*15+firstPlayer[8]*20+firstPlayer[9]*25;
        player2TotalScore = secondPlayer[2]*1+secondPlayer[3]*3+secondPlayer[4]*5+secondPlayer[5]*10+secondPlayer[6]*12+secondPlayer[7]*15+secondPlayer[8]*20+secondPlayer[9]*25;

        System.out.print("     Total:");
        System.out.printf("%22d %13d \n",player1TotalScore,player2TotalScore );
        if(player1TotalScore > player2TotalScore){
            return FIRST_PLAYER_WINS;
        }
        else if(player2TotalScore > player1TotalScore){
            return SECOND_PLAYER_WINS;
        }
        else return TIE;
    }

    public static int getPlayerScoreClassic(String playerChar, String [][] board){

        int streak = 0;
        int[] result = new int[10];
        for(int r = 0; r<board.length; r++){
            for(int c = 0; c<board[r].length; c++){
                // checking bottom 
                if(r == 0){
                    streak = checkForBottom(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(r>0 && !board[r-1][c].equals(playerChar)){
                    streak = checkForBottom(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                // checking right
                if(c == 0) {
                    streak = checkForRight(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(c>0 && !board[r][c - 1].equals(playerChar)){
                    streak = checkForRight(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                //checking diagonal right

                if(r == 0) {
                    streak = checkForBottomRight(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(r>0 && c == 0){
                    streak = checkForBottomRight(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(r>0 && c>0 && !board[r-1][c-1].equals(playerChar)){
                    streak = checkForBottomRight(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                //checking diagonal left

                if(r == 0) {
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(r>0 && c == board[r].length-1){
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }

                if(r>0 && c< board[r].length-1 && !board[r-1][c+1].equals(playerChar)){
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    result[streak]++;
                    streak = 0;
                }
            }
        }

        if(result[board.length] == 1){
            return 1;
        }

        if(isBoardFull(board)){
            return 3;
        }

        return 0;
    }

    public static int[] getPlayerScoreBlackout(String playerChar, String [][] board){
        int streak = 0;
        int[] total = new int[10];

        for(int r = 0; r<board.length; r++){
            for(int c = 0; c< board[r].length; c++){
                if(r == 0){
                    streak = checkForBottom(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(r> 0 && !board[r-1][c].equals(playerChar)){
                    streak = checkForBottom(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                // checking right
                if(c == 0) {
                    streak = checkForRight(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(c > 0 && !board[r][c - 1].equals(playerChar)){
                    streak = checkForRight(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                //checking diagonal right

                if(r == 0) {
                    streak = checkForBottomRight(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(r>0 && c == 0){
                    streak = checkForBottomRight(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(r>0 && c>0 && !board[r-1][c-1].equals(playerChar)){
                    streak = checkForBottomRight(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                //checking diagonal left

                if(r == 0) {
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(r>0 && c == board[r].length-1){
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }

                if(r>0 && c< board[r].length-1 && !board[r-1][c+1].equals(playerChar)){
                    streak = checkForBottomLeft(r, c, playerChar, board);
                    total[streak]++;
                    streak = 0;
                }
            }
        }

        // if(playerChar.startsWith(player1Char)){
        // player1ScoreCount = total;
        // }
        // else{
        // player2ScoreCount = total;
        // }

        return total;

    }

    public static int checkForRight (int row, int col, String player, String[][] board){
        int tally = 0;
        for(int c = col; c<board[row].length; c++){
            if(board[row][c].equals(player)){
                tally++;
            }
            else{
                return tally;
            }
        }
        return tally;
    }

    public static int checkForBottomRight (int row, int col, String player, String[][] board){
        int tally = 0;

        for(int r = row; r<board.length; r++){
            if(board[r][col].equals(player)){
                tally++;
                col++;
            }else return tally;

            if(col >= board[row].length){
                return tally;
            }
        }
        return tally;
    }

    public static int checkForBottom (int row, int col, String player, String[][] board){
        int tally = 0;
        for(int r = row; r<board.length; r++){
            if(board[r][col].equals(player)){
                tally++;
            }
            else{
                return tally;
            }
        }
        return tally;
    }

    public static int checkForBottomLeft (int row, int col, String player, String[][] board){
        int tally = 0;

        for(int r = row; r<board.length; r++){
            if(board[r][col].equals(player)){
                tally++;
                col--;
            }
            else return tally;

            if(col<0){
                return tally;
            }
        }
        return tally;
    }

    public static boolean isBoardFull(String [][] theBoard){
        int r, c;
        for(r=0; r<boardRow; r++){
            for(c=0; c<boardCol; c++){
                if(theBoard [r][c].equals(" ")){
                    return false;
                }
            }
        }
        return true;
    }
}
