package com.poc.websocket.entity;

import com.poc.websocket.service.Game;

import javax.websocket.Session;

public class UserSessionEntity {

    private final String userName;
    private final Session websocketSession;
    private final Game game;

    public UserSessionEntity(String userName, Session websocketSession, Game game) {
        this.userName = userName;
        this.websocketSession = websocketSession;
        this.game = game;
    }

    public String getUserName() {
        return userName;
    }

    public Session getWebsocketSession() {
        return websocketSession;
    }

    public Game getGame() {
        return game;
    }
}
