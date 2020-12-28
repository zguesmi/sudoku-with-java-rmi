import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Sudoku extends UnicastRemoteObject implements ISudoku {

	private int[][] solutionBoard;
	private int[][] clientBoard;
	private boolean[][] validPosition;

	public Sudoku() throws RemoteException {
		super();
		validPosition = new boolean[9][9];
	}

	public void generateSudokuBoard() throws RemoteException {
		solutionBoard = generateSolution(new int[9][9], 0);
		clientBoard = generateGame(copy(solutionBoard));
		generateValidPositions(validPosition);
	}

	/**
	 * Generates Sudoku game solution.
	 *
	 * @param game      Game to fill, user should pass 'new int[9][9]'.
	 * @param index     Current index, user should pass 0.
	 * @return          Sudoku game solution.
	 */
	private int[][] generateSolution(int[][] game, int index) {
			if (index > 80)
					return game;

			int x = index % 9;
			int y = index / 9;

			List<Integer> numbers = new ArrayList<Integer>();
			for (int i = 1; i <= 9; i++) numbers.add(i);
			Collections.shuffle(numbers);

			while (numbers.size() > 0) {
					int number = getNextPossibleNumber(game, x, y, numbers);
					if (number == -1)
							return null;
					game[y][x] = number;
					int[][] tmpGame = generateSolution(game, index + 1);
					if (tmpGame != null)
							return tmpGame;
					game[y][x] = 0;
			}
			return null;
	}
	/**
	 * Generates Sudoku game from solution.
	 *
	 * @param game      Game to be generated, user should pass a solution.
	 * @return          Generated Sudoku game.
	 */
	private int[][] generateGame(int[][] game) {
			List<Integer> positions = new ArrayList<Integer>();
			for (int i = 0; i < 81; i++)
					positions.add(i);
			Collections.shuffle(positions);
			return generateGame(game, positions);
	}
	/**
	 * Generates Sudoku game from solution, user should use the other
	 * generateGame method. This method simple removes a number at a position.
	 * If the game isn't anymore valid after this action, the game will be
	 * brought back to previous state.
	 *
	 * @param game          Game to be generated.
	 * @param positions     List of remaining positions to clear.
	 * @return              Generated Sudoku game.
	 */
	private int[][] generateGame(int[][] game, List<Integer> positions) {
			while (positions.size() > 0) {
					int position = positions.remove(0);
					int x = position % 9;
					int y = position / 9;
					int temp = game[y][x];
					game[y][x] = 0;
					if (!isValid(game))
							game[y][x] = temp;
			}
			return game;
	}
	/**
	 * Checks whether given game is valid.
	 *
	 * @param game      Game to check.
	 * @return          True if game is valid, false otherwise.
	 */
	private boolean isValid(int[][] game) {
			return isValid(game, 0, new int[] { 0 });
	}
	/**
	 * Checks whether given game is valid, user should use the other isValid
	 * method. There may only be one solution.
	 *
	 * @param game                  Game to check.
	 * @param index                 Current index to check.
	 * @param numberOfSolutions     Number of found solutions. Int[] instead of
	 *                              int because of pass by reference.
	 * @return                      True if game is valid, false otherwise.
	 */
	private boolean isValid(int[][] game, int index, int[] numberOfSolutions) {
			if (index > 80)
					return ++numberOfSolutions[0] == 1;

			int x = index % 9;
			int y = index / 9;

			if (game[y][x] == 0) {
					List<Integer> numbers = new ArrayList<Integer>();
					for (int i = 1; i <= 9; i++)
							numbers.add(i);

					while (numbers.size() > 0) {
							int number = getNextPossibleNumber(game, x, y, numbers);
							if (number == -1)
									break;
							game[y][x] = number;

							if (!isValid(game, index + 1, numberOfSolutions)) {
									game[y][x] = 0;
									return false;
							}
							game[y][x] = 0;
					}
			} else if (!isValid(game, index + 1, numberOfSolutions))
					return false;

			return true;
	}
	/**
	 * generate a matrix that contains true if the cell is initialized, false otherwise.
	 * that helps to let client modify just valid positions
	 * @return validPositions				matrix of boolean values
	**/
	private void generateValidPositions(boolean[][] validPosition){
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(clientBoard[i][j] == 0){
					validPosition[i][j] = true;
				}else{
					validPosition[i][j] = false;
				}
			}
		}
	}
	/**
	 * to clear board
	**/
	public void clearBoard() throws RemoteException {
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(validPosition[i][j])
					clientBoard[i][j] = 0;
			}
		}
	}
	/**
	 * get help with choosing a value to unhide it
	**/
	public void getHelp(int row, int col) throws RemoteException {
		clientBoard[row][col] = solutionBoard[row][col];
	}


	/**
	 * Copies a game.
	 *
	 * @param game      Game to be copied.
	 * @return          Copy of given game.
	 */
	private int[][] copy(int[][] game) {
			int[][] copy = new int[9][9];
			for (int y = 0; y < 9; y++) {
					for (int x = 0; x < 9; x++)
							copy[y][x] = game[y][x];
			}
			return copy;
	}


	/**
	 * Returns next posible number from list for given position or -1 when list
	 * is empty.
	 *
	 * @param game      Game to check.
	 * @param x         X position in game.
	 * @param y         Y position in game.
	 * @param numbers   List of remaining numbers.
	 * @return          Next possible number for position in game or -1 when
	 *                  list is empty.
	 */
	private int getNextPossibleNumber(int[][] game, int x, int y, List<Integer> numbers) {
			while (numbers.size() > 0) {
					int number = numbers.remove(0);
					if (isPossibleX(game, y, number) && isPossibleY(game, x, number) && isPossibleBlock(game, x, y, number))
							return number;
			}
			return -1;
	}
	/**
	 * Returns whether given number is candidate on x axis for given game.
	 *
	 * @param game      Game to check.
	 * @param y         Position of x axis to check.
	 * @param number    Number to check.
	 * @return          True if number is candidate on x axis, false otherwise.
	 */
	private boolean isPossibleX(int[][] game, int y, int number) {
			for (int x = 0; x < 9; x++) {
					if (game[y][x] == number)
							return false;
			}
			return true;
	}
	/**
	 * Returns whether given number is candidate on y axis for given game.
	 *
	 * @param game      Game to check.
	 * @param x         Position of y axis to check.
	 * @param number    Number to check.
	 * @return          True if number is candidate on y axis, false otherwise.
	 */
	private boolean isPossibleY(int[][] game, int x, int number) {
			for (int y = 0; y < 9; y++) {
					if (game[y][x] == number)
							return false;
			}
			return true;
	}
	/**
	 * Returns whether given number is candidate in block for given game.
	 *
	 * @param game      Game to check.
	 * @param x         Position of number on x axis in game to check.
	 * @param y         Position of number on y axis in game to check.
	 * @param number    Number to check.
	 * @return          True if number is candidate in block, false otherwise.
	 */
	private boolean isPossibleBlock(int[][] game, int x, int y, int number) {
			int x1 = x < 3 ? 0 : x < 6 ? 3 : 6;
			int y1 = y < 3 ? 0 : y < 6 ? 3 : 6;
			for (int yy = y1; yy < y1 + 3; yy++) {
					for (int xx = x1; xx < x1 + 3; xx++) {
							if (game[yy][xx] == number)
									return false;
					}
			}
			return true;
	}


	/*
	 * return current game
	 */
	public int[][] getGame(){return this.clientBoard;}
	/*
	 * return current solution
	 */
	public int[][] getSolution(){return this.solutionBoard;}
	/*
	 * check if position is valid or not.
	 * @param i								x position
	 * @param j 							y position
	 * @return boolean  			true if position is valid, false otherwise.
	**/
	public boolean isValidPosition(int x, int y){
		return( this.validPosition[x][y] && (x < 9) && (0 <= x) && (y < 9) && (0 <= y) );
	}
	/*
	 * return true if the a board is full false otherwise.
	**/
	public boolean isBoardFull() throws RemoteException {
		for(int i = 0; i<9; i++){
			for(int j = 0; j<9; j++){
				if(clientBoard[i][j] == 0){
					return false;
				}
			}
		}
		return true;
	}

	public String insertValue(int row, int col, int value) throws RemoteException {
		if(isValidPosition(row, col)){
			clientBoard[row][col] = value;
			return "";
		}else{
			System.out.println("This position can't be modified");
			return "This position can't be modified";
		}
	}

	public boolean iscorrectBoard() throws RemoteException {
		for(int i = 0; i<9; i++){
			for(int j = 0; j<9; j++){
				if (clientBoard[i][j] != solutionBoard[i][j])
					return false;
			}
		}
		return true;
	}

	public String solutionBoardToString(){
		String str = new String();
		String temp = new String();
		str = (char)27 + "[31;1m\n          0   1   2   3   4   5   6   7   8 			    0   1   2   3   4   5   6   7   8"  + (char)27 + "[0m";
		str += "\n        -------------------------------------			   -------------------------------------\n";
		for(int i = 0, op = 0; i<9; i++){
			str += (char)27 + "[31;1m     " + i + (char)27 + "[0m  ";
			temp = "| " + clientBoard[i][0] + "   " + clientBoard[i][1] + "   " + clientBoard[i][2] + " |";
			temp += " " + clientBoard[i][3] + "   " + clientBoard[i][4] + "   " + clientBoard[i][5] + " |";
			temp += " " + clientBoard[i][6] + "   " + clientBoard[i][7] + "   " + clientBoard[i][8] + " |";
			str += temp.replace("0", ".") ;
			str += "			" + (char)27 + "[31;1m" + i + (char)27 + "[0m  ";
			temp = "| " + solutionBoard[i][0] + "   " + solutionBoard[i][1] + "   " + solutionBoard[i][2] + " |";
			temp += " " + solutionBoard[i][3] + "   " + solutionBoard[i][4] + "   " + solutionBoard[i][5] + " |";
			temp += " " + solutionBoard[i][6] + "   " + solutionBoard[i][7] + "   " + solutionBoard[i][8] + " |\n";
			str += temp.replace("0", ".") ;
			if(i % 3 == 2)
				str += "        -------------------------------------			   -------------------------------------\n";
		}
		return str;
	}
	public String clientBoardToString(){
		String[] options = new String[20];
		for(int k=0; k<20; k++)
			options[k] = "\n";
		options[1] = "		Please choose an option:\n";
		options[2] = "		  1-Insert mode\n";
		options[3] = "		  2-Submit board\n";
		options[4] = "		  3-Clear board\n";
		options[5] = "		  4-Get solution\n";
		options[6] = "		  5-Get help (unhide a cell)\n";
		options[7] = "		  6-Quit this board\n";
		String str = new String();
		String temp = new String();
		str = (char)27 + "[31;1m\n          0   1   2   3   4   5   6   7   8 "  + (char)27 + "[0m";
		str += "\n        -------------------------------------\n";
		for(int i = 0, op = 0; i<9; i++, op++){
			str += (char)27 + "[31;1m     " + i + (char)27 + "[0m  ";
			temp = "| " + clientBoard[i][0] + "   " + clientBoard[i][1] + "   " + clientBoard[i][2] + " |";
			temp += " " + clientBoard[i][3] + "   " + clientBoard[i][4] + "   " + clientBoard[i][5] + " |";
			temp += " " + clientBoard[i][6] + "   " + clientBoard[i][7] + "   " + clientBoard[i][8] + " |";
			str += temp.replace("0", ".") + options[op];
			if(i % 3 == 2){
				op++;
				str += "        -------------------------------------" + options[op];
			}
		}
		return str;
	}
}
