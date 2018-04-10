package priority.queue;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

final class Office {
    private final Logger log = Logger.getLogger(Office.class.getName());
    private final PriorityBlockingQueue<Package> delivered = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Package> cannotDeliver = new PriorityBlockingQueue<>();

    void inform(Deliver deliver) {
        delivered.put(deliver.getPack());
        log.info("package has been delivered. " + deliver);
    }


    void informIncorrect(Deliver deliver) {
        cannotDeliver.put(deliver.getPack());
        log.info("package cannot be delivered. " + cannotDeliver);
    }
}
