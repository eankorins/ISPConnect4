import javafx.util.Pair;

import java.util.*;

public class GameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    private int cutoff;
    private int[][] gameBoard;
    public GameLogic() {
        //TODO Write your implementation for this method
    }

    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        this.cutoff = 5;
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
    public Winner gameFinished(int[][] board) {
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(gameBoard[col][row] != 0){
                    Winner winner = diagonalCheck(board, col, row);
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
        List<Action> actions = new ArrayList<Action>();
        int opponent = playerID == 1 ? 2 : 1;
        int[][] clonedBoard = gameBoard.clone();
        for(Integer action : availableActions(clonedBoard)){
            updateBoard(clonedBoard, action, playerID);
            actions.add(new Action(action, minValue(clonedBoard.clone(), action, opponent, 0).getScore()));
        }
        Action bestAction = null;
        int bestScore = 0;
        for(Action action : actions){
            if(action.getScore() > bestScore){
                bestScore = action.getScore();
                bestAction = action;
            }
        }
        return bestAction.getAction();
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
    private Action minValue(int[][] state, int a, int playerID, int d){
        int opponent = playerID == 1 ? 2 : 1;
        if(d == cutoff){
            return new Action(a, eval(state, playerID));
        } else {
            List<Action> actions = new ArrayList<Action>();
            for(Integer action : availableActions(state)){
                updateBoard(state, action, playerID);
                actions.add(new Action(action, maxValue(state.clone(), action, opponent, d + 1).getScore()));
            }
            Action bestAction = null;
            int bestScore = 0;
            for(Action action : actions){
                if(action.getScore() < bestScore){
                    bestScore = action.getScore();
                    bestAction = action;
                }
            }
            return bestAction;

        }
    }

    private Action maxValue(int[][] state, int a, int playerID, int d){
        int opponent = playerID == 1 ? 2 : 1;
        if(d == cutoff){
            return new Action(a, eval(state, playerID));
        } else {
            List<Action> actions = new ArrayList<Action>();
            for(Integer action : availableActions(state)){
                updateBoard(state, action, playerID);
                actions.add(new Action(action, minValue(state.clone(), action, opponent, d + 1).getScore()));
            }
            Action bestAction = null;
            int bestScore = 0;
            for(Action action : actions){
                if(action.getScore() > bestScore){
                    bestScore = action.getScore();
                    bestAction = action;
                }
            }
            return bestAction;
        }
    }
    private int eval(int[][] state, int player){
        List<Integer> scores = new ArrayList<Integer>();
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(state[col][row] == player){
                    scores.add(countVertical(state, col, row, player));
                }
            }
        }
        int total = 0;
        for(Integer score : scores){
            total += score;
        }
        return total;
    }

    private int countVertical(int[][] state, int col, int row, int player){
        int score = 1;
        if(state[col][row] == player){
            int next = row + 1;
            while(next < y){
                if(state[col][next] == player){
                    score *= 2;
                }
                else{
                    score = 0;
                }
            }
        }
        return score;
    }
    private int countHorizontal(int[][] state, int col, int row, int player){

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
