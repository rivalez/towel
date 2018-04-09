package ball;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Pilka {
    private int doKogo;
    public boolean show = true;

    public synchronized void setDoKogo(int doKogo, int kto) {
        System.out.println("Pilka:setDoKogo " + kto + " ---> " + doKogo);
        this.doKogo = doKogo;
    }

    public synchronized int getDoKogo() {
        return doKogo;
    }
}

class Zawodnik implements Runnable {
    private int myID;
    private Pilka p;
    private int liczbaZawodnikow;
    java.util.Random rnd;

    public Zawodnik(int id, Pilka p, int liczbaZawodnikow) {
        myID = id;
        this.p = p;
        this.liczbaZawodnikow = liczbaZawodnikow;
        rnd = new java.util.Random(id);
    }

    public void run() {
        int doKogo;
        while (true) {
            if (p.getDoKogo() == myID) {
                doKogo = rnd.nextInt(liczbaZawodnikow);
                System.out.println("Zadownik nr. " + myID + " ---> " + doKogo);
                p.setDoKogo(doKogo, myID);
            }
        }
    }
}

class Start {
    public static void main(String[] argv) throws Exception {
        Pilka p = new Pilka();

        final int liczbaZawodnikow = 20;
        ExecutorService service = Executors.newFixedThreadPool(liczbaZawodnikow);
        for (int i = 0; i < liczbaZawodnikow; i++) {
            service.submit(new Zawodnik(i, p, liczbaZawodnikow));
        }

        while (true) {
            Thread.sleep(1000);
            System.out.println("Tu MAIN: pilka jest u : " + p.getDoKogo());
        }
    }
}
