package MC.messagebroker.core;

import MC.messagebroker.model.ConsumerLag;
import MC.messagebroker.model.Message;

import java.util.List;

public interface Consumer {
    String getConsumerId();

    String getTopicName();

    long getCurrentOffset();

    void resetOffset(long offset);

    List<Message> poll(int maxMessages);

    ConsumerLag getLag();

    void close();
}
