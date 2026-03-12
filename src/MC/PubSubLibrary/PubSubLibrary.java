package MC.PubSubLibrary;

import MC.PubSubLibrary.api.Broker;
import MC.PubSubLibrary.impl.InMemoryBroker;

/**
 * Facade and factory for the Pub-Sub library. Entry point for creating a broker instance.
 * Use this for default in-memory implementation; for tests or alternate backends, instantiate
 * alternative Broker implementations directly.
 */
public final class PubSubLibrary {

    private PubSubLibrary() {
    }

    /**
     * Creates a new in-memory broker. Thread-safe; supports multiple topics and parallel publish.
     */
    public static Broker createBroker() {
        return new InMemoryBroker();
    }
}
