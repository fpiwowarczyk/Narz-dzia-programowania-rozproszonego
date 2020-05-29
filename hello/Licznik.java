package example.hello;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface Licznik extends Remote {
    public void zwieksz() throws RemoteException, InterruptedException;
    public void zmniejsz() throws RemoteException, InterruptedException;
    public int odczytaj() throws RemoteException;
}
