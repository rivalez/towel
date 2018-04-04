import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConversionManagement implements ConversionManagementInterface {
    private static final int PAIR_START_VALUE = 1;
    private ExecutorService threadPool;
    private final AtomicInteger nextToSend;
    private final DataPortionHandler dataPortionHandler;
    private final Sender sender;
    private final Database database;

    public ConversionManagement() {
        nextToSend = new AtomicInteger(PAIR_START_VALUE);
        dataPortionHandler = new DataPortionHandler();
        sender = new Sender();
        database = new Database();
    }

    public void setCores(int cores) {
        handleChangeThreadPool();
        threadPool = Executors.newFixedThreadPool(cores);
    }

    //size of available thread pool can change in runtime
    private void handleChangeThreadPool() {
        if (threadPool != null) {
            try {
                threadPool.awaitTermination(150, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setConverter(ConverterInterface converter) {
        //todo best way to convert data one by one
//        threadPool.execute(() -> converter.convert(database.paired));
    }

    //not multi threaded
    public void setConversionReceiver(ConversionReceiverInterface receiver) {
//        receiver.result();
    }


    public void addDataPortion(ConverterInterface.DataPortionInterface data) {
        threadPool.execute(() -> dataPortionHandler.addDataToMap(data));
    }

    class Sender {
        private void send() {
            threadPool.execute(this::handlePair);
        }

        private void handlePair() {
            if(database.getPaired().containsKey(nextToSend.get())){
                final ConverterInterface.DataPortionInterface one = database.getPaired().get(nextToSend.get());
                final ConverterInterface.DataPortionInterface two = database.getPaired().get(nextToSend.get());
                database.removePair();
                nextToSend.incrementAndGet();
            }
        }
    }


    //only to test
    public ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> getStore() {
        return database.store;
    }

    public ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> getPaired() {
        return database.paired;
    }

    class Database {

        private final ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> store;
        private final ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> paired;

        Database() {
            paired = new ConcurrentHashMap<>();
            store = new ConcurrentHashMap<>();
        }

        public ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> getStore() {
            return store;
        }

        public ConcurrentHashMap<Integer, ConverterInterface.DataPortionInterface> getPaired() {
            return paired;
        }

        private void removePair() {
            store.remove(nextToSend.get());
            paired.remove(nextToSend.get());
        }
    }

    //data add
    private class DataPortionHandler {
        private void addDataToMap(ConverterInterface.DataPortionInterface data) {
            if(isPossibleToPut(data)) {
                database.getPaired().putIfAbsent(data.id(), data);
            }
        }

        private boolean isPossibleToPut(ConverterInterface.DataPortionInterface data) {
            return null != database.getStore().putIfAbsent(data.id(), data);
        }
    }
}
