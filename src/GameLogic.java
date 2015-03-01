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
                    Winner winner = diagonalCheck(board, col, row);
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
            System.out.println("column " + column + " playerID " + playerID);
            printBoard(gameBoard);
        }
        //TODO Write your implementation for this method
    }

    public int decideNextMove() {

        List<Action> actions = new ArrayList<Action>();
        List<Integer> availableActions =  availableActions(gameBoard);
        for(Integer action : availableActions){
            int[][] newState = result(gameBoard, action, playerID);
            actions.add(new Action(action, maxValue(newState, action, playerID, 0).getScore()));
        }
        Action bestAction = new Action(availableActions.get(0), Integer.MAX_VALUE);
        for(Action action : actions){
            if(action.getScore() < bestAction.getScore()){
                bestAction = action;
            }
        }
        System.out.println("Choosing column: " + bestAction.getAction() + " with score: " + bestAction.getScore());
        return bestAction.getAction();
    }



    private void printBoard(int[][] board){
        for(int row = 0; row < y; row++){
            for(int col = 0; col < x; col++){
                System.out.print(board[col][row] + " ");
            }
            System.out.print("\n");
        }
    }
    private Action minValue(int[][] state, int a, int player, int d){
        int opponent = player == 1 ? 2 : 1;
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            if(winner.equals(Winner.PLAYER1)){
                if(player == 1){
                    return new Action(a, -1000 / d);
                }
                else{
                    return new Action(a,1000/ d);
                }
            }
            else if(winner.equals(Winner.PLAYER2)){
                if(player == 2){
                    return new Action(a, -1000 / d);
                }
                else{
                    return new Action(a,1000 / d);
                }
            }
            else{
                return new Action(a, 0);
            }
        }
        if(d == cutoff ){
            int score = eval(state, player);
            return new Action(a, score);
        } else {
            List<Action> actions = new ArrayList<Action>();
            List<Integer> availableActions =  availableActions(state);
            for(Integer action : availableActions){
                int[][] newState = result(state, action, player);
                actions.add(new Action(action, maxValue(newState, action, opponent, d + 1).getScore()));
            }
            Action bestAction = new Action(availableActions.get(0), Integer.MAX_VALUE);
            for(Action action : actions){
                if(action.getScore() < bestAction.getScore()){
                    bestAction = action;
                }
            }
            //System.out.println(bestAction);
            return bestAction;

        }
    }

    private Action maxValue(int[][] state, int a, int player, int d){
        int opponent = player == 1 ? 2 : 1;
        Winner winner = gameFinished(state);
        if(!winner.equals(Winner.NOT_FINISHED)){
            if(winner.equals(Winner.PLAYER1)){
                if(player == 1){
                    return new Action(a, 1000 / d);
                }
                else{
                    return new Action(a,-1000/ d);
                }
            }
            else if(winner.equals(Winner.PLAYER2)){
                if(player == 2){
                    return new Action(a, 1000 / d);
                }
                else{
                    return new Action(a,-1000 / d);
                }
            }
            else{
                return new Action(a, 0);
            }
        }
        if(d == cutoff){
            return new Action(a, -eval(state, player));
        } else {
            List<Action> actions = new ArrayList<Action>();
            List<Integer> availableActions =  availableActions(state);
            for(Integer action : availableActions){
                int[][] newState = result(state, action, player);
                actions.add(new Action(action, minValue(newState, action, opponent, d + 1).getScore()));
            }
            Action bestAction = new Action(availableActions.get(0), Integer.MIN_VALUE);
            for(Action action : actions){
                if(action.getScore() > bestAction.getScore()){
                    bestAction = action;
                }
            }
            //System.out.println(bestAction);
            return bestAction;
        }
    }

    private int eval(int[][] state, int player){
        List<Integer> scores = new ArrayList<Integer>();
        for(int col = 0; col < x; col++){
            for(int row = 0; row < y; row++){
                if(state[col][row] == player){
                    scores.add(countVertical(state, col, row, player));
                    scores.add(countHorizontal(state, col, row, player));
                }
            }
        }
        int total = 0;
        for(Integer score : scores){
            total += score;
        }
//        System.out.println("Eval score for player " + player + " is: " + total + " for board:");
//        printBoard(state);
        System.out.println("Score: " + total + " For " + player);
        return total;
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
    private int countVertical(int[][] state, int col, int row, int player){
        int score = 1;
        if(state[col][row] == player){
            int next = row - 1;
            while(next >= 0){
                if(state[col][next] == player) {
                    score *= 2;
                }
                else{
                    score /= 2;
                }
                next--;
            }
        }
        return score;
    }
    private int countHorizontal(int[][] state, int col, int row, int player){
        int score = 1;
        if(state[col][row] == player){
            int next = col + 1;
            while(next < x){
                if(state[next][row] == player) {
                    score *= 2;
                }
                else{
                    score /= 2;
                }
                next++;
            }
        }
        return score;
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
