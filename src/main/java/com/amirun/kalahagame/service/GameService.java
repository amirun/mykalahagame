package com.amirun.kalahagame.service;

import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.model.KalahaGame;

public interface GameService {
    KalahaGame move(KalahaGame game, Character pitId) throws InvalidMoveException;
}
