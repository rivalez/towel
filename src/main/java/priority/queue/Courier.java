package priority.queue;

public class Courier {
    private Package pack;
    private Deliver deliver;

    public Courier(Package pack, Deliver deliver) {
        this.pack = pack;
        this.deliver = deliver;
    }

    public Package getPack() {
        return pack;
    }

    public Deliver getDeliver() {
        return deliver;
    }
}
