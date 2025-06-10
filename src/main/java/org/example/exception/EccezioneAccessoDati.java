package org.example.exception;

public class EccezioneAccessoDati extends RuntimeException {
    public EccezioneAccessoDati(String message) {
        super(message);
    }
    public EccezioneAccessoDati(String message, Throwable cause) {
        super(message, cause);
    }
}