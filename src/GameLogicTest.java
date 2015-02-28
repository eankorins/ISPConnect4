import java.util.Random;

public class GameLogicTest implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int[][] gameBoard;
    public GameLogicTest() {
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

            }
        }
        return Winner.NOT_FINISHED;
    }


    public void insertCoin(int column, int playerID) {
        if(updateBoard(gameBoard, column, playerID)){
            System.out.println("column " + column + " playerID " + playerID);
        }
        if (playerID == this.playerID){

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
    private boolean isFull(int column){
        return gameBoard[column][y-1] != 0;
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


}
