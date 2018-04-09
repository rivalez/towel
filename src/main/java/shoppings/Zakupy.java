package shoppings;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MySleep {
    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
        }
    }
}

class Konto {
    private volatile double stan = 0.0;

    void wplata(double ile) {
        stan += ile;
    }

    void wyplata(double ile) {
        stan -= ile;
    }

    double stanKonta() {
        return stan;
    }
}

class Zakupy implements Runnable {
    private Konto p;
    private static Lock lock = new ReentrantLock();

    public void maszKonto(Konto p) {
        this.p = p;
    }

    public void run() {
        double cena = 77.78;            // znam cene towaru
        MySleep.sleep(200);// chwila na zastanowienie...
        System.out.println("Tu ja " + Thread.currentThread().getName() + " przed lockiem");
        lock.lock();
        if (p.stanKonta() > cena) {   // sprawdzam czy mam pieniadze na koncie
            System.out.println("Tu ja " + Thread.currentThread().getName() + " ide do kasy zaplacic");
            MySleep.sleep(500);// ide do kasy
            p.wyplata(cena);           // wyplata pieniedzy
        }
        lock.unlock();
    }
}

class Start {
    public static void main(String[] argv) {
        Konto k = new Konto();
        k.wplata(120.0);

        Zakupy zakupyJasia = new Zakupy();
        zakupyJasia.maszKonto(k);

        Zakupy zakupyMarysi = new Zakupy();
        zakupyMarysi.maszKonto(k);

        Thread watekJasia = new Thread(zakupyJasia);
        watekJasia.setName("watek Jasia");

        Thread watekMarysi = new Thread(zakupyMarysi);
        watekMarysi.setName("watek Marysi");

        watekMarysi.start();
        watekJasia.start();

        System.out.println("Wlasnie uruchomione zostaly 2 odrebne watki - czekamy na rezultat");

        MySleep.sleep(1000);

        System.out.println("Sprawdzamy stan konta: " + k.stanKonta());

        if (k.stanKonta() < 0) { // tu nalezy wyrazic zdziwienie
            System.out.println("Przecież sie umawialiśmy - przed zakupem masz sprawdzić czy są pieniędze na koncie !");
            System.out.println("A jest DEBET !!!!");
        }
    }
}
