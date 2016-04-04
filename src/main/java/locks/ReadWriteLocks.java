package locks;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ReadWriteLocks {

    public static final int ARRAY_LENGTH = 10;

    public static void main(String[] args) {
        ConcurrentArray<Integer> concurrentArray = new ConcurrentArray<>(ARRAY_LENGTH);
        IntStream.range(0, 3).forEach(i -> new Thread(() -> {
            while (true) {
                System.out.println("Reading "+ Arrays.toString(concurrentArray.read()));
            }
        }).start());
        new Thread(() -> {
            Random random = new Random();
            while (true) {
                Integer[] ints = Stream.generate(random :: nextInt).limit(random.nextInt(ARRAY_LENGTH + 1)).toArray(Integer[]::new);
                concurrentArray.write(ints, ARRAY_LENGTH - ints.length);
            }
        }).start();
    }

    public static class ConcurrentArray<T> {

        private Object [] items;
        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private Random random = new Random();

        public ConcurrentArray(int capacity) {
            items = new Object[capacity];
        }

        public void write(T [] values, int index) {
            readWriteLock.writeLock().lock();
            try {
                if (items.length < values.length + index) {
                    throw new RuntimeException("Not enough space in items");
                }
                System.arraycopy(values, 0, items, index, values.length);
                System.out.println("Array updated " + Arrays.toString(items));
                Thread.sleep(random.nextInt(1000));
                System.out.println("Array updated " + Arrays.toString(items));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        public T[] read() {
            readWriteLock.readLock().lock();
            try {
                Object[] result = Arrays.copyOf(items, items.length);
                Thread.sleep(random.nextInt(100));
                return (T[]) result;
            } catch (InterruptedException e) {
               throw new RuntimeException(e);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }

}
