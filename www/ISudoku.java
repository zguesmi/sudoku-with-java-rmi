import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ISudoku extends Remote {
	public void generateSudokuBoard() throws RemoteException;
	public boolean isBoardFull() throws RemoteException;
	public String solutionBoardToString() throws RemoteException;
	public String clientBoardToString() throws RemoteException;
	public String insertValue(int row, int col, int value) throws RemoteException;
	public boolean iscorrectBoard() throws RemoteException;
	public void clearBoard() throws RemoteException;
	public void getHelp(int row, int col) throws RemoteException;

	// public String printPos() throws RemoteException;
}
