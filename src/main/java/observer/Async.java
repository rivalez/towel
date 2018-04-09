package observer;

interface Listener {
    // potwierdzenie zakonczenia akcji
    void done();
}

interface AsynchronicAction {
    // zlecenie wykonania akcji
    void begin();

    // ustawienie obiektu nasłuchującego
    void setListener(Listener listener);
}

interface ThreadNameGetter {
    // metoda zwraca nazwe watku, ktory ja wykonal
    default public String thName() {
        return Thread.currentThread().getName();
    }
    static public String getThreadName() {
        return Thread.currentThread().getName();
    }
}

// klasa realizująca akcję
class Action implements AsynchronicAction, ThreadNameGetter {
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void begin() {
        System.out.println("Tu Action " + thName() + " uruchomiono begin()");
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for ( int i = 0; i < 6; i++ ) {
                        System.out.println("Tu Action " + thName() + " praca w toku...");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException ie) {
                }
                System.out.println("Tu Action " + thName() + " praca zakonczona - wysylam powiadomienie do Listenera");
                listener.done();
                System.out.println("Tu Action " + thName() + " wykonalem done() - wykonanie pracy zostalo potwierdzone");
            }
        });
        th.start();
        System.out.println("Tu Action " + thName() + " koniec pracy begin()");
    }
}

// klasa użytkownika, ktory zleca akcje do wykonania
class ActionUser implements Runnable, ThreadNameGetter {
    public final AsynchronicAction aAction;

    public ActionUser(AsynchronicAction aAction) {
        this.aAction = aAction;
    }

    class ListenerImplemenatation implements Listener {
        @Override
        public void done() {
            System.out.println("Tu ListenerImplemenatation " + thName() + " wykonano done");
            System.out.println("Tu ListenerImplemenatation " + thName() + " obudze watek, ktory na to czekal");
            synchronized (ActionUser.this) {
                ActionUser.this.notify();
                System.out.println("Tu ListenerImplemenatation " + thName() + " juz po notify");
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Tu ActionUser " + thName() + " dodaje obiekt Listener");
        aAction.setListener(new ListenerImplemenatation());
        System.out.println("Tu ActionUser " + thName() + " zaraz zaczynam Akcje, wykonuje begin");
        aAction.begin();
        System.out.println("Tu ActionUser " + thName() + " to znowu ja, juz po uruchomieniu begin");
        System.out.println("Tu ActionUser " + thName() + " zatrzymam sie, oczekujac na potwierdzenie zakonczenia akcji");
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Tu ActionUser " + thName() + " witajcie ponownie. Akcja zlecona przez begin juz jest zakonczona");
    }
}

class Start {
    public static void main(String[] args) {
        System.out.println( "Oto ja " + ThreadNameGetter.getThreadName() +
                " tworzę obiekt Action i przekazuje go do ActionUser");
        (new Thread(new ActionUser(new Action()))).start();
    }
}
