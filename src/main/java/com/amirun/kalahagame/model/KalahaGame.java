package com.amirun.kalahagame.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
@ToString
public class KalahaGame {

    @Id
    private String gameId;

    @NotNull
    private int[] gamePits;

    private Boolean isGameEnded;

    @NotNull
    private Player nextTurn;

    private Result winner;
}
