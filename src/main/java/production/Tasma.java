package production;

class Tasma {
    private Integer tasma[];

    Tasma(int size) {
        tasma = new Integer[size];
    }

    int getSize() {
        return tasma.length;
    }

    Integer get(int pos) {
        return tasma[pos];
    }

    void set(int pos, Integer i) {
        tasma[pos] = i;
    }
}

class Worker implements Runnable {
    private int id;
    private Tasma t;
    private int counter;

    Worker(Tasma t, int i) {
        this.t = t;
        id = i;
    }

    public void run() {
        while (true)
            synchronized (t) {
                if (id == 0) {
                    if (t.get(0) == null) {
                        t.set(0, new Integer(counter));
                        System.out.println("Set " + counter);
                        counter++;
                        t.notifyAll();
                    } else {
                        try {
                            t.wait();
                        } catch (Exception e) {
                        }  // UWAGA: Bardzo niebezpieczna konstrukcja !!!!
                    } // if
                } // if id == 0

                if (id == t.getSize()) {
                    if (t.get(id - 1) != null) {
                        System.out.println("Kasowanie " + t.get(id - 1));
                        t.set(id - 1, null);
                        t.notifyAll();
                    } else {
                        try {
                            t.wait();
                        } catch (Exception e) {
                        }
                    } // if
                } // if id = t.length

                if ((id > 0) && (id < t.getSize())) {
                    if ((t.get(id - 1) != null) && (t.get(id) == null)) {
                        t.set(id, t.get(id - 1));
                        t.set(id - 1, null);
                        System.out.println("move " + (id - 1) + " ---( " + t.get(id) + " ) --- > " + (id));
                        t.notifyAll();
                    } else {
                        try {
                            t.wait();
                        } catch (Exception e) {
                        }
                    }
                } // ( ( id > 0 ) && ( id < t.length ) )
            } // synchronized
    } // run
} // class Worker


class Start {
    public static void main(String[] s) {
        final int N = 6;

        Tasma t = new Tasma(N);

        for (int i = N; i >= 0; i--) {
            System.out.println("Watek " + i);
            (new Thread(new Worker(t, i))).start();
        }

    }
}
