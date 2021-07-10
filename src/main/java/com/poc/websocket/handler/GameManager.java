package com.poc.websocket.handler;

import com.poc.websocket.entity.UserSessionEntity;
import com.poc.websocket.service.Game;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {

    private static final GameManager GAME_MANAGER = new GameManager();

    //it will contains room to list of sessionIds, one room can contain at max 2 session ids
    private final Map<String, List<String>> roomMap;
    //it will contains room to Game object
    private final Map<String, Game> gameMap;
    //it will contain sessionId to userSessionEntity
    private final Map<String, UserSessionEntity> userSessionEntityMap;

    private GameManager() {
        this.roomMap = new ConcurrentHashMap<>();
        this.gameMap = new ConcurrentHashMap<>();
        this.userSessionEntityMap = new ConcurrentHashMap<>();
    }

    public static GameManager getInstance() {
        return GAME_MANAGER;
    }

    // will enter the current user's session into the room, also will add the userName to sessionToUserMap
    public boolean enterRoom(final String roomNo, final Session session, final String userName) {
        List<String> sessionIds = roomMap.getOrDefault(roomNo, new ArrayList<>());
        if (sessionIds.size() >= 2) {
            return false;
        }
        sessionIds.add(session.getId());
        roomMap.put(roomNo, sessionIds);

        Game game = gameMap.getOrDefault(roomNo, new Game());
        game.addSession(session.getId());
        gameMap.put(roomNo, game);

        userSessionEntityMap.put(session.getId(), new UserSessionEntity(userName, session, game));
        return true;
    }

    public String getOpponentName(final String roomNo, final String sessionId) {
        if (!roomMap.containsKey(roomNo) || roomMap.get(roomNo).size() < 2)
            return null;
        List<String> opponentSessionIds = roomMap.get(roomNo).stream().filter(id -> id != sessionId).collect(Collectors.toList());
        if (opponentSessionIds.isEmpty())
            return null;
        String opponentSessionId = opponentSessionIds.get(0);
        return userSessionEntityMap.get(opponentSessionId).getUserName();
    }

    public String getOpponentSessionId(final String roomNo, final String sessionId) {
        if (!roomMap.containsKey(roomNo))
            return null;
        List<String> opponentSessionIds = roomMap.get(roomNo).stream().filter(id -> id != sessionId).collect(Collectors.toList());
        if (opponentSessionIds.isEmpty())
            return null;
        return opponentSessionIds.get(0);
    }

    public String getUserName(final String sessionId) {
        return userSessionEntityMap.get(sessionId).getUserName();
    }

    public Session getSession(final String sessionId) {
        return userSessionEntityMap.get(sessionId).getWebsocketSession();
    }

    public char getPlayerSymbol(final String roomNo, final String sessionId) {
        return gameMap.get(roomNo).getSymbol(sessionId);
    }

    public boolean playGame(final String sessionId, final int row, final int col) {
        Game game = userSessionEntityMap.get(sessionId).getGame();
        if (game == null)
            return false;
        return game.placeMove(sessionId, row, col);
    }

    public String isWinAchieved(final String sessionId) {
        Game game = userSessionEntityMap.get(sessionId).getGame();
        if (game == null)
            return null;
        return game.getWinner();
    }

    public boolean isDraw(final String sessionId) {
        Game game = userSessionEntityMap.get(sessionId).getGame();
        if (game == null)
            return false;
        return game.isDraw();
    }

    public List<String> getAllSessionIdAssociated(final String sessionId) {
        Game game = userSessionEntityMap.get(sessionId).getGame();
        List<String> sessionIds = new ArrayList<>();
        sessionIds.add(game.getFirstSessionId());
        sessionIds.add(game.getSecondSessionId());
        return sessionIds;
    }

    public char getPlayerSymbolFromSessionId(final String sessionId) {
        Game game = userSessionEntityMap.get(sessionId).getGame();
        return game.getSymbol(sessionId);
    }

    public Session chooseRandomPlayerToStartTheGame(final String roomNo) {
        Game game = gameMap.get(roomNo);
        String sessionId = game.getRandomSessionForFirstMove();
        return getSession(sessionId);
    }


    public List<String> getAllRooms() {
        return roomMap.keySet().stream().collect(Collectors.toList());
    }

    public Game getGameFromRoom(final String room) {
        return gameMap.get(room);
    }

    public UserSessionEntity getPlayer(final String sessionId) {
        return userSessionEntityMap.get(sessionId);
    }

    public void closeGame(final String roomNo) {
        List<String> sessionIds = roomMap.get(roomNo);
        List<UserSessionEntity> userSessionEntities = null;
        if (sessionIds != null) {
            userSessionEntities = sessionIds.stream().map(userSessionEntityMap::get).collect(Collectors.toList());
        }
        if (userSessionEntities != null) {
            userSessionEntities.stream().forEach(userSessionEntity -> {
                try {
                    userSessionEntity.getWebsocketSession().close();
                } catch (IOException e) {
                    System.out.println("Error : " + e.getMessage());
                }
            });
        }
        sessionIds.stream().forEach(session -> userSessionEntityMap.remove(session));
        roomMap.remove(roomNo);
        gameMap.remove(roomNo);
    }

}
