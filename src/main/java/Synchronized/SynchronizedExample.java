package Synchronized;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SynchronizedExample {

    public static void main(String[] args) throws InterruptedException {
        new SynchronizedExample().test();
    }

    private int counter;
    private final Object lock = new Object();

    public int  increment() {
        synchronized (lock) {
            return counter++;
        }
    }

    public void test() throws InterruptedException {
        List<Aggregator> aggregators = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Aggregator aggregator = new Aggregator();
            aggregators.add(aggregator);
            new Thread(aggregator).start();
        }
        Thread.sleep(100);
        boolean isValid = true;
        Set<Integer> intSet = new HashSet<>();
        for (Aggregator aggregator : aggregators) {
            for (Integer integer : aggregator.integers) {
                if (!intSet.add(integer)) {
                    System.out.println("Error! Duplicate found: "+integer);
                    isValid = false;
                }
            }

        }

        if (isValid) {
            System.out.println("No duplicates!");
        }


    }
    public class Aggregator implements Runnable{

        private List<Integer> integers = new ArrayList<>();

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                integers.add(increment());
            }
        }
    }
}
