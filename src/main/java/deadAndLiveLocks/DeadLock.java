package deadAndLiveLocks;

import java.util.Random;

public class DeadLock {

    private static Random random = new Random();
    private static Account source = new Account(100L, 1);
    private static Account target = new Account(200L, 2);


    public static void main(String[] args) {
        new Thread(new Worker()).start();
        new Thread(new Worker()).start();
    }

    public static void transfer(Account source, Account target, long amount) {
        final Account a1;
        final  Account a2;

        if (source.compareTo(target) >= 1) {
            a1 = source;
            a2 = target;
        } else {
            a1 = target;
            a2 = source;
        }
        synchronized (a1) {
            synchronized (a2) {
                if (source.getBalance() >= amount) {
                    source.withdraw(amount);
                    target.put(amount);
                    System.out.println("Transfer "+amount);
                } else {
                    System.out.println("Not enough money");
                }
            }
        }
    }

    public static class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (random.nextBoolean()) {
                    transfer(source, target, random.nextInt(10));
                } else {
                    transfer(target, source, random.nextInt(10));
                }
            }
        }
    }
    public static class Account implements Comparable<Account>{

        private long balance;
        private int id;

        public Account(long balance, int id) {
            this.balance = balance;
            this.id = id;
        }

        public long getBalance() {
            return balance;
        }

        public void put(long amount) {
            balance += amount;
        }

        public void withdraw(long amount) {
            balance -= amount;
        }

        @Override
        public int compareTo(Account o) {
            if (id > o.id) return 1;
            else if (id < o.id) return -1;
            else return 0;
        }
    }
}
