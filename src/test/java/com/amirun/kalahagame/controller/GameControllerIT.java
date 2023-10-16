package com.amirun.kalahagame.controller;

import com.amirun.kalahagame.KalahaGameApplicationTests;
import com.amirun.kalahagame.model.KalahaGame;
import com.amirun.kalahagame.model.Player;
import com.amirun.kalahagame.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = KalahaGameApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GameControllerIT {

    private static final String PATH = "/game/";
    private static final String PATH_PLAY_GAME = "playGame/%s/playPit/%s";
    private static final String GAME_ID_JSONPATH = "$.gameId";
    private static final String PLAYER1_PITS_JSONPATH = "$.player1Pits";
    private static final String PLAYER2_PITS_JSONPATH = "$.player2Pits";
    private static final String PLAYER1_BIG_PIT_JSONPATH = "$.player1BigPit";
    private static final String PLAYER2_BIG_PIT_JSONPATH = "$.player2BigPit";
    private static final String IS_GAME_ENDED_JSONPATH = "$.isGameEnded";
    private static final String NEXT_TURN_JSONPATH = "$.nextTurn";
    private static final String MESSAGE_JSONPATH = "$.message";
    private static final String WINNER_JSONPATH = "$.winner";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getNewBoard() throws Exception {
        mockMvc.perform(get(PATH + "new").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(GAME_ID_JSONPATH).isNotEmpty())
                .andExpect(jsonPath(PLAYER1_PITS_JSONPATH).isArray())
                .andExpect(jsonPath(PLAYER1_PITS_JSONPATH, hasSize(6)))
                .andExpect(jsonPath(PLAYER2_PITS_JSONPATH).isArray())
                .andExpect(jsonPath(PLAYER2_PITS_JSONPATH, hasSize(6)))
                .andExpect(jsonPath(PLAYER1_BIG_PIT_JSONPATH).value(0))
                .andExpect(jsonPath(PLAYER2_BIG_PIT_JSONPATH).value(0))
                .andExpect(jsonPath(IS_GAME_ENDED_JSONPATH).value(Boolean.FALSE))
                .andExpect(jsonPath(NEXT_TURN_JSONPATH).value("PLAYER_1"))
                .andDo(print());
    }

    @Test
    void getGame_notFound() throws Exception {
        mockMvc.perform(get(PATH+"random-fake-id").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(MESSAGE_JSONPATH).value("Invalid Id: random-fake-id"))
                .andDo(print());
    }

    @Test
    void playGame_InvalidMove() throws Exception {
        String url = String.format(PATH+PATH_PLAY_GAME, "random-fake-id", 'Z');//invalid pit
        mockMvc.perform(patch(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(MESSAGE_JSONPATH).value("Choose a pit between A - F."))
                .andDo(print());
    }


    @Nested
    class NestedTests {

        private KalahaGame game;

        @BeforeEach
        public void setup() {
            game = repository.save(KalahaGame.builder()
                    .nextTurn(Player.PLAYER_1)
                    .isGameEnded(false)
                    .gamePits(new int[] {6,6,6,6,6,6,0,6,6,6,6,6,6,0})
                    .gameId("SAMPLE_ID")
                    .build());
        }

        @Test
        void getGame() throws Exception {
            String id = this.game.getGameId();
            mockMvc.perform(get(PATH+id).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(GAME_ID_JSONPATH).value(id))
                    .andExpect(jsonPath(PLAYER1_PITS_JSONPATH ).isArray())
                    .andExpect(jsonPath(PLAYER1_PITS_JSONPATH , hasSize(6)))
                    .andExpect(jsonPath(PLAYER2_PITS_JSONPATH ).isArray())
                    .andExpect(jsonPath(PLAYER2_PITS_JSONPATH , hasSize(6)))
                    .andExpect(jsonPath(PLAYER1_BIG_PIT_JSONPATH).value(0))
                    .andExpect(jsonPath(PLAYER2_BIG_PIT_JSONPATH).value(0))
                    .andExpect(jsonPath(IS_GAME_ENDED_JSONPATH).value(Boolean.FALSE))
                    .andExpect(jsonPath(NEXT_TURN_JSONPATH).value("PLAYER_1"))
                    .andDo(print());
        }

        @Test
        void playGame_TestWinner() throws Exception {
            KalahaGame localGame = repository.save(KalahaGame.builder()
                    .nextTurn(Player.PLAYER_1)
                    .isGameEnded(false)
                    .gamePits(new int[]{0, 0, 0, 0, 0, 1, 39, 1, 1, 5, 1, 1, 1, 22})
                    .build());

            String url = String.format(PATH+PATH_PLAY_GAME, localGame.getGameId(), 'F');
            mockMvc.perform(patch(url).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(IS_GAME_ENDED_JSONPATH).value(Boolean.TRUE))
                    .andExpect(jsonPath(WINNER_JSONPATH).value("PLAYER_1"))
                    .andDo(print());
        }
    }
}