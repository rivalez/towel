package priority.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class Deliver extends FutureTask<Deliver> {
    private Office office;

    public Deliver(Callable<Deliver> callable) {
        super(callable);
    }

    public Deliver(Runnable runnable, Deliver result) {
        super(runnable, result);
    }

    @Override
    protected void done() {
        try {
            office.inform(this.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //notify about ready

    }
}
