package com.amirun.kalahagame.service;


import com.amirun.kalahagame.dto.KalahaGameDTO;
import com.amirun.kalahagame.exception.GameNotFoundException;
import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.model.KalahaGame;

public interface KalahaService {
    KalahaGameDTO newGame();

    KalahaGameDTO play(String gameId, Character pitId) throws GameNotFoundException, InvalidMoveException;

    KalahaGameDTO getGameDTO(String gameId) throws GameNotFoundException;

    KalahaGame getGame(String gameId) throws GameNotFoundException;
}
