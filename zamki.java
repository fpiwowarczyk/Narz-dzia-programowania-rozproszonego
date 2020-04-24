
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.*;
public class zamki
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

        private final Lock Lock = new ReentrantLock();
        private final Condition bufferNotFull = Lock.newCondition();
        private final Condition bufferNotEmpty = Lock.newCondition();

        public void put(int v, final String name, final Integer POISON) throws InterruptedException 
        {
        while(v<=200)
        {
            Lock.lock();
            try
            {
                while(queue.size()==CAPACITY)
                    bufferNotFull.await();
                 queue.add(v++);
                bufferNotEmpty.signal();
            }
            finally
            {
                Lock.unlock();
            }
        }
        queue.add(POISON);

        }

        public int get(final String name, final Integer POISON) throws InterruptedException
        {
        while(true)
        {
            Lock.lock();
            try
            {
                while(queue.size()==0)
                    bufferNotEmpty.await();

                Integer val = (Integer) queue.remove();
                if(val==POISON)
                {
                    break;
                }
                else
                {
                    System.out.println(name + " consumed-" + val);
                    bufferNotFull.signal();
                }                       
            }
            finally
            {
                    Lock.unlock();
            }
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