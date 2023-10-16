package com.amirun.kalahagame.controller;

import com.amirun.kalahagame.dto.KalahaGameDTO;
import com.amirun.kalahagame.exception.GameNotFoundException;
import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.service.KalahaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@CrossOrigin
public class GameController {

    private final KalahaService service;

    @Operation(summary = "Create a new Kalaha game.", description = "Create a new Kalaha game and return the initial board.")
    @ApiResponse(
            responseCode = "200",
            description = "New game created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = KalahaGameDTO.class)
            )
    )
    @GetMapping("/new")
    public ResponseEntity<KalahaGameDTO> getNewBoard() {
        return ResponseEntity.ok(service.newGame());
    }

    @Operation(summary = "Get Kalaha game by ID", description = "Retrieve a Kalaha game by its unique ID.")
    @ApiResponse(
            responseCode = "200",
            description = "Returns existing game",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = KalahaGameDTO.class)
            )
    )
    @GetMapping("/{gameId}")
    public ResponseEntity<KalahaGameDTO> getGame(@PathVariable @NotNull(message = "valid game id required.") String gameId) throws GameNotFoundException {
        return ResponseEntity.ok(service.getGameDTO(gameId));
    }


    @Operation(summary = "Play a move in the specified Kalaha game.", description = "Play a move by specifying the game ID and pit ID. Returns the new board status.")
    @ApiResponse(
            responseCode = "200",
            description = "Returns the new board status after the specified.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = KalahaGameDTO.class)
            )
    )
    @PatchMapping("/playGame/{gameId}/playPit/{pitId}")
    public ResponseEntity<KalahaGameDTO> playGame(@PathVariable @NotBlank(message = "valid game id required.")
                                                          String gameId,
                                                  @PathVariable @NotNull(message = "Select a valid pit.")
                                                          Character pitId) throws GameNotFoundException, InvalidMoveException {
        return ResponseEntity.ok(service.play(gameId, pitId));
    }
}
