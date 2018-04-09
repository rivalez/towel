package reentrantlock;

import java.util.concurrent.locks.*;

class BezpiecznaZmienna {
    private double zmienna;
    private final ReentrantReadWriteLock rrwl = new ReentrantReadWriteLock();
    private final Lock blokadaDoZapisu = rrwl.writeLock();
    private final Lock blokadaDoOdczytu = rrwl.readLock();

    public void set( double z ) {
        blokadaDoZapisu.lock();
        try {
            System.out.println( Thread.currentThread().getName() + " W-LOCK" );
            Thread.currentThread().yield();
            zmienna = z;
        }
        finally {
            System.out.println( Thread.currentThread().getName() + " W-UNLOCK" );
            blokadaDoZapisu.unlock();
        }
    }

    public double get() {
        blokadaDoOdczytu.lock();
        try {
            System.out.println( Thread.currentThread().getName() + " R-LOCK" );
            Thread.currentThread().yield();
            return zmienna;
        }
        finally {
            System.out.println( Thread.currentThread().getName() + " R-UNLOCK" );
            blokadaDoOdczytu.unlock();
        }
    }
}

class ReenRW implements Runnable {
    private BezpiecznaZmienna z;

    public ReenRW( BezpiecznaZmienna tmp ) {
        z = tmp;
    }

    public void run() {
        for ( int i = 0; i < 10; i++ )
        {
            z.set(i);
            System.out.println( "Liczba = " + z.get() + " i jeszcze " + z.get() );
        }
    }
}

class TesterReenRW {

    public static void main( String[] args ) {
        BezpiecznaZmienna z = new BezpiecznaZmienna();
        ReenRW rrw = new ReenRW( z );

        Thread t1 = new Thread( rrw );
        Thread t2 = new Thread( rrw );
        t1.start();
        t2.start();

        System.out.println( "Koniec metody main()" );
    }

}
