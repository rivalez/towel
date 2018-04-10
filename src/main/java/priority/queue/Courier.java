package priority.queue;

final class Courier implements Runnable {
    private Package pack;
    private DeliverTask deliver;

    public Courier(Package pack, DeliverTask deliver) {
        this.pack = pack;
        this.deliver = deliver;
    }

    public Package getPack() {
        return pack;
    }

    public DeliverTask getDeliver() {
        return deliver;
    }

    //using setters to change package after delivery
    public void setPack(Package pack) {
        this.pack = pack;
    }

    public void setDeliver(DeliverTask deliver) {
        this.deliver = deliver;
    }

    @Override
    public void run() {

    }
}
