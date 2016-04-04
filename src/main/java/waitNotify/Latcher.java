package waitNotify;


public class Latcher {

    public static void main(String[] args) {
        new Latcher().test();
    }

    private CountDownLatch latch;

    public void test() {
        latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            new Thread(new Worker()).start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (latch.getCounter() > 0) {
                    latch.countDown();
                }
            }
        }).start();
    }

    public class Worker implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + " start waiting");
                latch.await();
                System.out.println("Thread " + Thread.currentThread().getName() + " stop waiting");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
 }
