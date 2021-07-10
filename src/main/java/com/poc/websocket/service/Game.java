package com.poc.websocket.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Game {

    private final String gameId;
    private final Board board;
    private final Map<String, Pair<Integer, Character>> symbolMap;

    private String firstSessionId;
    private String secondSessionId;
    private int currentMove; // check whose move it is.
    private String winner;

    public Game() {
        this.gameId = UUID.randomUUID().toString();
        this.board = new Board();
        this.symbolMap = new HashMap<>();
    }

    public void addSession(final String session){
        if(firstSessionId == null){
            firstSessionId = session;
            symbolMap.put(firstSessionId, new Pair(1, 'X'));
        } else {
            secondSessionId = session;
            symbolMap.put(secondSessionId, new Pair(-1, 'O'));
        }
    }

    public String getRandomSessionForFirstMove(){
        String sessionId = symbolMap.keySet().stream().findFirst().get();
        currentMove = symbolMap.get(sessionId).first;
        return sessionId;
    }

    public boolean placeMove(final String sessionId, int row, int col){
        int symbol = symbolMap.get(sessionId).first;
        if(symbol != currentMove || winner != null){ //either the user is not the current move user or winner is already declared.
            return false;
        }
        boolean win = board.move(symbol, row, col); //current session wins the game
        currentMove = -currentMove;
        if(win){
            winner = sessionId;
        }
        return true; // this means that the move has been placed
    }

    public String getWinner() {
        return winner;
    }

    public boolean isDraw(){
        return board.isDraw();
    }

    public char getSymbol(final String sessionId){
        return symbolMap.get(sessionId).second;
    }

    public String getFirstSessionId() {
        return firstSessionId;
    }

    public String getSecondSessionId() {
        return secondSessionId;
    }

    private static class Pair<T, U>{
        T first;
        U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    public String getGameId() {
        return gameId;
    }

    public int getCurrentMove() {
        return currentMove;
    }
}
