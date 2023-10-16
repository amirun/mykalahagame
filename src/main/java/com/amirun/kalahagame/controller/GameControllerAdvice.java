package com.amirun.kalahagame.controller;

import com.amirun.kalahagame.dto.ErrorDTO;
import com.amirun.kalahagame.exception.GameNotFoundException;
import com.amirun.kalahagame.exception.InvalidMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = GameController.class)
public class GameControllerAdvice {

    @ExceptionHandler(InvalidMoveException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorDTO> handleDuplicateRecipe(InvalidMoveException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorDTO(HttpStatus.CONFLICT.value(), e.getMessage())
        );
    }

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDTO> handleDuplicateRecipe(GameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage())
        );
    }
}
