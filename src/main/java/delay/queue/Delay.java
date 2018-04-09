package delay.queue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

class Delay {
    class Consumer implements Runnable {
        private DelayQueue<Product> queue;

        public Consumer(DelayQueue<Product> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            queue.forEach(product -> {
                try {
                    queue.take();
                    System.out.println("Size of queue during consuming elements is: " + queue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    class Producer implements Runnable {
        private DelayQueue<Product> queue;

        public Producer(DelayQueue<Product> queue) {
            this.queue = queue;
        }

        private void produce() {
            IntStream.range(0, 100).forEach(createProduct());
        }

        private IntConsumer createProduct() {
            return value -> queue.put(new Product(value, 10L));
        }

        @Override
        public void run() {
            produce();
        }
    }

    class Product implements Delayed {
        private int value;
        private long startTime;


        public Product(int value, long delayInMilis) {
            this.value = value;
            this.startTime = System.currentTimeMillis() + delayInMilis;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = startTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.startTime - ((Product) o).startTime);
        }
    }
}
