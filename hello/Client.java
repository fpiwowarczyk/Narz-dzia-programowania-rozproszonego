package example.hello;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public Client() {}

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
            Licznik licznik = (Licznik) registry.lookup("Licznik");
            for (int i = 0; i < 10; i++)
            {
                licznik.zwieksz();
                System.out.println("Wartość licznika: "  + licznik.odczytaj());
            }
            for (int i = 0; i < 10; i++)
            {
                licznik.zmniejsz();
                System.out.println("Wartość licznika: "  + licznik.odczytaj());
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();

        }
    }
}
