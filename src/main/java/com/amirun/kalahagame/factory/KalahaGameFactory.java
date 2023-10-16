package com.amirun.kalahagame.factory;

import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.amirun.kalahagame.constants.GameConstants.*;

@Component
public class KalahaGameFactory {

    @Value("${kalaha.config.pitSize.default}")
    private int defaultPitSize;

    /**
     * Creates a KalahaGame with specified number of stones in pit and can create a game where players can choose who goes first.
     * @param pitStones number of pit stones per pit when starting a new game.
     * @param nextTurn determine who goes first.
     * @return KalahaGame
     */
    public KalahaGame getGame(int pitStones, Player nextTurn){
        int[] board = new int[TOTAL_PIT_COUNT];
        for (int i = PLAYER1_PIT_START_INDEX_INCL; i < TOTAL_PIT_COUNT; i++) {
            if (i != PLAYER1_BIG_PIT_INDEX && i != PLAYER2_BIG_PIT_INDEX) { // Skip the big pits
                board[i] = pitStones;
            }
        }
        return KalahaGame.builder().gamePits(board).isGameEnded(false).nextTurn(nextTurn).build();
    }

    /**
     * Generates a KalahaGame object with the default number of stones and turn set to Player1. Change value of 'kalaha.config.pitSize.default' to set initial pit size.
     * @return KalahaGame A new Kalaha game with 6 pebbles per pit
     */
    public KalahaGame createDefaultGame() {
        return getGame(defaultPitSize, Player.PLAYER_1);
    }

}
