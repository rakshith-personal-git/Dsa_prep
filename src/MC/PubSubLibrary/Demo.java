package MC.PubSubLibrary;

import MC.PubSubLibrary.api.Broker;
import MC.PubSubLibrary.api.Consumer;
import MC.PubSubLibrary.api.Publisher;
import MC.PubSubLibrary.api.TopicConfig;
import MC.PubSubLibrary.model.Message;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demo for the Pub-Sub library (SDE3/4 structure). Uses API interfaces only.
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        Broker broker = PubSubLibrary.createBroker();

        broker.createTopic(TopicConfig.of("orders", 10_000));

        Publisher p1 = broker.createPublisher("orders");
        Consumer c1 = broker.createConsumer("orders");
        Consumer c2 = broker.createConsumer("orders");

        p1.publish("order-1");
        p1.publish("order-2");
        p1.publish("order-3");

        System.out.println("C1: " + c1.consume().map(Message::getContent).orElse("none"));
        System.out.println("C1: " + c1.consume().map(Message::getContent).orElse("none"));
        System.out.println("C2: " + c2.consume().map(Message::getContent).orElse("none"));

        System.out.println("C1 offset=" + c1.getCurrentOffset() + ", lag=" + c1.getLag());
        System.out.println("C2 offset=" + c2.getCurrentOffset() + ", lag=" + c2.getLag());
        System.out.println("Topic: " + broker.getTopicInfo("orders"));

        c1.resetOffset(0);
        Optional<Message> replayed = c1.consume();
        System.out.println("C1 after reset: " + replayed.map(Message::getContent).orElse("none"));

        Publisher p2 = broker.createPublisher("orders");
        Publisher p3 = broker.createPublisher("orders");
        ExecutorService exec = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            final int id = i;
            exec.submit(() -> {
                p1.publish("parallel-" + id + "-a");
                p2.publish("parallel-" + id + "-b");
                p3.publish("parallel-" + id + "-c");
            });
        }
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Topic after parallel publish: " + broker.getTopicInfo("orders"));
        System.out.println("C1 offset info: " + c1.getOffsetInfo());
        System.out.println("Demo done.");
    }
}
