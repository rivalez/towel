package priority.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransportManagementImpl implements TransportManagement {
    private ExecutorService couriers;
    private List<Client> clients;
    private final Office office = new Office();

    @Override
    public void setNumberOfCouriers(int value) {
        this.couriers = Executors.newFixedThreadPool(value);
    }

    @Override
    public void setExistingClients(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public void sendPackage(Package pack) {
        Deliver deliver = new Deliver(pack, office, clients.get(0));
        DeliverTask deliverTask = new DeliverTask(deliver);
        couriers.submit(new FutureTaskDeliver(deliverTask, deliver, office));
    }
}
