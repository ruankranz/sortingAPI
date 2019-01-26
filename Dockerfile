###
# 
# To build:
#  docker build -t demo/krankit-vertx .
# To run:
#   docker run -p 8080:8080 demo/krankit-vertx
###

# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME za.co.krankit.SortVerticle
ENV VERTICLE_FILE target/bubblesort-1.0-SNAPSHOT-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles
ENV MAX_ARRAY_SIZE 10000

EXPOSE 8080

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]