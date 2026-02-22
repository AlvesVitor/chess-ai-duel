package br.com.chessapp;

import br.com.chessapp.dto.ChessMoveRequestDTO;
import br.com.chessapp.dto.ChessMoveResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChessAIClient {
    private static final String API_URL = "http://localhost:8000/move";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String[] getMove(String board, String color, String model,
                            List<String> history, List<String> validMoves) throws Exception {

        ChessMoveRequestDTO req = new ChessMoveRequestDTO(board, color, model, history, validMoves);
        String json = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API retornou status: " + response.statusCode());
        }

        ChessMoveResponseDTO res = mapper.readValue(response.body(), ChessMoveResponseDTO.class);

        String[] parts = res.move.split(" ");
        if (parts.length != 2) {
            throw new Exception("Formato de movimento inválido: " + res.move);
        }

        return parts;
    }
}
