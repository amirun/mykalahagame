package com.amirun.kalahagame.service;

import com.amirun.kalahagame.dto.KalahaGameDTO;
import com.amirun.kalahagame.exception.GameNotFoundException;
import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.factory.KalahaGameFactory;
import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.model.Player;
import com.amirun.kalahagame.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KalahaServiceImplUT {

    @InjectMocks
    private KalahaServiceImpl kalahaService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private KalahaGameFactory factory;

    @Mock
    private GameService gameService;


    @Test
    public void testNewGame() {
        // Mock the behavior of GameRepository and KalahaGameFactory
        KalahaGame game = createGame() ;
        game.setGameId("SAMPLE_ID");
        when(factory.createDefaultGame()).thenReturn(game);
        when(gameRepository.save(any(KalahaGame.class))).thenReturn(game);

        KalahaGameDTO gameDTO = kalahaService.newGame();

        // Verify that the returned DTO matches the game's initial state
        int[] expectedPits = new int[]{6,6,6,6,6,6};
        int expectedBigPitValue = 0;
        assertArrayEquals(expectedPits, gameDTO.player1Pits());
        assertArrayEquals(expectedPits, gameDTO.player2Pits());
        assertEquals(expectedBigPitValue, gameDTO.player1BigPit());
        assertEquals(expectedBigPitValue, gameDTO.player2BigPit());
        assertEquals(Player.PLAYER_1, gameDTO.nextTurn());

    }

    @Test
    public void testPlay() throws InvalidMoveException {
        // Mock the behavior of GameRepository, GameService, and KalahaGameFactory
        KalahaGame game = createGame() ;
        String gameId = game.getGameId();
        char pitId = 'A';

        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        int[] state1 = {0,7,7,7,7,7,1,6,6,6,6,6,6,0};
        game.setGamePits(state1);

        when(gameService.move(game, pitId)).thenReturn(game);
        when(gameRepository.save(game)).thenReturn(game);

        KalahaGameDTO gameDTO = assertDoesNotThrow(() -> kalahaService.play(gameId, pitId));


        int[] expectedPits1 = new int[]{0,7,7,7,7,7};
        int[] expectedPits2 = new int[]{6,6,6,6,6,6};
        int expectedBigPitValue1 = 1;
        int expectedBigPitValue2 = 0;
        assertArrayEquals(expectedPits1, gameDTO.player1Pits());
        assertArrayEquals(expectedPits2, gameDTO.player2Pits());
        assertEquals(expectedBigPitValue1, gameDTO.player1BigPit());
        assertEquals(expectedBigPitValue2, gameDTO.player2BigPit());
        assertEquals(Player.PLAYER_1, gameDTO.nextTurn());

    }

    @Test
    public void testGetGame() {
        KalahaGame expectedGame = createGame() ;
        String gameId = expectedGame.getGameId();
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(expectedGame));

        KalahaGame game = assertDoesNotThrow(() ->kalahaService.getGame(gameId));

        // Verify that the returned game matches the expected game
        assertEquals(expectedGame, game);
    }

    @Test
    public void testGetGameGameNotFoundException() {
        // Mock the behavior of GameRepository
        String gameId = "nonExistentGameId";
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        assertThrows(GameNotFoundException.class, () -> kalahaService.getGame(gameId));
    }

    KalahaGame createGame(){
        return KalahaGame.builder()
                .nextTurn(Player.PLAYER_1)
                .isGameEnded(false)
                .gamePits(new int[] {6,6,6,6,6,6,0,6,6,6,6,6,6,0})
                .gameId("SAMPLE_ID")
                .build();
    }
}