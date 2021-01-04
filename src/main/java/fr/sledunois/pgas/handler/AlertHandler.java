package fr.sledunois.pgas.handler;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.pubsub.PgSubscriber;

public class AlertHandler {
    private final Logger log = LoggerFactory.getLogger(AlertHandler.class);
    private static final String SUBSCRIBER_NAME = "account_less_than_0";

    public AlertHandler(PgSubscriber subscriber) {
        subscriber.channel(SUBSCRIBER_NAME)
                .handler(payload -> log.info(String.format("Receiving an alert: Account %s is less than 0", payload)));

        subscriber.connect()
                .onFailure(err -> log.error(String.format("Unable to connect subscriber %s", SUBSCRIBER_NAME), err));
    }

}
