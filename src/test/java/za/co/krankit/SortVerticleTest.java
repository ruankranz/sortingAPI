package za.co.krankit;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import za.co.krankit.sorting.Utils;
import za.co.krankit.verticles.SortVerticle;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;

/**
 * This is our JUnit test for our verticle. The test uses vertx-unit, so we declare a custom runner.
 */
@RunWith(VertxUnitRunner.class)
public class SortVerticleTest {

    private Vertx vertx;
    private Integer port;

    private final int maxSize = Utils.getMaxSize();

    /**
     * Before executing our test, let's deploy our verticle.
     *
     * This method instantiates a new Vertx and deploy the verticle. Then, it waits until the verticle has successfully
     * completed its start sequence (thanks to `context.asyncAssertSuccess`).
     *
     * @param context the test context.
     */
    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        // We create deployment options and set the _configuration_ json object:
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );

        // We pass the options as the second parameter of the deployVerticle method.
        vertx.deployVerticle(SortVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    /**
     * This method, called after our test, just cleanup everything by closing the vert.x instance
     *
     * @param context the test context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /**
     * Test with a simple non-nested array
     *
     * @param context the test context
     */
    @Test
    public void testSortWithSimpleArray(TestContext context) {
        // This test is asynchronous, so get an async handler to inform the test when we are done.
        Async async = context.async();
        final String json = "[99, 2, 4, 1, 55, 0]";

        // We create a HTTP client and query our application.
        vertx.createHttpClient().post(port, "localhost", "/api/sort/bubble")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    // When we get the response we check if it matches the expected sorted result.
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        JsonArray sortedArr = body.toJsonArray();
                        // Assertions are made on the 'context' object and are not Junit assert.
                        // This ways it manage the async aspect of the test the right way.
                        context.assertEquals(sortedArr.toString(), "[0,1,2,4,55,99]");
                        // Then, we call the `complete` method on the async handler
                        // to declare this async (and here the test) done.
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }


    /**
     * Test with a nested array which has a nil value
     *
     * @param context the test context
     */
    @Test
    public void testSortWithNestedArray(TestContext context) {
        Async async = context.async();
        final String json = "[99, [2, 4, nil], [[55]], 0]";

        vertx.createHttpClient().post(port, "localhost", "/api/sort/bubble")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        JsonArray sortedArr = body.toJsonArray();
                        context.assertEquals(sortedArr.toString(), "[0,2,4,55,99]");
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }


    /**
     * Test an array with a size larger or equal to the MAX_ARRAY_SIZE
     *
     * @param context the test context
     */
    @Test
    public void testMaxArraySizeConstraint(TestContext context) {
        Async async = context.async();

        // Generate a huge array
        int[] bubbleArray = new int[maxSize];
        Random rand = new Random();
        for (int i = 0; i < maxSize; i++) {
            int random = rand.nextInt(maxSize);
            bubbleArray[i] = random;
        }

        final String json = Arrays.toString(bubbleArray);

        vertx.createHttpClient().post(port, "localhost", "/api/sort/bubble")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 400);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        context.assertTrue(body.toString().contains("Too large"));
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }

    /**
     * Test an array with a large size
     *
     * @param context the test context
     */
    @Test
    public void testLargeArray(TestContext context) {
        Async async = context.async();

        // Generate a huge array
        int[] bubbleArray = new int[9999];
        Random rand = new Random();
        for (int i = 0; i < 9999; i++) {
            int random = rand.nextInt(9999);
            bubbleArray[i] = random;
        }

        final String json = Arrays.toString(bubbleArray);
        Arrays.sort(bubbleArray);
        String result = Json.encodePrettily(bubbleArray);

        vertx.createHttpClient().post(port, "localhost", "/api/sort/bubble")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", Integer.toString(json.length()))
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 200);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        context.assertEquals(body.toString(), result);
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}