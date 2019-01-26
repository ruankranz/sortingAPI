package za.co.krankit;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import za.co.krankit.verticles.SortVerticle;

public class Main {

    private static Logger logger = LoggerFactory.getLogger("za.co.krankit.Main");

    // This class deploys our SortVerticle
    public static void main (String[] args) {
        Vertx vertx;
        vertx = Vertx.vertx();

        // Here we can set more customizable options as we scale the application
        // For now we will deploy 2 verticles, but this could be scaled to thousands
        // and could even be clustered across multiple servers/nodes
        DeploymentOptions options = new DeploymentOptions()
                .setInstances(2)
                .setConfig(new JsonObject().put("http.port", 8080)
                );

        // Pass the options to the deployVerticle method.
        vertx.deployVerticle(SortVerticle.class.getName(), options);
        logger.info("Verticles have been deployed");
    }

}
