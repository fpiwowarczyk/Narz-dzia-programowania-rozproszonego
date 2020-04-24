
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class semafory
 { 

    public interface IBuffer
    {
        void put(int v,String name,Integer POISON) throws InterruptedException;
        int get(String name,Integer POISON) throws InterruptedException;
    }



    public static class Producer extends Thread
    {
        private final IBuffer target;
        public String name;
        public int v=100;
        private final Integer POISON; 

        public Producer(final IBuffer target, final String name, final Integer POISON) {
            this.target = target;
            this.name = name;
            this.POISON = POISON;

        }

        public void run() {
            if (target != null)
                try {
                    target.put(v, name, POISON);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
        }

    }

    public static class Consumer extends Thread {
        private final IBuffer target;
        private final String name;
        private final Integer POISON;

        public Consumer(final IBuffer target, final String name, final Integer POISON) {
            this.target = target;
            this.name = name;
            this.POISON = POISON;
        }

        public void run() {
            if (target != null)
                try {
                    target.get(name, POISON);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
        }

    }

    public static class PC implements IBuffer {
        private static final int CAPACITY = 10;
        private final Queue<Integer> queue = new LinkedList<>();

        static Semaphore semCons = new Semaphore(0);
        static Semaphore semProds = new Semaphore(1);
        public static Semaphore mutex = new Semaphore(1);
        public void put(int v, final String name, final Integer POISON) throws InterruptedException 
        {
        while(v<=110)
        {   
            while(queue.size()<=CAPACITY)
            {
                semProds.acquire();
                        queue.add(v++);
                semProds.release();
            }

            semCons.release();
        }
        queue.add(POISON);

        }

        public int get(final String name, final Integer POISON) throws InterruptedException
        {
        while(true)
        {
            Integer val=0;
            while(queue.size()!=0)
            {
                semCons.acquire();
                        val= (Integer) queue.remove();
                        if(val==POISON) break;   
                        System.out.println(name + " consumed-" + val);
                semCons.release();  
            }
            semProds.release();  
            if(val==POISON) break;  

        }
         return 0;
        }

    public static void main(final String[] args) throws InterruptedException {
        final PC pc = new PC();
        final Integer POISON = -1;
        final ExecutorService executorService = Executors.newFixedThreadPool(6);
        executorService.execute(new Producer(pc, "prod1",POISON));
        executorService.execute(new Producer(pc, "prod2",POISON));
        executorService.execute(new Producer(pc, "prod3",POISON));

        executorService.execute(new Consumer(pc, "Konsument 1",POISON));
        executorService.execute(new Consumer(pc, "Konsument 2",POISON));
        executorService.execute(new Consumer(pc, "Konsument 3",POISON));

        executorService.shutdown();

        }
    }
}