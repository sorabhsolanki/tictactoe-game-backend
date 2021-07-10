package com.poc.websocket;

import com.poc.websocket.controller.AdminController;
import com.poc.websocket.controller.WebSocketController;
import com.poc.websocket.util.PropertyReader;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

public class TicTacToeMainApp extends Application<TicTacToeServiceConfiguration> {

    @Override
    public void initialize(final Bootstrap<TicTacToeServiceConfiguration> bootstrap) {
    }

    @Override
    public void run(TicTacToeServiceConfiguration ticTacToeServiceConfiguration, Environment environment) {
        PropertyReader.init();
        environment.jersey().register(new AdminController());
        WebSocketServerContainerInitializer.configure(environment.getApplicationContext(), (servletContext, serverContainer) -> {
            serverContainer.setDefaultMaxTextMessageBufferSize(200);
            serverContainer.addEndpoint(WebSocketController.class);
        });
    }

    public static void main(String[] args) throws Exception {
        new TicTacToeMainApp().run(args);
    }
}
