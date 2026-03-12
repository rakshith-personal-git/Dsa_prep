package MC.messagebroker.util;

import MC.messagebroker.core.MessageBroker;
import MC.messagebroker.core.MessageBrokerImpl;

public final class BrokerFactory {
    private BrokerFactory() {
    }

    public static MessageBroker create() {
        return new MessageBrokerImpl();
    }
}
