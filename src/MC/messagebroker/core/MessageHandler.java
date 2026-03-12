package MC.messagebroker.core;

import MC.messagebroker.model.Message;

@FunctionalInterface
public interface MessageHandler {
    void onMessage(Message message);
}
