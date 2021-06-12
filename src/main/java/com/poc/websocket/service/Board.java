package com.poc.websocket.service;

public class Board {

    private final int[][] mat;
    private int[] rows;
    private int[] cols;
    private int diagonal = 0;
    private int revDiagonal = 0;

    public Board(){
        mat = new int[3][3];
        rows = new int[3];
        cols = new int[3];
    }

    public boolean move(int symbol, int row, int col){
        if(!isSafe(row, col)){
            return false;
        }

        int n = mat.length;
        mat[row][col] = symbol;

        rows[row] += symbol;
        cols[col] += symbol;

        if(row == col){
            diagonal += symbol;
        }
        if(row == n- 1- col){
            revDiagonal += symbol;
        }

        if(Math.abs(rows[row]) == n || Math.abs(cols[col]) == n || Math.abs(diagonal) == n || Math.abs(revDiagonal) == n)
            return true;

        return false;
    }
    private boolean isSafe(int row, int col){
        if(row < 0 || row >= mat.length || col < 0 || col >= mat[0].length || mat[row][col] != 0)
            return false;
        return true;
    }
}
