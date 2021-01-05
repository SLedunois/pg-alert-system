package fr.sledunois.pgas;

import fr.sledunois.pgas.controller.AccountController;
import fr.sledunois.pgas.handler.AlertHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.pubsub.PgSubscriber;
import io.vertx.sqlclient.PoolOptions;

public class MainVerticle extends AbstractVerticle {

  private int pgPort = 5432;
  private String pgHost = "localhost";
  private String pgDB = "pgas";
  private String pgUser = "postgres";
  private String pgPassword = "postgres";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Init Http server and its router
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    // Init PostgreSQL client
    PgPool pgClient = initPg(vertx);

    // Add request logger
    router.route().handler(LoggerHandler.create());

    // Enable request body handler
    router.route().handler(BodyHandler.create());

    // Init account controller
    new AccountController(router, pgClient);

    // Init alert handler
    new AlertHandler(pgSubscriber(vertx));

    // Start http server using router
    server.requestHandler(router).listen(8080);

    // Complete verticle launch
    startPromise.complete();
  }

  private PgPool initPg(Vertx vertx) {
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    return PgPool.pool(vertx, pgConnectOptions(), poolOptions);
  }

  private PgSubscriber pgSubscriber(Vertx vertx) {
    return PgSubscriber.subscriber(vertx, pgConnectOptions());
  }

  private PgConnectOptions pgConnectOptions() {
    return new PgConnectOptions().setPort(pgPort).setHost(pgHost).setDatabase(pgDB).setUser(pgUser)
        .setPassword(pgPassword);
  }
}
