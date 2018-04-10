package priority.queue;

import java.util.Arrays;

final class Package implements Comparable<Package> {
    private final int id;
    private final Client client;
    private final byte[] content = {0,0,0,1};

    Package(int id, Client client) {
        this.id = id;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id=" + id +
                ", client=" + client +
                ", content=" + Arrays.toString(content) +
                '}';
    }

    @Override
    public int compareTo(Package o) {
        return 1;
    }
}
