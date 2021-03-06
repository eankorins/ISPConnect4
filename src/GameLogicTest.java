
import java.util.*;

public class GameLogicTest implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int opponent;
    private int cutoff;
    private int turns;
    private int[][] gameBoard;
    public GameLogicTest() {
        //TODO Write your implementation for this method
    }

    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        this.opponent = playerID == 1 ? 2 : 1;
        this.cutoff = 7;
        gameBoard = new int[x][y];
        //TODO Write your implementation for this method
    }
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
            //System.out.println("column " + column + " playerID " + playerID);
            //printBoard(gameBoard);
        }
        //TODO Write your implementation for this method
    }

    public int decideNextMove() {
        if(playerID == 1 && turns == 0){
            return x/2;
        }
        Timer timer = new Timer();
        timer.play();
        List<Action> actions = new ArrayList<Action>();
        List<Integer> availableActions =  availableActions(gameBoard);
        for(Integer action : availableActions){
            int[][] newState = result(gameBoard, action, playerID);
            actions.add(new Action(action, minValue(newState, action, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)));
        }
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
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            return utility(winner);
        }
        if(d == cutoff ){
            return eval(state, playerID) - eval(state, opponent);
        } else {
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
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            return utility(winner);
        }
        if(d == cutoff ){
            return eval(state, playerID) - eval(state, opponent);
        } else {
            for(Integer action : availableActions(state)){
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

        for(int col = 0; col < x; col++){
            countVertical(state, scoringMatrix, col, playerID);
        }
        for(int row = 0; row < y; row++) {
            countHorizontal(state, scoringMatrix, row, playerID);
        }
//        System.out.println("Eval score for player " + player + " is: " + total + " for board:");
//        printBoard(state);
        //System.out.println("Score: " + total + " For " + player);
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                score += scoringMatrix[col][row];
            }
        }
        return score;
    }

    private Winner fourConnected(int[][] board, int col, int row){
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
    private int countDiagonalRight(int[][] state, int[][] scoringMatrix, int col, int player){
        int score = 1;
        int counter = 0;
        int currentScore = 0;
        for(int row = 0; row < y; row++){
            int cellValue = state[col][row];
            if(cellValue == player){
                counter++;
                score *= 2;
                currentScore += score;
                scoringMatrix[col][row] += score;
            }
            else if(cellValue == 0){
                if(counter < 5) {
                    score *= 2;
                    currentScore += score;
                    scoringMatrix[col][row] += score;
                }
            }
            else{
                if(counter < 4){
                    scoringMatrix[col][row] -= currentScore;
                }
                break;
            }

        }
        return currentScore;
    }
    private int countDiagonalLeft(int[][] state, int[][] scoringMatrix, int col, int player){
        int score = 1;
        int counter = 0;
        int currentScore = 0;
        for(int row = 0; row < y; row++){
            int cellValue = state[col][row];
            if(cellValue == player){
                counter++;
                score *= 2;
                currentScore += score;
                scoringMatrix[col][row] += score;
            }
            else if(cellValue == 0){
                if(counter < 5) {
                    score *= 2;
                    currentScore += score;
                    scoringMatrix[col][row] += score;
                }
            }
            else{
                if(counter < 4){
                    scoringMatrix[col][row] -= currentScore;
                }
                break;
            }

        }
        return currentScore;
    }
    private int countVertical(int[][] state, int[][] scoringMatrix, int col, int player){
        int score = 1;
        int counter = 0;
        int currentScore = 0;
        for(int row = 0; row < y; row++){
            int cellValue = state[col][row];
            if(cellValue == player){
                counter++;
                score *= 2;
                currentScore += score;
                scoringMatrix[col][row] += score;
            }
            else if(cellValue == 0){
                if(counter < 5) {
                    score *= 2;
                    currentScore += score;
                    scoringMatrix[col][row] += score;
                }
            }
            else{
                if(counter < 4){
                    scoringMatrix[col][row] -= currentScore;
                }
                break;
            }

        }
        return currentScore;
    }
    private int countHorizontal(int[][] state, int[][] scoringMatrix, int row, int player){
        int score = 1;
        int counter = 0;

        int currentScore = 0;
        for(int col = 0; col < x; col++){
            int cellValue = state[col][row];
            if(cellValue == player){
                counter++;
                score *= 2;
                currentScore += score;
                scoringMatrix[col][row] += score;
            }
            else if(cellValue == 0){
                if(counter < 5) {
                    score *= 2;
                    currentScore += score;
                    scoringMatrix[col][row] += score;
                }
            }
            else{
                if(counter < 4){
                    scoringMatrix[col][row] -= currentScore;
                }
                break;
            }

        }
        return currentScore;
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
            if(board[col][0] == 0){
                actions.add(col);
            }
        }
        return actions;
    }
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
    private void printBoard(int[][] board){
        for(int row = 0; row < y; row++){
            for(int col = 0; col < x; col++){
                System.out.print(board[col][row] + " ");
            }
            System.out.print("\n");
        }
    }

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
