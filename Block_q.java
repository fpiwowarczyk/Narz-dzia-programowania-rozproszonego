
  
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;

public class Block_q
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
        final BlockingQueue<Integer> q = new ArrayBlockingQueue<Integer>(10);

        public void put(int v, final String name, final Integer POISON) throws InterruptedException {
            try {
                while (v <= 200) {
                    q.put(v++);
                }
            } finally {
                q.put(POISON);
            }
        }

        public int get(final String name, final Integer POSION) throws InterruptedException {
            while (true) {
                final int val = q.take();
                if (val == POSION) {
                    return 1;

                } else
                    System.out.println(name + " consumed-" + val);

            }
        }

    }

    public static void main(final String[] args) throws InterruptedException {
        final PC pc = new PC();
        final Integer POISON = -1;
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        executorService.execute(new Producer(pc, "prod1",POISON));
        executorService.execute(new Producer(pc, "prod2",POISON));
        executorService.execute(new Producer(pc, "prod3",POISON));

        executorService.execute(new Consumer(pc, "Konsument 1",POISON));
        executorService.execute(new Consumer(pc, "Konsument 2",POISON));
        executorService.execute(new Consumer(pc, "Konsument 3",POISON));

        executorService.shutdown();

    } 
  
} 