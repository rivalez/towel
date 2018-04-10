package priority.queue;

import java.util.List;

public interface TransportManagement {
    void setNumberOfCouriers(int value);

    void setExistingClients(List<Client> clients);

    void sendPackage(Package pack);
}
