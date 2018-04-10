package priority.queue;

import java.util.List;

class ClientValidator {
    private final List<Client> clients;

    ClientValidator(List<Client> clients) {
        this.clients = clients;
    }

    boolean checkIfClientExists(Package pack) {
        return clients.stream().map(e -> e.equals(pack.getClient())).findAny().orElse(false);
    }
}
