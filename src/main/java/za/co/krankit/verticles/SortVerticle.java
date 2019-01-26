package za.co.krankit.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import za.co.krankit.sorting.Utils;
import za.co.krankit.sorting.BubbleSort;

import java.util.ArrayList;


public class SortVerticle extends AbstractVerticle {
    // The class extends AbstractVerticle. In the Vert.x world, a verticle is a component.
    // By extending AbstractVerticle, our class gets access to the vertx field.

    private final int maxSize = Utils.getMaxSize();
    private static Logger logger = LoggerFactory.getLogger("za.co.krankit.verticles.SortVerticle");

    @Override
    public void start(Future<Void> fut) {
        // The start method is called when the verticle is deployed.
        // If you increase the amount of verticles in the deployment options to ex. .setInstances(10)
        // the start method will be called 10 times, and deploy 10 verticles


        // Create a router object.
        // This object is responsible for dispatching the HTTP requests to the right handler.
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Bind "api/sort/bubble" to our sorting handler
        // It routes requests arriving on "api/sort/bubble" to the given handler.
        router.route().method(HttpMethod.POST)
                .path("/api/sort/bubble")
                .blockingHandler(this::doBubbleSort);

        // Create the HTTP server and pass the router to the request handler.
        vertx.createHttpServer().requestHandler(router)
                .listen(
                        // Retrieve the port from the configuration, default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    /**
     * Handler for the 'api/sort/bubble' route
     * used for processing the requests and writing the result
     *
     * @param routingContext data and info about the request
     */
    private void doBubbleSort(RoutingContext routingContext) {
        logger.info("Got incoming request ");

        // Executes the blocking code using a thread from the worker pool.
        // When the code is complete the handler will be called with the result on the original context
        vertx.executeBlocking(future -> {

                    ArrayList<Integer> numbers = Utils.getIntegers(routingContext.getBodyAsString());
                    // Check MAX_ARRAY_SIZE environment variable
                    if (numbers.size() < maxSize) {
                        // if it's within range then execute bubble sort
                        BubbleSort.sort(numbers);
                        // then call `complete` method on the async handler and pass the result
                        future.complete(numbers);
                    } else {
                        // else if the MAX_ARRAY_SIZE is too large
                        // call `fail` method on the async handler and set error message
                        future.fail("Too large. Max size is " + maxSize);
                    }
                }, result -> {
                    // here we get our response from the async handler
                    // and return the sorted array and a 200 status code if all is well
                    if (result.succeeded()) {
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(result.result()));
                    } else {
                        // else we return a 400 status code
                        routingContext.response()
                                .setStatusCode(400)
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(result.cause().getMessage());
                    }
                }
        );
    }
}