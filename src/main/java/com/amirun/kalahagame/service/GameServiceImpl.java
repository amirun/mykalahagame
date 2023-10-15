package com.amirun.kalahagame.service;

import com.amirun.kalahagame.exception.InvalidMoveException;
import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.model.Player;
import com.amirun.kalahagame.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.amirun.kalahagame.constants.GameConstants.*;

@Service
public class GameServiceImpl implements GameService{

    private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Override
    public KalahaGame move(KalahaGame game, Character pitId) throws InvalidMoveException {
        logger.debug("Move START - gameId:{}, board:{}, turn:{}, pit:{}", game.getGameId(), game.getGamePits(), game.getNextTurn(), pitId);

        int index = getPitIndex(game.getNextTurn(), pitId);

        int[] pits = game.getGamePits();
        int stones = pits[index];

        //Invalid pit if there are no stones
        if(stones < 1) {
            logger.error("No stones in selected pit: {}", pitId);
            throw new InvalidMoveException("No stones in selected pit.");
        }
        pits[index] = EMPTY_PIT; //empty the pit from where stones are picked
        int currentIndex = index;


        while(stones > EMPTY_PIT) {
            currentIndex = (++currentIndex) % TOTAL_PIT_COUNT; //increment pit index and loop
            if(currentIndex != getOpponentBigPit(game.getNextTurn())) {
                pits[currentIndex]++;
                stones--;
            }
        }
        logger.debug("Move SOWED - gameId:{}, board:{}, turn:{}, pit:{}", game.getGameId(), game.getGamePits(), game.getNextTurn(), pitId);

        capture(game.getGamePits(), currentIndex, game.getNextTurn());

        if (isGameOver(game.getGamePits())) {
            game.setIsGameEnded(true);
            game.setWinner(getWinner(game.getGamePits()));
        } else {
            determineNextTurn(game, currentIndex);
        }

        logger.debug("Move END - gameId:{}, board:{}, turn:{}, pit:{}", game.getGameId(), game.getGamePits(), game.getNextTurn(), pitId);
        return game;
    }



    public void capture(int[] pits, int currentIndex, Player currentPlayer){
        logger.debug("capture START - board:{}, currentIndex:{}, player:{}", pits, currentIndex, currentPlayer);
        if(isPlayerPit(currentIndex, currentPlayer)                 //check if move ended on current player side
                && pits[currentIndex] == 1                          //check if current pit has 1 pebble
                &&  pits[SMALL_PIT_COUNT - currentIndex] > EMPTY_PIT) {     //check if opposite pit has 0 pebbles

            int total = pits[currentIndex] + pits[SMALL_PIT_COUNT - currentIndex];
            pits[currentIndex] = EMPTY_PIT;
            pits[SMALL_PIT_COUNT - currentIndex] = EMPTY_PIT;
            pits[getPlayerBigPit(currentPlayer)] += total;
        }
        logger.debug("capture END - board:{}, currentIndex:{}, player:{}", pits, currentIndex, currentPlayer);
    }

    private boolean isPlayerPit(int currentIndex, Player currentPlayer) {
        return Player.PLAYER_1.equals(currentPlayer)
                ? currentIndex >= PLAYER1_PIT_START_INDEX_INCL && currentIndex < PLAYER1_PIT_END_INDEX_EXCL
                : currentIndex >= PLAYER2_PIT_START_INDEX_INCL && currentIndex < PLAYER2_PIT_END_INDEX_EXCL;

    }

    /**
     * Checks if turn switches to next player. If current player lands in own big pit, no change in turn.
     *
     * @param game game object
     * @param currentIndex index of the pit where last move ended
     */
    public void determineNextTurn(KalahaGame game, int currentIndex){
        if(!isBigPit(currentIndex)) {
            game.setNextTurn(game.getNextTurn().equals(Player.PLAYER_1)
                    ? Player.PLAYER_2
                    : Player.PLAYER_1);
        }
    }

    boolean isBigPit(int index) {
        return index == PLAYER1_BIG_PIT_INDEX || index == PLAYER2_BIG_PIT_INDEX;
    }
    /**
     *
     * @param player Player 1 or 2
     * @return Index of the big pit of provided player.
     */
    public int getPlayerBigPit(Player player) {
        return Player.PLAYER_1.equals(player) ? PLAYER1_BIG_PIT_INDEX : PLAYER2_BIG_PIT_INDEX;
    }

    /**
     *
     * @param player Player 1 or 2
     * @return Index of the big pit of opponent of provided player.
     */
    public int getOpponentBigPit(Player player) {
        return Player.PLAYER_1.equals(player) ? PLAYER2_BIG_PIT_INDEX : PLAYER1_BIG_PIT_INDEX;
    }


    public int getPitIndex(Player currentPlayer, Character pitId) {
        //convert incoming
        int subtractionfactor = Player.PLAYER_1.equals(currentPlayer)? FIRST_PIT_CHAR : (FIRST_PIT_CHAR - PLAYER2_PIT_START_INDEX_INCL);
        int index = pitId - subtractionfactor;
        logger.debug("getPitIndex - subtraction_factor:{}, pitIndex:{}", subtractionfactor, index);
        return index;
    }

    public boolean isGameOver(int[] pits) {
        int player1Sum = sumPits(pits, PLAYER1_PIT_START_INDEX_INCL, PLAYER1_PIT_END_INDEX_EXCL);
        int player2Sum = sumPits(pits, PLAYER2_PIT_START_INDEX_INCL, PLAYER2_PIT_END_INDEX_EXCL);
        logger.debug("isGameOver - player1Sum:{}, player2Sum:{}", player1Sum, player2Sum);
        return player1Sum == EMPTY_PIT || player2Sum == EMPTY_PIT;
    }


    private int sumPits(int[] gamePits, int start, int end) {
        return Arrays.stream(gamePits, start, end).sum();
    }

    public Result getWinner(int[] pits) {
        int player1Sum = sumPits(pits, PLAYER1_PIT_START_INDEX_INCL, PLAYER1_PIT_END_INDEX_EXCL +1 );
        int player2Sum = sumPits(pits, PLAYER2_PIT_START_INDEX_INCL, PLAYER2_PIT_END_INDEX_EXCL +1 );
        logger.debug("getWinner - player1Sum:{}, player2Sum:{}", player1Sum, player2Sum);
        if (player1Sum == player2Sum) {
            return Result.TIE;
        } else  {
            return player1Sum > player2Sum ? Result.PLAYER_1 : Result.PLAYER_2;
        }
    }
}
