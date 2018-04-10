package priority.queue;

import java.util.concurrent.Callable;

final class DeliverTask implements Callable<DeliverTask> {
    private final Deliver deliver;

    DeliverTask(Deliver deliver) {
        this.deliver = deliver;
    }

    @Override
    public DeliverTask call() {
        deliver.setState(State.ON_WAY);
        System.out.println(Thread.currentThread().getName() + " handles " + this + " on the way");
        return this;
    }

    @Override
    public String toString() {
        return "DeliverTask{" +
                "deliver=" + deliver +
                '}';
    }
}
