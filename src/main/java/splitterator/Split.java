package splitterator;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;

public class Split {
    static final AtomicInteger ai = new AtomicInteger();
    static final int LIMIT = 800;

    static class Sumator {
        int v;
    }

    static void calcOrSplit( Spliterator<Integer> collection ) {
        long size = collection.estimateSize();
        System.out.println( "Rozmiar kolekcji : " + size );
        if (  size > LIMIT ) {
            Thread th = new Thread( () -> Split.calcOrSplit( collection.trySplit() ) );
            System.out.println( "Uruchamiam nowy watek A" );
            th.start();
            Thread th2 = new Thread( () -> Split.calcOrSplit( collection ) );
            System.out.println( "Uruchamiam nowy watek B");
            th2.start();
            try {
                th.join();
                th2.join();
            } catch (InterruptedException e1) {	}
        } else {
            Sumator	sum = new Sumator();
            collection.forEachRemaining( e -> sum.v += e);
            ai.addAndGet( sum.v );
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>();

        for ( int i = 0; i < 5 * LIMIT; i++ )
            list.add( i );

        calcOrSplit( list.spliterator() );
        System.out.println( "Wynik: " + ai.get() );
    }
}
