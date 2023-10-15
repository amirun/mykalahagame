package com.amirun.kalahagame.service;

import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.factory.KalahaGameFactory;
import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.model.Player;
import com.amirun.kalahagame.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceImplUT {

    private GameServiceImpl gameService;
    private KalahaGameFactory factory;

    @BeforeEach
    public void setUp() {
        gameService = new GameServiceImpl();
        factory = new KalahaGameFactory();
        ReflectionTestUtils.setField(factory, "defaultPitSize", 6);
    }

    @Test
    void testMove() {
        KalahaGame game = factory.createDefaultGame();
        int[] state1 = {0,7,7,7,7,7,1,6,6,6,6,6,6,0};
        int[] state2 = {0,7,7,0,8,8,2,7,7,7,7,6,6,0};
        int[] state3 = {1,8,8,1,8,8,2,7,7,7,0,7,7,1};

        assertDoesNotThrow(() ->gameService.move(game, 'A'));
        assertArrayEquals(state1, game.getGamePits());

        assertDoesNotThrow(() ->gameService.move(game, 'D'));
        assertEquals(Player.PLAYER_2, game.getNextTurn());
        assertArrayEquals(state2, game.getGamePits());

        assertDoesNotThrow(() ->gameService.move(game, 'D'));
        assertEquals(Player.PLAYER_1, game.getNextTurn());
        assertArrayEquals(state3, game.getGamePits());

    }

    @Test
    void testMove_EndGame() {
        KalahaGame game = factory.createDefaultGame();
        int[] p2Win = {2, 3, 2, 1, 1, 1, 22, 0, 0, 0, 0, 0, 1, 39};
        int[] stateEnd = {2, 3, 2, 1, 1, 1, 22, 0, 0, 0, 0, 0, 0, 40};
        game.setGamePits(p2Win);
        game.setNextTurn(Player.PLAYER_2);

        assertDoesNotThrow(() ->gameService.move(game, 'F'));
        assertEquals(Result.PLAYER_2, game.getWinner());
        assertArrayEquals(stateEnd, game.getGamePits());
        assertEquals(true, game.getIsGameEnded());
    }

    @Test
    void testMove_Exception() {
        KalahaGame game = factory.createDefaultGame();
        int[] state1 = {0,7,7,7,7,7,1,6,6,6,6,6,6,0};
        game.setGamePits(state1);
        assertThrows(InvalidMoveException.class, () -> gameService.move(game, 'A'));

    }

    @Test
    public void testCaptureStones() {
        int[] pits =     {1, 1, 1, 1, 7, 1, 26, 1, 0, 0, 7, 5, 0, 21};
        int[] stateEnd = {1, 1, 1, 1, 0, 1, 26, 0, 0, 0, 7, 5, 0, 29};
        KalahaGame game = factory.createDefaultGame();
        game.setGamePits(pits);
        game.setNextTurn(Player.PLAYER_2);

        assertDoesNotThrow(() ->gameService.move(game, 'A'));
        assertArrayEquals(stateEnd, game.getGamePits());

    }

    @Test
    void testGetPlayerBigPitForPlayer1() {
        int expectedBigPit1 = 6;  // The expected big pit index for PLAYER_1
        int actualBigPit1 = gameService.getPlayerBigPit(Player.PLAYER_1);
        assertEquals(expectedBigPit1, actualBigPit1);

        int expectedBigPit2 = 13;  // The expected big pit index for PLAYER_1
        int actualBigPit2 = gameService.getPlayerBigPit(Player.PLAYER_2);
        assertEquals(expectedBigPit2, actualBigPit2);
    }

    @Test
    void testGetOpponentBigPitForPlayers() {
        int expectedBigPit1 = 13;  // The expected big pit index for PLAYER_1's opponent
        int actualBigPit1 = gameService.getOpponentBigPit(Player.PLAYER_1);
        assertEquals(expectedBigPit1, actualBigPit1);

        int expectedBigPit2 = 6;  // The expected big pit index for PLAYER_2's opponent
        int actualBigPit2 = gameService.getOpponentBigPit(Player.PLAYER_2);
        assertEquals(expectedBigPit2, actualBigPit2);
    }

    @Test
    void testGetPitIndexForPlayer1() {
        char pitId1 = 'B'; // Adjust this to a valid pit ID for PLAYER_1
        int expectedIndex1 = 1; // The expected index for 'I' when currentPlayer is PLAYER_1
        int actualIndex1 = gameService.getPitIndex(Player.PLAYER_1, pitId1);

        assertEquals(expectedIndex1, actualIndex1);

        char pitId2 = 'D';  // Adjust this to a valid pit ID for PLAYER_2
        int expectedIndex2 = 10;  // The expected index for 'I' when currentPlayer is PLAYER_2
        int actualIndex2 = gameService.getPitIndex(Player.PLAYER_2, pitId2);

        assertEquals(expectedIndex2, actualIndex2);
    }

    @Test
    void testIsGameOver() {
        int[] pits = {0, 0, 0, 0, 0, 0, 30, 1, 1, 1, 1, 1, 1, 30};
        assertTrue(gameService.isGameOver(pits));
    }


    @Test
    void testGetWinner() {
        int[] pits1Win = {0, 0, 0, 0, 0, 0, 40, 1, 1, 5, 1, 1, 1, 22};
        int[] pits2Win = {2, 3, 2, 1, 1, 1, 22, 0, 0, 0, 0, 0, 0, 40};
        int[] tiePits  = {4, 4, 1, 0, 2, 2, 23, 0, 0, 0, 0, 0, 0, 36};

        assertEquals(Result.PLAYER_1, gameService.getWinner(pits1Win));
        assertEquals(Result.PLAYER_2, gameService.getWinner(pits2Win));
        assertEquals(Result.TIE, gameService.getWinner(tiePits));
    }

}