import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Factory extends UnicastRemoteObject implements IFactory {

    private int numberOfClient = 0;

    public Factory() throws RemoteException {}

    public ISudoku newBoard() throws RemoteException {
        if( numberOfClient < 10){
            numberOfClient++;
            System.out.println("We have a new player");
            return new Sudoku();
        }else{
            System.out.println("There's already 10 client playing right now");
            return null;
        }
    }

    public void decreaseNumberOfUsers() throws RemoteException{
        numberOfClient--;
        System.out.println("One player quit the game");
    }
}
