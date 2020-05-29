package example.hello;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements Licznik {
    int licznik = 0;

    public Server() {}

    @Override
    public void zwieksz() throws RemoteException, InterruptedException {
        int tmp = licznik;
        Thread.sleep(5000);
        licznik = tmp + 1;
    }

    @Override
    public void zmniejsz() throws RemoteException, InterruptedException {
        int tmp = licznik;
        Thread.sleep(5000);
        licznik = tmp - 1;
    }

    @Override
    public int odczytaj() throws RemoteException {
        return licznik;
    }

    public static void main(String[] args)
    {
        try {
            Server obj = new Server();
            Licznik server = (Licznik) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(1234);
            registry.bind("Licznik", server);
            System.out.println("Binded");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
