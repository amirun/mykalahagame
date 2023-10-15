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
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getNewBoard() throws Exception {
        mockMvc.perform(get("/game/new").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").isNotEmpty())
                .andExpect(jsonPath("$.player1Pits" ).isArray())
                .andExpect(jsonPath("$.player1Pits" , hasSize(6)))
                .andExpect(jsonPath("$.player2Pits" ).isArray())
                .andExpect(jsonPath("$.player2Pits" , hasSize(6)))
                .andExpect(jsonPath("$.player1BigPit").value(0))
                .andExpect(jsonPath("$.player2BigPit").value(0))
                .andExpect(jsonPath("$.isGameEnded").value(Boolean.FALSE))
                .andExpect(jsonPath("$.nextTurn").value("PLAYER_1"))
                .andDo(print());
    }

    @Test
    void getGame_notFound() throws Exception {
        mockMvc.perform(get("/game/random-fake-id").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invalid Id: random-fake-id"))
                .andDo(print());
    }

    @Test
    void playGame_InvalidMove() throws Exception {
        String url = String.format("/game/playGame/%s/playPit/%s", "random-fake-id", 'Z');//invalid pit
        mockMvc.perform(patch(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Choose a pit between A - F."))
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
            mockMvc.perform(get("/game/"+id).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gameId").value(id))
                    .andExpect(jsonPath("$.player1Pits" ).isArray())
                    .andExpect(jsonPath("$.player1Pits" , hasSize(6)))
                    .andExpect(jsonPath("$.player2Pits" ).isArray())
                    .andExpect(jsonPath("$.player2Pits" , hasSize(6)))
                    .andExpect(jsonPath("$.player1BigPit").value(0))
                    .andExpect(jsonPath("$.player2BigPit").value(0))
                    .andExpect(jsonPath("$.isGameEnded").value(Boolean.FALSE))
                    .andExpect(jsonPath("$.nextTurn").value("PLAYER_1"))
                    .andDo(print());
        }

        @Test
        void playGame_TestWinner() throws Exception {
            KalahaGame localGame = repository.save(KalahaGame.builder()
                    .nextTurn(Player.PLAYER_1)
                    .isGameEnded(false)
                    .gamePits(new int[]{0, 0, 0, 0, 0, 1, 39, 1, 1, 5, 1, 1, 1, 22})
                    .build());

            String url = String.format("/game/playGame/%s/playPit/%s", localGame.getGameId(), 'F');
            mockMvc.perform(patch(url).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isGameEnded").value(Boolean.TRUE))
                    .andExpect(jsonPath("$.winner").value("PLAYER_1"))
                    .andDo(print());
        }
    }
}