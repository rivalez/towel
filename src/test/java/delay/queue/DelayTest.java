package delay.queue;


import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

class DelayTest {

    @Test
    public void shouldConsumeElements() throws InterruptedException {
        //given
        Delay testClass = new Delay();
        DelayQueue<Delay.Product> queue = new DelayQueue<>();
        Delay.Producer producer = testClass.new Producer(queue);
        Delay.Consumer consumer = testClass.new Consumer(queue);

        ExecutorService threads = Executors.newFixedThreadPool(2);
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        //when
        threads.execute(producer);
        scheduled.schedule(consumer, 1000L, TimeUnit.MILLISECONDS);

        //then
        threads.awaitTermination(1000L, TimeUnit.MILLISECONDS);
        threads.shutdown();
    }
}