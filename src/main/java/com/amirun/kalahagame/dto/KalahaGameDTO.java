package com.amirun.kalahagame.dto;

import com.amirun.kalahagame.model.Player;
import com.amirun.kalahagame.model.Result;

public record KalahaGameDTO (
    String gameId,
    int[] player1Pits,
    int player1BigPit,
    int[] player2Pits,
    int player2BigPit,
    Boolean isGameEnded,
    Player nextTurn,
    Result winner
){
}