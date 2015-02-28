import java.util.*;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int[][] gameBoard;
    public GameLogic() {
        //TODO Write your implementation for this method
    }
	
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        gameBoard = new int[x][y];
        //TODO Write your implementation for this method
    }
    public Winner gameFinished() {
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(gameBoard[col][row] != 0){
                    Winner winner = diagonalCheck(gameBoard, col, row);
                    if(!winner.equals(Winner.NOT_FINISHED)){

                        System.out.println( winner + " Wins on Col: " + col + " Row: " + row);
                        return winner;
                    }
                }
            }
        }

        return Winner.NOT_FINISHED;
    }


    public void insertCoin(int column, int playerID) {

        if(updateBoard(gameBoard, column, playerID)){
            System.out.println("column " + column + " playerID " + playerID);
            printBoard();
        }
        //TODO Write your implementation for this method	
    }

    public int decideNextMove() {
        int col = 0;
        Random rand = new Random();
        while(isFull(col)) {
            col = rand.nextInt(x);
        }
        return col;
    }

    private Winner diagonalCheck(int[][] board, int col, int row){
        //Vertical
        if(row <= y - 4){
            int player = board[col][row];
            int counter = 0;
            for(int i = 0; i < 4; i++){
                if(board[col][row+i] == player){
                    counter++;
                }
                else
                    break;
            }
            if(counter == 4){
                return player == 1 ? Winner.PLAYER1 : Winner.PLAYER2;
            }
        }
        if(col <= x - 4){
            int player = board[col][row];
            int counter = 0;
            for(int i = 0; i < 4; i++){
                if(board[col+i][row] == player){
                    counter++;
                }
                else
                    break;
            }
            if(counter == 4){
                return player == 1 ? Winner.PLAYER1 : Winner.PLAYER2;
            }
        }
        //Right Check
        if(col <= x - 4 && row <= y - 4){
            int player = board[col][row];
            int counter = 0;
            for(int i = 0; i < 4; i++){
                if(board[col+i][row+i] == player){
                    counter++;
                }
                else
                    break;
            }
            if(counter == 4){
                return player == 1 ? Winner.PLAYER1 : Winner.PLAYER2;
            }
        }
        //Left Check
        if(col >= x - 4 && row <= y - 4){
            int player = board[col][row];
            int counter = 0;
            for(int i = 0; i < 4; i++){
                if(board[col-i][row+i] == player){
                    counter++;
                }
                else
                    break;
            }
            if(counter == 4){
                return player == 1 ? Winner.PLAYER1 : Winner.PLAYER2;
            }
        }
        return Winner.NOT_FINISHED;
    }

    private void printBoard(){
        for(int row = 0; row < y; row++){
            for(int col = 0; col < x; col++){
                System.out.print(gameBoard[col][row] + " ");
            }
            System.out.print("\n");
        }
    }
//    private int minValue(int[][] state, int playerID){
//        Winner winner = gameFinished();
//        int opponent = playerID == 1 ? 2 : 1;
//        if(!winner.equals(Winner.NOT_FINISHED)){
//            return utilityValue(state, winner);
//        }
//        else{
//            List<Integer> values = new ArrayList<Integer>();
//            for(Integer action : availableActions(state)){
//                updateBoard(state, action, opponent);
//                values.add(maxValue(state.clone(), opponent));
//            }
//            return Collections.max(values);
//        }
//    }
//    private int maxValue(int[][] state, int playerID){
//        Winner winner = gameFinished();
//        int opponent = playerID == 1 ? 2 : 1;
//        if(!winner.equals(Winner.NOT_FINISHED)){
//            return utilityValue(state, winner);
//        }
//        else{
//            List<Integer> values = new ArrayList<Integer>();
//            for(Integer action : availableActions(state)){
//                updateBoard(state, action, opponent);
//                values.add(minValue(state.clone(), opponent));
//            }
//            return Collections.min(values);
//        }
//    }
//    private int utilityValue(int[][] state, Winner winner){
//
//        if(winner.equals(Winner.PLAYER1)){
//            if(this.playerID == 1){
//                return 1;
//            }
//            else{
//                return -1;
//            }
//        }
//        else if(winner.equals(Winner.PLAYER2)){
//            if(this.playerID == 2){
//                return 1;
//            }
//            else{
//                return -1;
//            }
//        }
//        else{
//            return 0;
//        }
//
//
//
//    }

    private boolean isFull(int column){
        return gameBoard[column][0] != 0;
    }
    private boolean updateBoard(int[][] board, int col, int player){
        if(col == -1) {
            return false;
        }
        if (board[col][0] != 0) {
            return false;
        }
        int r = board[col].length-1;
        while(board[col][r]!=0) r--;
        board[col][r]=player;
        return true;
    }

    private List<Integer> availableActions(int[][] board){
        List<Integer> actions = new ArrayList<Integer>();
        for(int col = 0; col < board.length; col++){
            if(board[col][0] != 0){
                actions.add(col);
            }
        }
        return actions;
    }
}
