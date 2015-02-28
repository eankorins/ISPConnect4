import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        return Winner.NOT_FINISHED;
    }


    public void insertCoin(int column, int playerID) {

        if(updateBoard(gameBoard, column, playerID)){
            System.out.println("column " + column + " playerID " + playerID);
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

    private int minValue(int[][] state){
        for(Integer action : availableActions(state)){
            
        }
        return 0;
    }
    private int maxValue(int[][] state){

        return 0;
    }

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
