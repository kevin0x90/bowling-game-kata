package org.k3v1n.bowling;

public class GameEndException extends RuntimeException {
    public GameEndException() {
        super("Game already ended, rolls are not allowed");
    }
}
