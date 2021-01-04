package fr.sledunois.pgas.controller;

import fr.sledunois.pgas.service.AccountService;
import fr.sledunois.pgas.utils.HttpUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;

public class AccountController {

    private Logger log = LoggerFactory.getLogger(AccountController.class);
    private AccountService accountService;

    public AccountController(Router router, PgPool pgClient) {
        accountService = new AccountService(pgClient);

        router.post("/accounts").handler(this::postAccount);
        router.get("/accounts/:id").handler(this::getAccount);
        router.post("/accounts/:id/operations").handler(this::postOperation);
    }

    private void getAccount(RoutingContext ctx) {
        try {
            int accountId = Integer.parseInt(ctx.request().getParam("id"));
            accountService.getAccount(accountId).onSuccess(res -> HttpUtils.ok(ctx.response(), res))
                    .onFailure(err -> HttpUtils.internalServerError(ctx.response(), err.getMessage()));
        } catch (NumberFormatException e) {
            HttpUtils.internalServerError(ctx.response(), "Unable to parse account identifier");
        }
    }

    private void postAccount(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();

        // Get account amount. In case of null amount, get 0
        accountService.createAccount(body.getInteger("amount", 0))
                .onSuccess(acc -> HttpUtils.created(ctx.response(), acc))
                .onFailure(err -> HttpUtils.internalServerError(ctx.response(), err.getMessage()));
    }

    private void postOperation(RoutingContext ctx) {
        try {
            int accountId = Integer.parseInt(ctx.request().getParam("id"));
            JsonObject body = ctx.getBodyAsJson();
            Integer operationAmount = body.getInteger("amount");
            log.info(String.format("Operation amount for %d account: %d", accountId, operationAmount));
            accountService.addOperation(accountId, operationAmount)
                    .onSuccess(res -> HttpUtils.noContent(ctx.response()))
                    .onFailure(err -> HttpUtils.internalServerError(ctx.response(), err.getMessage()));
        } catch (NumberFormatException e) {
            log.error(String.format("Unable to parse account identifier. Expected integer, found %s",
                    ctx.request().getParam("id")), e);
            HttpUtils.internalServerError(ctx.response(), "Unable to parse account identifier");
        }
    }

}
