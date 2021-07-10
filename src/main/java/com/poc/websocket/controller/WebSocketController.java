package com.poc.websocket.controller;


import com.poc.websocket.dto.RequestDto;
import com.poc.websocket.dto.ResponseDto;
import com.poc.websocket.handler.GameManager;
import com.poc.websocket.util.SerializeDeserializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.stream.Collectors;

@ServerEndpoint("/tictactoeserver")
public class WebSocketController {

    private final static Logger LOG = LoggerFactory.getLogger(WebSocketController.class);
    private static final GameManager gameManager = GameManager.getInstance();

    public WebSocketController() {
    }

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("WebSocket opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String data, Session session) {
        LOG.info("Data from session {} is {} ", session.getId(), data);
        RequestDto requestDto = SerializeDeserializeUtil.getMessage(data);
        if (requestDto.getMessage().equals("loggingin")) {
            boolean isEntered = gameManager.enterRoom(requestDto.getRoomNo(), session, requestDto.getUserName());
            if (!isEntered) {
                LOG.warn("Game is closed.");
                return;
            }
            char symbol = gameManager.getPlayerSymbol(requestDto.getRoomNo(), session.getId());

            ResponseDto responseDto = new ResponseDto(requestDto.getMessage());
            responseDto.setSymbol(symbol);

            String opponentName = gameManager.getOpponentName(requestDto.getRoomNo(), session.getId());
            if (opponentName != null) {
                responseDto.setOpponentName(opponentName);
                String response = SerializeDeserializeUtil.getResponse(responseDto);
                session.getAsyncRemote().sendText(response);

                //notify the other user as well
                String opponentSessionId = gameManager.getOpponentSessionId(requestDto.getRoomNo(), session.getId());
                Session opponentWebSocketSession = gameManager.getSession(opponentSessionId);
                ResponseDto opponentResponseDto = new ResponseDto(requestDto.getMessage());
                char opponentSymbol = gameManager.getPlayerSymbol(requestDto.getRoomNo(), opponentSessionId);
                opponentResponseDto.setSymbol(opponentSymbol);
                opponentResponseDto.setOpponentName(gameManager.getUserName(session.getId()));
                String opponentResponse = SerializeDeserializeUtil.getResponse(opponentResponseDto);
                opponentWebSocketSession.getAsyncRemote().sendText(opponentResponse);

                Session playerSessionToStartTheGame = gameManager.chooseRandomPlayerToStartTheGame(requestDto.getRoomNo());
                ResponseDto startGameResponse = new ResponseDto("startgame");
                playerSessionToStartTheGame.getAsyncRemote().sendText(SerializeDeserializeUtil.getResponse(startGameResponse));

            } else {
                String response = SerializeDeserializeUtil.getResponse(responseDto);
                session.getAsyncRemote().sendText(response);
            }
        } else if (requestDto.getMessage().equals("playing")) {
            int index = requestDto.getIndex();
            int row = index / 3;
            int col = index % 3;
            if(gameManager.playGame(session.getId(), row, col)){
                // this means that move has been successfully placed
                String winnerSessionId = gameManager.isWinAchieved(session.getId());

                List<String> sessionIds = gameManager.getAllSessionIdAssociated(session.getId());
                if(winnerSessionId != null && !winnerSessionId.isEmpty()){
                    String opponentSessionId = sessionIds.stream().filter(id -> id != session.getId()).collect(Collectors.toList()).get(0);
                    Session opponentWebSocketSession = gameManager.getSession(opponentSessionId);

                    char symbol = gameManager.getPlayerSymbolFromSessionId(session.getId());

                    ResponseDto responseDto = new ResponseDto("winning");
                    responseDto.setWinningMessage("You Win!!");
                    responseDto.setSymbol(symbol);
                    responseDto.setBoardIndex(requestDto.getIndex());
                    session.getAsyncRemote().sendText(SerializeDeserializeUtil.getResponse(responseDto));

                    ResponseDto opponentResponseDto = new ResponseDto("winning");
                    opponentResponseDto.setWinningMessage("You Loose!!");
                    opponentResponseDto.setSymbol(symbol);
                    opponentResponseDto.setBoardIndex(requestDto.getIndex());
                    opponentWebSocketSession.getAsyncRemote().sendText(SerializeDeserializeUtil.getResponse(opponentResponseDto));

                    return;
                } else if(gameManager.isDraw(session.getId())){
                    String opponentSessionId = sessionIds.stream().filter(id -> id != session.getId()).collect(Collectors.toList()).get(0);
                    Session opponentWebSocketSession = gameManager.getSession(opponentSessionId);
                    char symbol = gameManager.getPlayerSymbolFromSessionId(session.getId());

                    ResponseDto responseDto = new ResponseDto("draw");
                    responseDto.setWinningMessage("Match Draw!!");
                    responseDto.setSymbol(symbol);
                    responseDto.setBoardIndex(requestDto.getIndex());
                    session.getAsyncRemote().sendText(SerializeDeserializeUtil.getResponse(responseDto));

                    ResponseDto opponentResponseDto = new ResponseDto("draw");
                    opponentResponseDto.setWinningMessage("Match Draw!!");
                    opponentResponseDto.setSymbol(symbol);
                    opponentResponseDto.setBoardIndex(requestDto.getIndex());
                    opponentWebSocketSession.getAsyncRemote().sendText(SerializeDeserializeUtil.getResponse(opponentResponseDto));

                }
                ResponseDto responseDto = new ResponseDto(requestDto.getMessage());
                responseDto.setSymbol(gameManager.getPlayerSymbolFromSessionId(session.getId()));
                responseDto.setBoardIndex(requestDto.getIndex());
                String response = SerializeDeserializeUtil.getResponse(responseDto);

                Session webSocketSession = gameManager.getSession(sessionIds.get(0));
                Session opponentWebSocketSession = gameManager.getSession(sessionIds.get(1));

                webSocketSession.getAsyncRemote().sendText(response);
                opponentWebSocketSession.getAsyncRemote().sendText(response);
            }
        }
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        LOG.info("Closing a WebSocket session {} due to {} ", session.getId(), reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable t) {
        LOG.info("Got error message from session {} and ,message {}", session.getId(), t.getMessage());
    }

}
