package concurrentColections;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Conditions {

    public static void main(String[] args) {
        TransferQueue<String> queue = new TransferQueue<>(10);
        IntStream.range(0, 5).forEach((i) -> new Thread(() -> {
            while (true) {
                try {
                    queue.put(Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } ).start());
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Taking out + " + queue.take());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static class TransferQueue<T> {

        private final Lock lock = new ReentrantLock();
        private final Condition full = lock.newCondition();
        private final Condition empty = lock.newCondition();


        private Object[] items;
        private int putIndex = 0;
        private int takeIndex = 0;
        private int size = 0;

        public TransferQueue(int capacity) {
           items = new Object[capacity];
        }

        public void put(T value) throws InterruptedException {
            lock.lock();
            try {
                while (size == items.length) {
                    System.out.println("Queue is full " + Thread.currentThread().getName() + " start waiting");
                    full.await();
                }
                items[putIndex] = value;
                if (++putIndex == items.length) {
                    putIndex = 0;
                }
                size++;
                empty.signal();
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (size == 0) {
                    System.out.println("Queue is empty " + Thread.currentThread().getName() + " start waiting");
                    empty.await();
                }
                T result = (T)items[takeIndex];
                if (++takeIndex == items.length) {
                    takeIndex = 0;
                }
                size--;
                full.signal();
                return result;
            } finally {
                lock.unlock();
            }
        }


        public int getPutIndex() {
            return putIndex;
        }

        public void setPutIndex(int putIndex) {
            this.putIndex = putIndex;
        }

        public int getTakeIndex() {
            return takeIndex;
        }

        public void setTakeIndex(int takeIndex) {
            this.takeIndex = takeIndex;
        }

        public Object[] getItems() {
            return items;
        }

        public void setItems(Object[] items) {
            this.items = items;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
