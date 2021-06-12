package com.poc.websocket;

import com.poc.websocket.controller.WebSocketController;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class TicTacToeMainApp {

    private void startServer() throws Exception {
        // Expose Prometheus metrics.
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8002);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        try {
            // Initialize javax.websocket layer
            WebSocketServerContainerInitializer.configure(context,
                    (servletContext, wsContainer) ->
                    {
                        // This lambda will be called at the appropriate place in the
                        // ServletContext initialization phase where you can initialize
                        // and configure  your websocket container.
                        // Configure defaults for container
                        wsContainer.setDefaultMaxTextMessageBufferSize(65535);
                        // Add WebSocket endpoint to javax.websocket layer
                        wsContainer.addEndpoint(WebSocketController.class);
                    });

            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) throws Exception {
        TicTacToeMainApp ticTacToeMainApp = new TicTacToeMainApp();
        ticTacToeMainApp.startServer();
    }
}
