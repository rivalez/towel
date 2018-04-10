package priority.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

final class FutureTaskDeliver extends FutureTask<DeliverTask> {
    private final Deliver deliver;
    private final Office office;

    FutureTaskDeliver(Callable<DeliverTask> callable, Deliver deliver, Office office) {
            super(callable);
        this.deliver = deliver;
        this.office = office;
    }

    @Override
    protected void done() {
        deliver.setState(State.DELIVERED);
        office.inform(deliver);
    }
}
