
import java.util.*;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int opponent;
    private int cutoff;
    private int turns;
    private int[][] gameBoard;
    public GameLogic() {
        //TODO Write your implementation for this method
    }

    public void initializeGame(int x, int y, int playerID) {
        //Initalizes local gameboard as well as player/opponent IDs
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        this.opponent = playerID == 1 ? 2 : 1;
        this.cutoff = 8;
        gameBoard = new int[x][y];
        //TODO Write your implementation for this method
    }
    //Called by the GUI Class
    public Winner gameFinished() {
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(gameBoard[col][row] != 0){
                    Winner winner = fourConnected(gameBoard, col, row);
                    if(!winner.equals(Winner.NOT_FINISHED)){
                        //System.out.println( winner + " Wins on Col: " + col + " Row: " + row);
                        return winner;
                    }
                }
            }
        }

        return Winner.NOT_FINISHED;
    }

    //Used by minimax functions to determine if their current state is terminal.
    public Winner gameFinished(int[][] board) {
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(board[col][row] != 0){
                    Winner winner = fourConnected(board, col, row);
                    if(!winner.equals(Winner.NOT_FINISHED)){

                        //System.out.println( winner + " Wins on Col: " + col + " Row: " + row);
                        return winner;
                    }
                }
            }
        }

        return Winner.NOT_FINISHED;
    }

    public void insertCoin(int column, int playerID) {

        if(updateBoard(gameBoard, column, playerID)){
            turns++;
            //printBoard(scoringMatrix(gameBoard, playerID));
            //System.out.println("column " + column + " playerID " + playerID);
            //printBoard(gameBoard);
        }
        //TODO Write your implementation for this method
    }

    public int decideNextMove() {
        //Chooses middle column if it has the first move.
        if(playerID == 1 && turns == 0){
            return x/2;
        }
        Timer timer = new Timer();
        timer.play();

        //This GameLogic will always act as the Maximizing Player.
        List<Action> actions = new ArrayList<Action>();
        //All Availble actions for the current game state.
        List<Integer> availableActions =  availableActions(gameBoard);
        //Gets the possible score for every possible action
        for(Integer action : availableActions){
            int[][] newState = result(gameBoard, action, playerID);
            actions.add(new Action(action, minValue(newState, action, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        }
        //Finds max score for the possible actions.
        Action bestAction = new Action(availableActions.get(0), Integer.MIN_VALUE);
        for(Action action : actions){
            if(action.getScore() > bestAction.getScore()){
                bestAction = action;
            }
        }
        System.out.println("Choosing column: " + bestAction.getAction() + " with score: " + bestAction.getScore());
        System.out.println("Decision took: " + timer.check());
        return bestAction.getAction();
    }
    private int maxValue(int[][] state, int a, int d, int alpha, int beta){
        //Checks for terminal game state
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            return utility(winner);
        }
        //Returns evaluation score when at cutoff depth
        if(d == cutoff ){
            return eval(state, playerID) - eval(state, opponent);
        } else {
            //Iterates all remaining for the GameLogic's player
            for(Integer action : availableActions(state)){
                int[][] newState = result(state, action, playerID);
                int score = minValue(newState, action, d + 1, alpha, beta);
                if(score > alpha){
                    alpha = score;
                }
                if(alpha >= beta){
                    break;
                }
            }
            return alpha;
        }
    }

    private int minValue(int[][] state, int a, int d, int alpha, int beta){
        //Checks for terminal game state
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            return utility(winner);
        }
        //Returns evaluation score when at cutoff depth
        if(d == cutoff ){
            return eval(state, playerID) - eval(state, opponent);
        } else {
            for(Integer action : availableActions(state)){
                //Iterates all remaining for the GameLogic's opponent
                int[][] newState = result(state, action, opponent);
                int score = maxValue(newState, action, d + 1, alpha, beta);
                if(score < beta){
                    beta = score;
                }
                if(alpha >= beta){
                    break;
                }
            }
            //System.out.println(bestAction);
            return beta;

        }
    }
    private int utility(Winner winner){
        //Returns max integer if the GameLogic's Player wins or min integer if it's opponent wins
        if(winner.equals(Winner.PLAYER1)) {
            if (playerID == 1) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }
        else if(winner.equals(Winner.PLAYER2)){
            if(playerID == 2){
                return Integer.MAX_VALUE;
            }
            else{
                return Integer.MIN_VALUE;
            }
        }
        else{
            return 0;
        }

    }
    private int eval(int[][] state, int playerID){
        int[][] scoringMatrix = new int[x][y];
        int score = 0;
        //Scores every Column
        for(int col = 0; col < x; col++){
            countVertical(state, scoringMatrix, col, playerID);
        }
        //Scores every Row
        for(int row = 0; row < y; row++) {
            countHorizontal(state, scoringMatrix, row, playerID);
        }

        //For collumn [0] && [x-1] y-4 iterations
        //Counts right diagonal scores for rows y-4 on Column 0 (First Column)
        //Counts left diagonal scores for rows y-4 on Column x-1 (Last Column)
        for(int row = y - 1; row > y-4; row--){
            countDiagonalRight(state,scoringMatrix, 0, row, playerID);
            countDiagonalLeft(state, scoringMatrix, x-1, row, playerID);
        }
        //Counts remaining right diagonals from row y - 1 (Bottom row) untill Column x - 4
        for(int col = 1; col < x - 4; col++){
            countDiagonalRight(state,scoringMatrix, col, y - 1, playerID);
        }
        //Counts remaining left diagonals from row y - 1 (Bottom row) untill Column x - 4
        for(int col = x-1; col > 3; col--){
            countDiagonalLeft(state, scoringMatrix, col, y - 1, playerID);
        }

        //Sums the final scoring matrix
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                score += scoringMatrix[col][row];
            }
        }
        return score;
    }

    //Same as eval function, but returns the actual scoring matrix of the displayed game state for printing to console.
    private int[][] scoringMatrix(int[][] state, int playerID){
        int[][] scoringMatrix = new int[x][y];

        int score = 0;

        for(int col = 0; col < x; col++){
            countVertical(state, scoringMatrix, col, playerID);
        }
        for(int row = 0; row < y; row++) {
            countHorizontal(state, scoringMatrix, row, playerID);
        }

        //For collumn [0] && [x-1] y-4 iterations
        for(int row = y - 1; row > y-4; row--){
            countDiagonalRight(state,scoringMatrix, 0, row, playerID);
            countDiagonalLeft(state, scoringMatrix, x-1, row, playerID);
        }
        for(int col = 1; col < x - 4; col++){
            countDiagonalRight(state,scoringMatrix, col, y - 1, playerID);
        }
        for(int col = x-1; col > 3; col--){
            countDiagonalLeft(state, scoringMatrix, col, y - 1, playerID);
        }
        //Remaining Column Diagonals from row y
//        System.out.println("Eval score for player " + player + " is: " + total + " for board:");
//        printBoard(state);
        //System.out.println("Score: " + total + " For " + player);
        return scoringMatrix;
    }
    //Checks if any 4 coins are connected
    private Winner fourConnected(int[][] board, int col, int row){
        //Vertical Check
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
        //Horizontal Check
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
        //Right Diagonal Check
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
        //Left Diagonal Check
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
    private int countDiagonalRight(int[][] state, int[][] scoringMatrix, int col, int row, int player){
        int score = 0;
        int currentRow = row;
        int currentColumn = col;
        int distance = 0;
        int connected = 0;

        //Loop untill it hits the top row of the board
        while(currentRow >= 0) {
            if(currentColumn > x - 1){
                break;
            }
            int cellValue = state[currentColumn][currentRow];
            if (cellValue == 0) {
                distance++;
                if(distance == 3){
                    distance = 0;
                    connected = 0;
                }
            }
            //If the value is the current player
            if (cellValue == player) {
                if(distance > 1){
                    scoringMatrix[col][row] += 8 / (int)Math.pow(2.0, distance);
                }
                connected++;
                if(connected > 0){
                    scoringMatrix[col][row] += Math.pow(2.0, connected);
                }
                distance = 0;
            }
            else{
                connected = 0;
                distance = 0;
            }
            //Move one row up the board and a column to the right
            currentColumn++;
            currentRow--;
        }

        return score;
    }
    private int countDiagonalLeft(int[][] state, int[][] scoringMatrix, int col, int row, int player){
        int score = 0;
        int currentRow = row;
        int currentColumn = col;
        int distance = 0;
        int connected = 0;

        //Loop till it hits the top row of the board
        while(currentRow >= 0) {
            //Or it hits the left most column
            if(currentColumn <= 0){
                break;
            }
            int cellValue = state[currentColumn][currentRow];
            if (cellValue == 0) {
                distance++;
                if(distance == 3){
                    distance = 0;
                    connected = 0;
                }
            }
            if (cellValue == player) {
                if(distance > 1){
                    scoringMatrix[col][row] += 8 / (int)Math.pow(2.0, distance);
                }
                connected++;
                if(connected > 0){
                    scoringMatrix[col][row] += Math.pow(2.0, connected);
                }
                distance = 0;
            }
            else{
                connected = 0;
                distance = 0;
            }
            //Move one row up the board and a column to the left
            currentColumn--;
            currentRow--;
        }

        return score;
    }
    private int countVertical(int[][] state, int[][] scoringMatrix, int col, int player){
        int score = 0;
        int distance = 0;
        int connected = 0;


        for(int row = 0; row < y; row++) {
            int cellValue = state[col][row];
            if (cellValue == 0) {
                distance++;
                if(distance == 3){
                    distance = 0;
                    connected = 0;
                }
            }
            if (cellValue == player) {
                if(distance > 1){
                    scoringMatrix[col][row] += 8 / (int)Math.pow(2.0, distance);
                }
                connected++;
                if(connected > 0){
                    scoringMatrix[col][row] += Math.pow(2.0, connected);
                }
                distance = 0;
            }
            else{
                connected = 0;
                distance = 0;
            }
        }
        return score;
    }
    private int countHorizontal(int[][] state, int[][] scoringMatrix, int row, int player){
        int score = 0;
        //Represents distance from wall to player, or distance between two coins by the same player
        int distance = 0;
        //Represents Connected coins
        int connected = 0;

        //For every column
        for(int col = 0; col < y; col++) {
            int cellValue = state[col][row];
            //If the cell is empty
            if (cellValue == 0) {
                //Increment Distance
                distance++;
                connected = 0;
                //If the distance is 3 then reset max
                if(distance > 2){
                    distance = 0;
                }
            }
            //If cell is occupied by player
            if (cellValue == player) {
                //And there is a valid distance from wall or between 2 coins
                if(distance > 1){
                    //Add 8 / 2 to the power of the distance (4 or 2) points to the scoring matrix
                    scoringMatrix[col][row] += 8 / (int)Math.pow(2.0, distance);
                }

                connected++;
                //For connected pieces
                if(connected > 0){

                    scoringMatrix[col][row] += Math.pow(2.0, connected);
                }
                distance = 0;
            }
            else{
                connected = 0;
                distance = 0;
            }

        }
        return score;
    }

    private boolean isFull(int column){
        return gameBoard[column][0] != 0;
    }

    //Update board function from the GUI class for local use.
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

    //Returns a list of integers representing indexes of columns that are still open
    private List<Integer> availableActions(int[][] board){
        List<Integer> actions = new ArrayList<Integer>();
        for(int col = 0; col < board.length; col++){
            if(board[col][0] == 0){
                actions.add(col);
            }
        }
        return actions;
    }

    //Returns a new state with the an action taken by a player.
    private int[][] result(int[][] state, int column, int player){
        int[][] newState = new int[x][y];
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                newState[col][row] = state[col][row];
            }
        }
        updateBoard(newState, column, player);
        return newState;
    }

    //Prints a board into console.
    private void printBoard(int[][] board){
        for(int row = 0; row < y; row++){
            for(int col = 0; col < x; col++){
                System.out.print(board[col][row] + " ");
            }
            System.out.print("\n");
        }
    }


    //Action class used to determine the best action in decideNextMove function.
    private class Action{
        private int action;
        private Integer score;

        public Action(int action, int score){
            this.score = score;
            this.action = action;
        }

        public Integer getScore(){
            return score;
        }
        public int getAction(){
            return action;
        }

    }
}
