package com.amirun.kalahagame.service;

import com.amirun.kalahagame.constants.GameConstants;
import com.amirun.kalahagame.dto.KalahaGameDTO;
import com.amirun.kalahagame.exception.GameNotFoundException;
import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.factory.KalahaGameFactory;
import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.amirun.kalahagame.constants.GameConstants.*;

@Service
@RequiredArgsConstructor
public class KalahaServiceImpl implements KalahaService {
    private final GameRepository repository;
    private final KalahaGameFactory factory;
    private final GameService gameService;

    private final Logger logger = LoggerFactory.getLogger(KalahaServiceImpl.class);

    @Override
    public KalahaGameDTO newGame() {
        KalahaGame game = repository.save(factory.createDefaultGame());
        logger.info("New game created. GameID:{}", game.getGameId());
        return gameToDto(game);
    }

    @Transactional
    @Override
    public KalahaGameDTO play(String gameId, Character pitId) throws GameNotFoundException, InvalidMoveException {
        logger.info("Playing move. GameID:{}, pitId:{}", gameId, pitId);
        if(pitId < 'A' || pitId > 'F') {
            throw new InvalidMoveException("Choose a pit between A - F.");
        }
        KalahaGame game = getGame(gameId);
        KalahaGame newState = gameService.move(game, pitId);
        return gameToDto(repository.save(newState));
    }

    @Override
    public KalahaGameDTO getGameDTO(String gameId) throws GameNotFoundException {
        return gameToDto(getGame(gameId));
    }

    @Override
    public KalahaGame getGame(String gameId) throws GameNotFoundException {
        logger.info("Fetching game by id:{}", gameId);
        return repository.findById(gameId).orElseThrow( () -> {
            logger.error("Invalid Id: {}", gameId);
            return new GameNotFoundException("Invalid Id: " + gameId);
        });
    }

    public KalahaGameDTO gameToDto(KalahaGame game){
        return new KalahaGameDTO(game.getGameId(), Arrays.copyOfRange(game.getGamePits(), PLAYER1_PIT_START_INDEX_INCL, PLAYER1_PIT_END_INDEX_EXCL),
                game.getGamePits()[GameConstants.PLAYER1_PIT_END_INDEX_EXCL],
                Arrays.copyOfRange(game.getGamePits(), PLAYER2_PIT_START_INDEX_INCL, PLAYER2_PIT_END_INDEX_EXCL),
                game.getGamePits()[GameConstants.PLAYER2_PIT_END_INDEX_EXCL],
                game.getIsGameEnded(),
                game.getNextTurn(),
                game.getWinner());

    }
}
