import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFactory extends Remote {
	public ISudoku newBoard() throws RemoteException;
	public void decreaseNumberOfUsers() throws RemoteException;
}
