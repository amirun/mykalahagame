package com.amirun.kalahagame.repository;

import com.amirun.kalahagame.model.KalahaGame;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepository extends MongoRepository<KalahaGame, String> {
}
