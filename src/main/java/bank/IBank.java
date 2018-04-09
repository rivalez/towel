package bank;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class IBank {
    final Thread thread;
    long[] konta;
    private TransferConsumer consumer;

    IBank(int ileKont) {
        konta = new long[ileKont];
        for (int i = 0; i < ileKont; i++)
            konta[i] = 100;  // kazdemu cos na dobry poczatek
        consumer = new TransferConsumer(konta);
        thread = new Thread(consumer);
        thread.setName("Transfer");
        thread.start();
    }

    public String toString() {
        String tmp = "Stan skarbca banku to: ";
        long sum = 0;
        for (int i = 0; i < konta.length; i++)
            sum += konta[i];
        return tmp + sum;
    }

    void transfer(Transfer transfer) {
        consumer.put(transfer);
    }
}


class TransferConsumer implements Runnable {
    private BlockingQueue<Transfer> queue = new LinkedBlockingQueue<>(10);
    private long[] konta;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();


    TransferConsumer(long[] konta) {
        this.konta = konta;
    }

    void handle() throws InterruptedException {
        lock.lock();
        while(queue.isEmpty()){
            condition.await();
        }
        condition.signal();
        lock.unlock();
        while (!queue.isEmpty()) {
            Transfer transfer = queue.take();
            increment(transfer.receiver, transfer.amount);
            decrement(transfer.sender, transfer.amount);
            System.out.println("przelew zrobil watek: " + Thread.currentThread().getName() + " poszedl przelew z : " + transfer.sender + " do: " + transfer.receiver + " o wartości: " + transfer.amount);
        }

    }

    void put(Transfer transfer) {
        try {
            this.queue.put(transfer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void increment(int i, int kwota) {
        konta[i] += kwota;
    }

    private void decrement(int j, int kwota) {
        konta[j] -= kwota;
    }
}

class Transfer {
    int sender;
    int receiver;
    int amount;

    public Transfer(int sender, int receiver, int amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
}

class WlascicielKonta implements Runnable {
    private int liczbaPrzelewow;
    private final IBank b;

    public WlascicielKonta(int liczbaPrzelewow, IBank b) {
        this.liczbaPrzelewow = liczbaPrzelewow;
        this.b = b;
    }

    public void run() {
        for (int k = 0; k < liczbaPrzelewow; k++) {
            int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, b.konta.length);
            int j = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, b.konta.length);
            int kwota = java.util.concurrent.ThreadLocalRandom.current().nextInt(0, 100);
            b.transfer(new Transfer(i, j, kwota));
        }
    }
}

class Start {
    public static void main(String[] arvg) throws Exception {
        final int L_KONT = 10;
        final int L_WATKOW = 10;
        final int L_PRZELEWOW = 5;
        Thread[] th = new Thread[L_WATKOW];
        IBank b = new IBank(L_KONT);
        System.out.println("Stan banku przed przelewami: \n\n" + b);
        for (int i = 0; i < L_WATKOW; i++) {
            th[i] = new Thread(new WlascicielKonta(L_PRZELEWOW, b));
            th[i].start();
        }
        for (int i = 0; i < L_WATKOW; i++) {
            th[i].join();
        }
        b.thread.join();
        System.out.println("Stan banku przed przelewami: \n\n" + b);
    }
}
