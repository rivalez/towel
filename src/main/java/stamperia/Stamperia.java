package stamperia;

import java.util.*;
import java.util.concurrent.*;

class CalcRandomSum extends RecursiveTask<Long> implements Callable<Long> {
    private static final long serialVersionUID = 5799860828100165650L;

    private final static boolean USE_LOCALRANDOM = true;
    // uzyj nowszej implementacji Random

    private final static boolean CALLABLE_CALL_USE_RUN = true;
    // wolamy bezposrednio metode run()

    private static final int MAX = 10000;
    // losowanie liczb losowych od 0 do MAX-1

    private static final long MAX_NUMBER_OF_RANDOM_NUMBERS_TO_SUM_PER_TASK = 125000000;
    protected final long NUMER_OF_RANDOM_NUMBERS_TO_SUM; // ilosc liczb do wylosowania
    private final Random rnd;

    /**
     * Ta metoda wykonuje rzeczywista prace - to w niej liczby
     * losowe sa generowane i sumowane.
     *
     * @param repetitions - ilosc powtorzen petli
     * @return suma wylosowanych liczb
     */
    public long run(long repetitions) {
        long sum = 0;

        for (long i = 0; i < repetitions; i++)
            sum += rnd.nextInt(MAX);

        return sum;
    }

    public CalcRandomSum(long repetitions) {
        NUMER_OF_RANDOM_NUMBERS_TO_SUM = repetitions;

        if (USE_LOCALRANDOM)
            rnd = ThreadLocalRandom.current();
        else
            rnd = new Random();
    }

    /**
     *  Implementacji tej metody wymaga zgodnosc z interfejsem Callable<T>
     */
    @Override
    public Long call() throws Exception {
        System.out.println("call()");

        if (CALLABLE_CALL_USE_RUN)
            return run(NUMER_OF_RANDOM_NUMBERS_TO_SUM);
        else
            return compute();
    }

    /**
     * Nadpisanie tej metody wymagane jest do pracy RecursiveTask
     * Metoda sprawdza czy jest wykonywana w srodowisku ForkJoinPool-a i
     * wtedy dokunuje rozdzialu pracy na podzadania. Pracujac poza
     * ForkJoinPool tylko wywoluje run()
     */
    @Override
    protected Long compute() {
        System.out.println("compute()");
        try {
            if (inForkJoinPool()) {
                System.out.println("Pracujemy pod kontrola ForkJoinPool-a");
                if (NUMER_OF_RANDOM_NUMBERS_TO_SUM > MAX_NUMBER_OF_RANDOM_NUMBERS_TO_SUM_PER_TASK) {
                    long half = NUMER_OF_RANDOM_NUMBERS_TO_SUM / 2L;
                    System.out
                            .println("Ilosc liczb do wygenerowania jest za duzo, nastapi podzial po "
                                    + half + " sztuk");
                    CalcRandomSum pierwszaPolowaZadania = new CalcRandomSum(half);
                    pierwszaPolowaZadania.fork(); // wykonaj to jako osobne zadanie
                    CalcRandomSum drugaPolowaZadania = new CalcRandomSum(half);
                    return drugaPolowaZadania.compute() + pierwszaPolowaZadania.get();
                }
            }
            return run(NUMER_OF_RANDOM_NUMBERS_TO_SUM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metoda odpowiedzialna za podzial pracy na jednakowe fragmenty.
     * @param repetitions liczba danych do wygenerowania i zsumowania
     * @param parts liczba podzadan
     * @return kolekcja zawierajace podzadania
     */
    public static Collection<CalcRandomSum> div(long repetitions, int parts) {
        Collection<CalcRandomSum> ccra = new ArrayList<CalcRandomSum>();

        for (int i = 0; i < parts; i++)
            ccra.add(new CalcRandomSum(repetitions / parts));

        return ccra;
    }

}

class Start {
    private final static long NUMBERS_TO_GENERATE = 4000000000L;

    private final static ExecutorService exec = Executors.newCachedThreadPool();
    private static long t0;

    private static void timerStart() {
        t0 = System.currentTimeMillis();
    }

    private static void timerEnd() {
        System.out.println("Czas " + (System.currentTimeMillis() - t0)
                + " msec.");
    }

    // ///////////////////// liczb watek main
    private static void testWprost() throws Exception {
        CalcRandomSum cra = new CalcRandomSum(NUMBERS_TO_GENERATE);
        timerStart();
        System.out.println("Wynik wprost: " + cra.call());
        timerEnd();
    }

    // ///////////////////// liczymy za pomoca executora
    private static void test1Worker() throws Exception {
        CalcRandomSum cra = new CalcRandomSum(NUMBERS_TO_GENERATE);
        timerStart();
        Future<Long> result = exec.submit(cra);
        System.out.println("Wynik 1 thread: " + result.get());
        timerEnd();
    }

    // ///////////////////// ten sam executor, ale wiecej zadan do policzenia
    private static void testNWorker() throws Exception {

        final int NUMBER_OF_SUBTASKS = 25;

        timerStart();
        List<Future<Long>> result = exec.invokeAll(CalcRandomSum.div(
                NUMBERS_TO_GENERATE, NUMBER_OF_SUBTASKS));

        long sum = 0L;
        for (Future<Long> fl : result) {
            sum += fl.get();
        }
        System.out.println("Wynik koncowy N thread: " + sum);
        timerEnd();
    }

    // /////////////////// a teraz dzialamy poprzez ForkJoinPool
    private static void testFJWorker() throws Exception {
        timerStart();
        ForkJoinPool pool = new ForkJoinPool();

        System.out.println("Wynik koncowy N thread: "
                + pool.invoke(new CalcRandomSum(NUMBERS_TO_GENERATE)));
        timerEnd();
    }

    static class MyComparator implements Comparator<CalcRandomSum> {

        @Override
        public int compare(CalcRandomSum o1, CalcRandomSum o2) {
            return 0;
        }
    }

    public static void main(String[] args) throws Exception {
        testWprost();
        test1Worker();
        testNWorker();
        exec.shutdownNow(); // bez tego terminal "zawisa"

        testFJWorker();
    }

}
