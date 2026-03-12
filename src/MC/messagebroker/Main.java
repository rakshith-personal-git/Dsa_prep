package MC.messagebroker;

import MC.messagebroker.core.Consumer;
import MC.messagebroker.core.MessageBroker;
import MC.messagebroker.core.Publisher;
import MC.messagebroker.model.ConsumerLag;
import MC.messagebroker.model.Message;
import MC.messagebroker.util.BrokerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private final MessageBroker broker;
    private Publisher orderPublisher;
    private Consumer consumerA;
    private Consumer consumerB;

    Main(MessageBroker broker) {
        this.broker = broker;
    }

    public static void main(String[] args) throws Exception {
        Main demo = new Main(BrokerFactory.create());
        demo.run();
    }

    private void run() throws Exception {
        sep("DEMO START");

        demoTopicLifecycle();
        demoSinglePublisherSingleConsumer();
        demoMultipleConsumers();
        demoParallelPublishing();
        demoPullModel();
        demoOffsetResetAndReplay();
        demoLagVisibility();
        demoRetentionPeriod();
        demoExceptionHandling();
        demoGracefulShutdown();

        sep("DEMO END");
    }

    private void demoTopicLifecycle() {
        sep("Topic Lifecycle");

        broker.createTopic("orders", Duration.ofHours(1));
        broker.createTopic("payments", Duration.ofMinutes(30));
        broker.createTopic("to-delete", Duration.ofSeconds(60));
        System.out.println("Topics: " + broker.listTopics());

        broker.deleteTopic("to-delete");
        System.out.println("After delete: " + broker.listTopics());

        tryCatch("Duplicate topic", () -> broker.createTopic("orders", Duration.ofHours(1)));
        tryCatch("Delete missing topic", () -> broker.deleteTopic("ghost-topic"));
    }

    private void demoSinglePublisherSingleConsumer() throws InterruptedException {
        sep("Single Publisher / Single Consumer");

        orderPublisher = broker.createPublisher("orders");

        AtomicInteger receivedCount = new AtomicInteger();
        consumerA = broker.createConsumer("consumer-A", "orders", msg -> {
            System.out.printf("  [consumer-A] received offset=%d payload='%s'%n",
                    msg.getOffset(), msg.getPayload());
            receivedCount.incrementAndGet();
        });

        orderPublisher.publish("ORDER-001");
        orderPublisher.publish("ORDER-002");
        orderPublisher.publish("ORDER-003");
        Thread.sleep(200);
        System.out.printf("consumer-A received %d messages%n", receivedCount.get());
    }

    private void demoMultipleConsumers() throws InterruptedException {
        sep("Multiple Consumers");

        AtomicInteger receivedCount = new AtomicInteger();
        consumerB = broker.createConsumer("consumer-B", "orders", msg -> {
            System.out.printf("  [consumer-B] received offset=%d payload='%s'%n",
                    msg.getOffset(), msg.getPayload());
            receivedCount.incrementAndGet();
        });

        orderPublisher.publish("ORDER-004");
        orderPublisher.publish("ORDER-005");
        Thread.sleep(200);
        System.out.printf("consumer-B received %d new messages%n", receivedCount.get());
    }

    private void demoParallelPublishing() throws InterruptedException {
        sep("Parallel Publishing");

        broker.createTopic("perf-topic", Duration.ofMinutes(5));
        Publisher perfPub = broker.createPublisher("perf-topic");

        int threadCount = 10;
        int messagesPerThread = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        AtomicInteger totalPublished = new AtomicInteger();

        for (int t = 0; t < threadCount; t++) {
            int tid = t;
            pool.submit(() -> {
                for (int i = 0; i < messagesPerThread; i++) {
                    perfPub.publish("thread-" + tid + "-msg-" + i);
                    totalPublished.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        pool.shutdown();
        System.out.println("Total published: " + totalPublished.get());
    }

    private void demoPullModel() {
        sep("Pull Model");

        Consumer pollConsumer = broker.createConsumer("poll-C", "perf-topic", msg -> {
        });
        List<Message> batch = pollConsumer.poll(5);
        if (batch.isEmpty()) {
            System.out.println("Polled 0 messages (topic may be empty).");
        } else {
            System.out.println("Polled " + batch.size() + " messages. First: " + batch.get(0));
        }
    }

    private void demoOffsetResetAndReplay() {
        sep("Offset Reset + Replay");

        System.out.printf("consumer-A current offset=%d%n", consumerA.getCurrentOffset());
        consumerA.resetOffset(1);
        System.out.println("Reset to offset=1. Polling 2 messages:");
        consumerA.poll(2).forEach(m -> System.out.println("  replayed: " + m));

        tryCatch("Reset to invalid offset", () -> consumerA.resetOffset(9999));
    }

    private void demoLagVisibility() {
        sep("Lag Visibility");

        ConsumerLag lag = consumerB.getLag();
        System.out.println("consumer-B lag: " + lag);

        Map<String, ConsumerLag> topicLag = broker.getTopicLag("orders");
        topicLag.forEach((cid, l) -> System.out.printf("  %s → lag=%d, lastOffset=%d%n",
                cid, l.getLag(), l.getLastOffset()));
    }

    private void demoRetentionPeriod() throws InterruptedException {
        sep("Retention Period");

        broker.createTopic("short-topic", Duration.ofSeconds(2));
        Publisher shortPub = broker.createPublisher("short-topic");
        shortPub.publish("will-expire");
        shortPub.publish("will-expire-too");
        System.out.println("Published 2 messages to short-topic with 2-second retention.");
        Thread.sleep(3000);

        Consumer shortC = broker.createConsumer("short-c", "short-topic", m ->
                System.out.println("  [short-c] got: " + m));
        List<Message> expired = shortC.poll(10);
        System.out.println("Messages after expiry: " + expired.size());
    }

    private void demoExceptionHandling() {
        sep("Exception Handling");

        tryCatch("Publish to deleted topic", () -> {
            broker.deleteTopic("payments");
            broker.createPublisher("payments").publish("x");
        });
        tryCatch("Duplicate consumer", () -> broker.createConsumer("consumer-A", "orders", m -> {
        }));
        tryCatch("Consumer on unknown topic", () -> broker.createConsumer("x", "no-topic", m -> {
        }));
    }

    private void demoGracefulShutdown() {
        sep("Graceful Shutdown");

        broker.shutdown();
        tryCatch("Publish after shutdown", () -> orderPublisher.publish("too-late"));
        System.out.println("Broker shut down cleanly.");
    }

    private static void sep(String label) {
        System.out.println("\n--- " + label + " ---");
    }

    private static void tryCatch(String desc, Runnable r) {
        try {
            r.run();
            System.out.println("[" + desc + "] (no exception)");
        } catch (Exception e) {
            System.out.printf("[%s] caught %s: %s%n", desc, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
