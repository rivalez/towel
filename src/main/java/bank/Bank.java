package bank;

class Bank {
    private int[] stanKont;

    public Bank( int lKont ) {
        stanKont = new int[ lKont ];
    }

    public void zleceniePrzelewu( final int zKonta, final int naKonto, final int ile ) {
// kazde zlecenie przelewu generuje nowy watek
        new Thread( new Runnable() {
            public void run() {
                System.out.println( "   Zlecono przelew z " + zKonta + " na " + naKonto + " zlotych w kwocie " + ile );

                synchronized ( stanKont ) { // blokada stanu kont
                    while ( stanKont[ zKonta] < ile ) {
                        System.out.println( "      Brak srodkow w koncie " + zKonta + " jest " + stanKont[ zKonta] + " a potrzeba " + ile );
                        try {
                            stanKont.wait();  // zatrzymujemy watek i czekamy na cud...
                        }
                        catch ( Exception e ) {}
                    } // while
                    stanKont[ zKonta ] -= ile;
                    stanKont[ naKonto ] += ile;
                    stanKont.notifyAll(); // dokonano zmiany stanu, wiec informujemy o tym innych
                    System.out.println( "   Wykonano przelew z " + zKonta + " na " + naKonto + " kwoty " + ile );
                } // synchronized

            } // run
        }).start();
    }

    public void wplata( int naKonto, int ile ) {
        synchronized( stanKont ) {
            stanKont[ naKonto ] += ile;
            stanKont.notifyAll(); // wlata zmienia stan konta, wiec budzimy watki
            System.out.println( "Wplata na konto " + naKonto + " w kwocie " + ile + " daje w sumie " + stanKont[ naKonto ] );
        } // synchronized
    }

    public String toString() {
        String t = "                     ^\n" + "                  ^^^^^^^\n" + "               ^^^^^^^^^^^^^\n" + "               II  iBANK  II\n" + "               II---------II\n";
        t += "Stan kont w iBanku :\n";
        for ( int i = 0; i < stanKont.length; i++ )
            t += " Konto " + i + " stan " + stanKont[ i ] + "\n";
        return t;
    }

    public static void main( String[] argv ) throws Exception {
        final int LICZBA_KONT = 5;
        Bank b = new Bank( LICZBA_KONT );

        b.zleceniePrzelewu( 0, 1, 10 );
        b.zleceniePrzelewu( 1, 2, 10 );
        b.zleceniePrzelewu( 2, 1, 10 );
        b.zleceniePrzelewu( 1, 3, 10 );
        b.zleceniePrzelewu( 1, 4, 10 );

        b.wplata( 0, 11 );
        Thread.sleep( 500 );

        b.wplata( 1, 11 );
        Thread.sleep( 500 );

        System.out.println( b );
    }
}


