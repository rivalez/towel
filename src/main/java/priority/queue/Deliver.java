package priority.queue;

final class Deliver {
    private final Package pack;
    private final Office office;
    private final Client client;
    private State state = State.NEW;

    Deliver(Package pack, Office office, Client client) {
        this.pack = pack;
        this.office = office;
        this.client = client;
    }

    Package getPack() {
        return pack;
    }

    void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Deliver{" +
                "pack=" + pack +
                ", office=" + office +
                ", client=" + client +
                ", state=" + state +
                '}';
    }
}
