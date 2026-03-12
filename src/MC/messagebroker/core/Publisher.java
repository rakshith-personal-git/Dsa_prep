package MC.messagebroker.core;

public interface Publisher {
    String getTopicName();

    long publish(String payload);

    void close();
}
