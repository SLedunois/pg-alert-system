package fr.sledunois.pgas.service;

import fr.sledunois.pgas.utils.PgResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

public class AccountService {

    private PgPool pgClient;

    public AccountService(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    /**
     * Create a new account. It set the amount with
     * 
     * @param amount
     * @return
     */
    public Future<JsonObject> createAccount(int amount) {
        Promise<JsonObject> promise = Promise.promise();
        pgClient.preparedQuery("INSERT INTO account(amount) VALUES ($1) RETURNING id").execute(Tuple.of(amount), ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
            } else {
                promise.complete(PgResult.rowSetToJsonObject(ar.result()));
            }
        });

        return promise.future();
    }

    /**
     * Get account amount status
     * 
     * @param accountId Account identifier
     * @return Future completing process
     */
    public Future<JsonObject> getAccount(int accountId) {
        Promise<JsonObject> promise = Promise.promise();
        pgClient.preparedQuery("SELECT amount FROM account WHERE id = $1").execute(Tuple.of(accountId), ar -> {
            if (ar.failed()) {
                promise.fail(ar.cause());
            } else {
                promise.complete(PgResult.rowSetToJsonObject(ar.result()));
            }
        });

        return promise.future();
    }

    /**
     * Add an operation on a specific account.
     * 
     * @param accountId Account identifier
     * @param amount    Amount operation
     * @return Future completing process
     */
    public Future<Void> addOperation(int accountId, int amount) {
        Promise<Void> promise = Promise.promise();
        pgClient.preparedQuery("INSERT INTO operation (amount, account_id) VALUES ($1, $2);")
                .execute(Tuple.of(amount, accountId), ar -> {
                    if (ar.failed()) {
                        promise.fail(ar.cause());
                    } else {
                        promise.complete();
                    }
                });

        return promise.future();
    }
}
