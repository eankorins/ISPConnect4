
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
        //TODO Write your implementation for this method
        return Winner.NOT_FINISHED;
    }


    public void insertCoin(int column, int playerID) {

        if(updateBoard(column, playerID)){
            System.out.println("column " + column + " playerID " + playerID);
        }
        //TODO Write your implementation for this method	
    }

    public int decideNextMove() {
        //TODO Write your implementation for this method
        return 0;
    }
    private boolean updateBoard(int col, int player){
        if(col == -1) {
            return false;
        }
        if (gameBoard[col][0] != 0) {
            return false;
        }
        int r = gameBoard[col].length-1;
        while(gameBoard[col][r]!=0) r--;
        gameBoard[col][r]=player;
        return true;
    }
}
