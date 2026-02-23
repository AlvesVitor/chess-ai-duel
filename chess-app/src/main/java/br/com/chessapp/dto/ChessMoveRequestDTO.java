package br.com.chessapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChessMoveRequestDTO {
    public String board;
    public String color;
    public String model;
    public List<String> history;

    @JsonProperty("valid_moves")
    public List<String> validMoves;
    @JsonProperty("in_check")
    public boolean inCheck;

    public ChessMoveRequestDTO(String board, String color, String model,
                               List<String> history, List<String> validMoves, boolean inCheck) {
        this.board = board;
        this.color = color;
        this.model = model;
        this.history = history;
        this.validMoves = validMoves;
        this.inCheck = inCheck;
    }
}
