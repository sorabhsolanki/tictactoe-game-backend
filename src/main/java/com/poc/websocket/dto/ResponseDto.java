package com.poc.websocket.dto;

public class ResponseDto {

    private String message;
    private String opponentName;

    private char symbol;

    private int boardIndex;

    private String winningMessage;

    public ResponseDto() {
    }

    public ResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getBoardIndex() {
        return boardIndex;
    }

    public void setBoardIndex(int boardIndex) {
        this.boardIndex = boardIndex;
    }

    public String getWinningMessage() {
        return winningMessage;
    }

    public void setWinningMessage(String winningMessage) {
        this.winningMessage = winningMessage;
    }
}
