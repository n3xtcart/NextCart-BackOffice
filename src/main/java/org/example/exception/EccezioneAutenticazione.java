package org.example.exception;

public class EccezioneAutenticazione extends Exception {
    public EccezioneAutenticazione(String message) {
        super(message);
    }
    public EccezioneAutenticazione(String message, Throwable cause) {
        super(message, cause);
    }
}