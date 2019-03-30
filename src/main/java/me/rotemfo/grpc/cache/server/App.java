package me.rotemfo.grpc.cache.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final int port = 8080;

    public static void main(String[] args) throws Exception {
        final Server server = ServerBuilder.forPort(port).addService(new CacheServiceImpl()).build();
        server.start();
        logger.info("Server started, listening on port {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may has been reset by its JVM shutdown hook.
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.error("*** server shut down");
        }));
        server.awaitTermination();
    }
}